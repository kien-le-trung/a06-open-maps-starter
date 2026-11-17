package comp210.a6;

import bridges.base.Edge;
import bridges.base.GraphAdjList;
import bridges.base.SLelement;

import java.util.*;

/**
 * Route finding on a road graph. Vertex keys are ints, and each edge holds
 * the length of a road segment in meters. The methods work on any graph with
 * those key and edge types, whatever the vertices hold, so you can test them
 * on small graphs you build by hand.
 *
 * Part 1: bfs and routeTo.
 * Part 2: dijkstra, with its two invariant checks, and negativeEdgeGraph.
 *
 * Walk a vertex's edges with graph.getAdjacencyList(vertex), the same
 * getValue() / getNext() loop as a linked list.
 */
public class RoutePlanner {

    // ------------------------------------------------------------------
    // Part 1: fewest intersections
    // ------------------------------------------------------------------

    /**
     * Breadth-first search from source. Fills steps with the number of edges
     * on the shortest route (by edge count) to each vertex BFS reaches, and
     * parent with the vertex just before each one on that route. A vertex BFS
     * never reaches gets no entry in steps. The source has no parent.
     */
    public static void bfs(GraphAdjList<Integer, ?, Double> graph, int source,
                           HashMap<Integer, Integer> steps, HashMap<Integer, Integer> parent) {
        Deque<Integer> queue = new ArrayDeque<>();
        queue.add(source);
        steps.put(source, 0);

        while (!queue.isEmpty()) {
            int current = queue.removeFirst();
            SLelement<Edge<Integer, Double>> node = graph.getAdjacencyList(current);
            while (node != null) {
                int neighbor = node.getValue().getTo();
                if (!steps.containsKey(neighbor)) {
                    steps.put(neighbor, steps.get(current) + 1);
                    parent.put(neighbor, current);
                    queue.addLast(neighbor);
                }
                node = node.getNext();
            }
        }
        //throw new UnsupportedOperationException("Part 1: write RoutePlanner.bfs");
    }

    /**
     * Returns the route from source to destination as a list of vertices,
     * source first and destination last, by following parent links back from
     * destination. Returns an empty list when destination was not reached.
     */
    public static List<Integer> routeTo(HashMap<Integer, Integer> parent, int source, int destination) {
        List<Integer> route = new ArrayList<>();

        if (destination != source && !parent.containsKey(destination)) {
            return route;
        }

        // travel upwards till null
        Integer current = destination;
        while (current != null) {
            route.add(current);
            current = parent.get(current);
        }
        Collections.reverse(route);
        return route;
        //throw new UnsupportedOperationException("Part 1: write RoutePlanner.routeTo");
    }

    // ------------------------------------------------------------------
    // Part 2: fewest meters
    // ------------------------------------------------------------------

    /**
     * Dijkstra's shortest path algorithm. Fills distance with the length of
     * the shortest route from source to each vertex, or Double.MAX_VALUE if
     * there is none, and parent with the vertex just before each one on its
     * route. The source has no parent.
     *
     * The loop keeps the settled-set invariant: once a vertex is settled, its
     * distance is final. Two checks enforce it as the loop runs:
     *
     *   Check 1. Vertices are settled in order of distance, so each settled
     *            distance is at least the one settled before it.
     *   Check 2. Relaxing an edge never lowers the distance of a vertex that
     *            is already settled.
     *
     * @throws IllegalStateException when either check fails, with a message
     *         that starts with "Check 1" or "Check 2" and names the vertex
     */
    public static void dijkstra(GraphAdjList<Integer, ?, Double> graph, int source,
                                HashMap<Integer, Double> distance, HashMap<Integer, Integer> parent) {
        Queue<Pair<Integer,Double>> queue = new PriorityQueue<>();
        HashSet<Integer> settled = new HashSet<>();
        double lastDistance = Double.NEGATIVE_INFINITY;

        for (Integer from : graph.getVertices().keySet()) {
            distance.put(from, Double.MAX_VALUE);
        }
        distance.put(source, 0.0);
        queue.add(new Pair<>(source, 0.0));

        while (!queue.isEmpty()) {
            Pair<Integer, Double> curr_pair = queue.poll();
            int current = curr_pair.getKey();
            double currentDistance = curr_pair.getValue();
            if (currentDistance > distance.get(current)) {
                continue; // stale check
            }

            if (settled.contains(current)) {
                continue;
            }

            if (currentDistance < lastDistance) {
                throw new IllegalStateException("Check 1: " + current + " is settled with distance " + currentDistance + " after a vertex with distance " + lastDistance);
            }

            settled.add(current);
            lastDistance = currentDistance;
            SLelement<Edge<Integer, Double>> node = graph.getAdjacencyList(curr_pair.getKey());
            
            while (node != null) {
                Edge<Integer, Double> edge = node.getValue();
                int neighbor = edge.getTo();
                double candidate = currentDistance + edge.getEdgeData();
                if (candidate < distance.get(neighbor)) {
                    if (settled.contains(neighbor)) {
                        throw new IllegalStateException("Check 2: " + neighbor + " is already settled but a shorter distance is found");
                    }
                    distance.put(neighbor, candidate);
                    parent.put(neighbor, current);
                    queue.add(new Pair<>(neighbor, candidate));
                }
                node = node.getNext();
            }
        }

        //throw new UnsupportedOperationException("Part 2: write RoutePlanner.dijkstra");
    }

    /**
     * Returns a small graph with one negative edge on which dijkstra fails
     * check 2. Running dijkstra from vertex 0 must settle some vertex and then
     * find a shorter route to it through the negative edge.
     */
    public static GraphAdjList<Integer, String, Double> negativeEdgeGraph() {
        GraphAdjList<Integer, String, Double> graph = new GraphAdjList<>();
        graph.addVertex(0, "Source");
        graph.addVertex(1, "Node 1");
        graph.addVertex(2, "Node 2");

        graph.addEdge(0, 1, 2.0);
        graph.addEdge(0, 2, 5.0);
        graph.addEdge(2, 1, -4.0);

        return graph;
        // throw new UnsupportedOperationException("Part 2: write RoutePlanner.negativeEdgeGraph");
    }
}
