package backend.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private final Map<String, Node> nodes = new HashMap<>();
    private final Map<String, List<String>> adjacency = new HashMap<>();

    public void addNode(Node node) {
        if (node == null || node.getId() == null) {
            return;
        }
        nodes.put(node.getId(), node);
        adjacency.putIfAbsent(node.getId(), new ArrayList<>());
    }

    public void addEdge(String fromId, String toId) {
        if (fromId == null || toId == null) {
            return;
        }
        if (!adjacency.containsKey(fromId) || !adjacency.containsKey(toId)) {
            return;
        }
        adjacency.get(fromId).add(toId);
        adjacency.get(toId).add(fromId);
    }

    public Node getNode(String id) {
        return nodes.get(id);
    }

    public List<String> getNeighbors(String id) {
        return adjacency.getOrDefault(id, Collections.emptyList());
    }

    public Collection<Node> getAllNodes() {
        return nodes.values();
    }

    public double haversine(double lat1, double lng1, double lat2, double lng2) {
        double radiusMeters = 6371000d;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return radiusMeters * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
