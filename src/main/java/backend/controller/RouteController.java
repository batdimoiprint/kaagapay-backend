package backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import backend.model.Graph;
import backend.model.Node;
import backend.service.DijkstraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("/api/route")
@CrossOrigin(origins = "*")
@Tag(name = "Route API", description = "Dijkstra shortest path for Brgy. Capri Novaliches")
public class RouteController {

    @Autowired
    private DijkstraService dijkstraService;

    private Graph graph;

    private static final String SOURCE_ID = "node_8";

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

    @Operation(
        summary = "Get shortest path",
        description = "Input a destination lat/lng. The source is always fixed at node_8 (Brgy. Capri). Returns the shortest path as a list of coordinates."
    )
    @PostMapping(consumes = org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Map<String, Object> getRoute(
        @io.swagger.v3.oas.annotations.Parameter(description = "Latitude of destination", example = "14.7185")
        @RequestParam double lat,
        @io.swagger.v3.oas.annotations.Parameter(description = "Longitude of destination", example = "121.0295")
        @RequestParam double lng,
        Authentication authentication
    ) {
        // Authenticated user information available from JwtAuthenticationFilter
        String username = authentication != null ? authentication.getName() : "Anonymous";
        System.out.println("Route requested by user: " + username);

        // Find nearest node to the input lat/lng
        String destId = findNearestNode(lat, lng);

        Map<String, Object> response = new HashMap<>();

        if (destId == null || destId.equals(SOURCE_ID)) {
            response.put("found", false);
            response.put("message", "No valid destination node found");
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

        response.put("found", true);
        response.put("nearestDestinationNode", destId);
        response.put("path", coords);
        return response;
    }

    @Operation(
        summary = "Get all nodes",
        description = "Returns all nodes in the graph with their ID, lat, and lng. Use this to find valid destination node IDs."
    )
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

    @Operation(
        summary = "Find nearest node to coordinates",
        description = "Input any lat/lng and get the nearest graph node back. Useful for debugging which node your destination maps to."
    )
    @GetMapping("/nearest")
    public Map<String, Object> getNearestNode(
        @io.swagger.v3.oas.annotations.Parameter(description = "Latitude", example = "14.7185")
        @RequestParam double lat,
        @io.swagger.v3.oas.annotations.Parameter(description = "Longitude", example = "121.0295")
        @RequestParam double lng
    ) {
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

    // Finds the nearest node in the graph to a given lat/lng
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