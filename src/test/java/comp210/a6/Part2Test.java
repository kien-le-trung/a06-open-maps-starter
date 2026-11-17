package comp210.a6;

import bridges.base.GraphAdjList;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Your tests for Part 2. Write at least three, then right-click this file in
 * IntelliJ and choose Run 'Part2Test'. The README's "Writing your tests"
 * section has ideas.
 */
class Part2Test {

    /** Vertices 0 to n - 1 with the given edges, each {from, to, length}. */
    static GraphAdjList<Integer, String, Double> graph(int n, double[][] edges) {
        GraphAdjList<Integer, String, Double> graph = new GraphAdjList<>();
        for (int i = 0; i < n; i++) {
            graph.addVertex(i, "v" + i);
        }
        for (double[] e : edges) {
            graph.addEdge((int) e[0], (int) e[1], e[2]);
        }
        return graph;
    }

    // Write your tests here. Each one is a method marked @Test, for example:
    //
    // @Test
    // void dijkstraGivesTheSourceZero() {
    //     GraphAdjList<Integer, String, Double> g = graph(2, new double[][] {{0, 1, 5}});
    //     HashMap<Integer, Double> distance = new HashMap<>();
    //     RoutePlanner.dijkstra(g, 0, distance, new HashMap<>());
    //     assertEquals(0.0, distance.get(0));
    // }

    @Test
    void pairCompareTest() {
        Pair<Integer, Double> smaller = new Pair<>(0, 0.25);
        Pair<Integer, Double> larger = new Pair<>(1, 0.75);

        assertTrue(smaller.compareTo(larger) < 0);
        assertTrue(larger.compareTo(smaller) > 0);
        assertEquals(0, smaller.compareTo(new Pair<>(2, 0.25)));
    }

    @Test
    void dijkstraTest() {
        GraphAdjList<Integer, String, Double> graph = new GraphAdjList<>();
        graph.addVertex(0, "Source");
        graph.addVertex(1, "Node 1");
        graph.addVertex(2, "Node 2");

        graph.addEdge(0, 2, 10.0);
        graph.addEdge(0, 1, 2.0);
        graph.addEdge(1, 2, 3.0);

        HashMap<Integer, Double> distance = new HashMap<>();
        HashMap<Integer, Integer> parent = new HashMap<>();

        RoutePlanner.dijkstra(graph, 0, distance, parent);

        assertEquals(0.0, distance.get(0).doubleValue());
        assertEquals(2.0, distance.get(1).doubleValue());
        assertEquals(5.0, distance.get(2).doubleValue());
        assertEquals(0, parent.get(1).intValue());
        assertEquals(1, parent.get(2).intValue());
        assertFalse(parent.containsKey(0));
    }

    @Test
    void negativeEdgeGraphTest() {
        GraphAdjList<Integer, String, Double> graph =
                RoutePlanner.negativeEdgeGraph();

        HashMap<Integer, Double> distance = new HashMap<>();
        HashMap<Integer, Integer> parent = new HashMap<>();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> RoutePlanner.dijkstra(graph, 0, distance, parent));
        assertTrue(exception.getMessage().startsWith("Check 2"));
    }
}
