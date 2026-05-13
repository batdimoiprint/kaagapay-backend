package backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import backend.model.Graph;
import backend.model.Node;
import backend.service.BoundaryService;
import backend.service.DijkstraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("/api/route")
@CrossOrigin(origins = "*")
@Tag(name = "Route API", description = "Dijkstra shortest path for Brgy. Capri Novaliches")
public class RouteController {

    @Autowired
    private DijkstraService dijkstraService;

    @Autowired
    private BoundaryService boundaryService;

    private Graph graph;

    private static final String SOURCE_ID = "node_721";

    public static class RouteRequest {
        @Schema(description = "Latitude of destination", example = "14.716432646016036")
        private double lat;

        @Schema(description = "Longitude of destination", example = "121.030132611756")
        private double lng;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }

    @PostConstruct
    public void buildGraph() throws Exception {
        graph = new Graph();
        ObjectMapper mapper = new ObjectMapper();

        InputStream nodesStream = getClass().getResourceAsStream("/nodes.json");
        List<Map<String, Object>> nodeList = mapper.readValue(nodesStream, List.class);
        for (Map<String, Object> n : nodeList) {
            String id = (String) n.get("id");
            double lat = ((Number) n.get("lat")).doubleValue();
            double lng = ((Number) n.get("lng")).doubleValue();
            graph.addNode(new Node(id, lat, lng));
        }

        InputStream edgesStream = getClass().getResourceAsStream("/edges.json");
        List<Map<String, Object>> edgeList = mapper.readValue(edgesStream, List.class);
        for (Map<String, Object> e : edgeList) {
            graph.addEdge((String) e.get("from"), (String) e.get("to"));
        }

        System.out.println("Loaded: " + nodeList.size()
                + " nodes, " + edgeList.size() + " edges");
    }

    @Operation(summary = "Get shortest path", description = "Submit destination lat and lng as form fields. Source is always fixed at node_8.", requestBody = @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE, schema = @Schema(implementation = RouteRequest.class))))
    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Map<String, Object> getRoute(
            @ModelAttribute RouteRequest request,
            @Parameter(hidden = true) Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "Anonymous";
        System.out.println("Route requested by user: " + username);

        Map<String, Object> response = new HashMap<>();

        // 1. Boundary check — is the point inside Brgy. Sta. Lucia?
        if (!boundaryService.isWithinBoundary(request.getLat(), request.getLng())) {
            response.put("found", false);
            response.put("outsideBoundary", true);
            response.put("message", "Location is outside Barangay Sta. Lucia coverage area");
            response.put("path", Collections.emptyList());
            return response;
        }

        String destId = findNearestNode(request.getLat(), request.getLng());

        if (destId == null || destId.equals(SOURCE_ID)) {
            response.put("found", false);
            response.put("message", "No valid destination node found");
            response.put("path", Collections.emptyList());
            return response;
        }

        // 2. Snap distance check — is the nearest node within 150m?
        Node nearestNode = graph.getNode(destId);
        double snapDistance = graph.haversine(
                request.getLat(), request.getLng(),
                nearestNode.getLat(), nearestNode.getLng());
        if (snapDistance > 150) {
            response.put("found", false);
            response.put("outsideBoundary", true);
            response.put("message", "No mapped road found near your location");
            response.put("path", Collections.emptyList());
            return response;
        }

        List<Node> path = dijkstraService.findPath(graph, SOURCE_ID, destId);

        if (path.isEmpty()) {
            response.put("found", false);
            response.put("message", "No path found to destination");
            response.put("path", Collections.emptyList());
            return response;
        }

        List<List<Double>> coords = new ArrayList<>();
        for (Node n : path) {
            coords.add(Arrays.asList(n.getLat(), n.getLng()));
        }

        // Calculate total real-world distance along the path
        double totalMeters = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            Node a = path.get(i);
            Node b = path.get(i + 1);
            totalMeters += graph.haversine(a.getLat(), a.getLng(), b.getLat(), b.getLng());
        }

        response.put("found", true);
        response.put("nearestDestinationNode", destId);
        response.put("path", coords);
        response.put("distanceMeters", Math.round(totalMeters * 100.0) / 100.0);
        if (totalMeters >= 1000) {
            response.put("distance", String.format("Distance from Brgy Hall: %.2f km", totalMeters / 1000.0));
        } else {
            response.put("distance", String.format("Distance from Brgy Hall: %.2f meters", totalMeters));
        }
        return response;
    }

    @Operation(summary = "Check if coordinates are inside Brgy. Sta. Lucia",
            description = "Returns isOutsideBarangay true/false. Use this for live location checks on the submit page.")
    @GetMapping("/boundary-check")
    public Map<String, Object> checkBoundary(
            @Parameter(description = "Latitude", example = "14.7064", required = true) @RequestParam double lat,
            @Parameter(description = "Longitude", example = "121.0509", required = true) @RequestParam double lng) {
        boolean inside = boundaryService.isWithinBoundary(lat, lng);
        Map<String, Object> result = new HashMap<>();
        result.put("isOutsideBarangay", !inside);
        result.put("message", inside
                ? "Current Location is within Barangay Sta. Lucia"
                : "The GPS location attached to this complaint is outside Barangay Sta. Lucia.");
        return result;
    }

    @Operation(summary = "Get all nodes", description = "Returns all nodes in the graph with their ID, lat, and lng.")
    @GetMapping("/nodes")
    public List<Map<String, Object>> getNodes() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Node n : graph.getAllNodes()) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("id", n.getId());
            entry.put("lat", n.getLat());
            entry.put("lng", n.getLng());
            result.add(entry);
        }
        return result;
    }

    @Operation(summary = "Find nearest node to coordinates", description = "Input any lat/lng and get the nearest graph node back.")
    @GetMapping("/nearest")
    public Map<String, Object> getNearestNode(
            @Parameter(description = "Latitude", example = "14.7185", required = true) @RequestParam double lat,
            @Parameter(description = "Longitude", example = "121.0295", required = true) @RequestParam double lng) {
        String nearestId = findNearestNode(lat, lng);
        Node n = graph.getNode(nearestId);
        Map<String, Object> result = new HashMap<>();
        result.put("id", n.getId());
        result.put("lat", n.getLat());
        result.put("lng", n.getLng());
        result.put("distanceFromInputMeters",
                graph.haversine(lat, lng, n.getLat(), n.getLng()));
        return result;
    }

    private String findNearestNode(double lat, double lng) {
        String nearestId = null;
        double minDist = Double.MAX_VALUE;
        for (Node n : graph.getAllNodes()) {
            double dist = graph.haversine(lat, lng, n.getLat(), n.getLng());
            if (dist < minDist) {
                minDist = dist;
                nearestId = n.getId();
            }
        }
        return nearestId;
    }
}