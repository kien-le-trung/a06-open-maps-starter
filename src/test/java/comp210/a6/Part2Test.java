package comp210.a6;

import bridges.base.GraphAdjList;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Run these when you finish Part 2. Right-click this file in IntelliJ and
 * choose Run 'Part2Test'.
 *
 * These are a sample of what Gradescope runs, not all of it. Gradescope also
 * checks dijkstra against random graphs, and counts how many times you walk
 * each vertex's edges to make sure stale pairs are skipped.
 */
class Part2Test {

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

    @Test
    void pairCompareToOrdersByDistance() {
        assertTrue(new Pair<>(1, 2.0).compareTo(new Pair<>(2, 5.0)) < 0, "2.0 before 5.0");
        assertTrue(new Pair<>(1, 5.0).compareTo(new Pair<>(2, 2.0)) > 0, "5.0 after 2.0");
        assertEquals(0, new Pair<>(1, 3.0).compareTo(new Pair<>(2, 3.0)), "a tie; the vertex plays no part");
        assertTrue(new Pair<>(1, 0.25).compareTo(new Pair<>(2, 0.75)) < 0, "0.25 before 0.75");
    }

    @Test
    void dijkstraTakesTheCheapRoute() {
        // 0 -> 3 costs 10 in one step; 0 -> 1 -> 2 -> 3 costs 3.
        GraphAdjList<Integer, String, Double> g = graph(4, new double[][] {
            {0, 3, 10}, {0, 1, 1}, {1, 2, 1}, {2, 3, 1},
        });
        HashMap<Integer, Double> distance = new HashMap<>();
        HashMap<Integer, Integer> parent = new HashMap<>();
        RoutePlanner.dijkstra(g, 0, distance, parent);
        assertEquals(Map.of(0, 0.0, 1, 1.0, 2, 2.0, 3, 3.0), distance);
        assertEquals(2, parent.get(3), "3 is reached through 2");
    }

    @Test
    void dijkstraMarksUnreachableVertices() {
        GraphAdjList<Integer, String, Double> g = graph(3, new double[][] {{0, 1, 2}, {2, 0, 1}});
        HashMap<Integer, Double> distance = new HashMap<>();
        RoutePlanner.dijkstra(g, 0, distance, new HashMap<>());
        assertEquals(Double.MAX_VALUE, distance.get(2), "2 -> 0 is one-way");
    }

    @Test
    void checkTwoCatchesANegativeEdge() {
        // 0 settles, then 1 (at 1), then 2 (at 2). The edge 2 -> 1 offers 1 a
        // distance of 0, but 1 is already settled.
        GraphAdjList<Integer, String, Double> g = graph(3, new double[][] {{0, 1, 1}, {0, 2, 2}, {2, 1, -2}});
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> RoutePlanner.dijkstra(g, 0, new HashMap<>(), new HashMap<>()));
        assertTrue(e.getMessage().startsWith("Check 2"), "message: " + e.getMessage());
    }

    @Test
    void yourNegativeEdgeGraphTripsCheckTwo() {
        GraphAdjList<Integer, String, Double> g = RoutePlanner.negativeEdgeGraph();
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> RoutePlanner.dijkstra(g, 0, new HashMap<>(), new HashMap<>()),
                "dijkstra from vertex 0 should fail a check on your graph");
        assertTrue(e.getMessage().startsWith("Check 2"), "it should be check 2. Message: " + e.getMessage());
    }
}
