package backend.service;

import backend.model.Graph;
import backend.model.Node;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

@Service
public class DijkstraService {

    private static final double INF = Double.MAX_VALUE;

    public List<Node> findPath(Graph graph, String sourceId, String destId) {
        if (graph == null || sourceId == null || destId == null) {
            return Collections.emptyList();
        }

        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();

        for (Node node : graph.getAllNodes()) {
            distances.put(node.getId(), INF);
        }

        if (!distances.containsKey(sourceId) || !distances.containsKey(destId)) {
            return Collections.emptyList();
        }

        distances.put(sourceId, 0d);

        PriorityQueue<NodeDistance> queue = new PriorityQueue<>(
            Comparator.comparingDouble(item -> item.distance)
        );
        queue.add(new NodeDistance(sourceId, 0d));

        Set<String> settled = new HashSet<>();

        while (!queue.isEmpty()) {
            NodeDistance current = queue.poll();
            if (settled.contains(current.nodeId)) {
                continue;
            }
            settled.add(current.nodeId);

            if (current.nodeId.equals(destId)) {
                break;
            }

            Node currentNode = graph.getNode(current.nodeId);
            if (currentNode == null) {
                continue;
            }

            for (String neighborId : graph.getNeighbors(current.nodeId)) {
                if (settled.contains(neighborId)) {
                    continue;
                }
                Node neighborNode = graph.getNode(neighborId);
                if (neighborNode == null) {
                    continue;
                }

                double weight = graph.haversine(
                    currentNode.getLat(),
                    currentNode.getLng(),
                    neighborNode.getLat(),
                    neighborNode.getLng()
                );
                double newDist = distances.get(current.nodeId) + weight;

                if (newDist < distances.getOrDefault(neighborId, INF)) {
                    distances.put(neighborId, newDist);
                    previous.put(neighborId, current.nodeId);
                    queue.add(new NodeDistance(neighborId, newDist));
                }
            }
        }

        return buildPath(graph, destId, previous, distances);
    }

    private List<Node> buildPath(
        Graph graph,
        String destId,
        Map<String, String> previous,
        Map<String, Double> distances
    ) {
        if (!distances.containsKey(destId) || distances.get(destId) == INF) {
            return Collections.emptyList();
        }

        LinkedList<Node> path = new LinkedList<>();
        String currentId = destId;
        while (currentId != null) {
            Node node = graph.getNode(currentId);
            if (node == null) {
                return Collections.emptyList();
            }
            path.addFirst(node);
            currentId = previous.get(currentId);
        }

        return path;
    }

    private static class NodeDistance {
        private final String nodeId;
        private final double distance;

        private NodeDistance(String nodeId, double distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }
    }
}
