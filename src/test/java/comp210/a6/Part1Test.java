package comp210.a6;

import bridges.base.GraphAdjList;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Run these when you finish Part 1. Right-click this file in IntelliJ and
 * choose Run 'Part1Test'.
 *
 * These are a sample of what Gradescope runs, not all of it. Gradescope also
 * checks bfs against random graphs and times it on 50,000 vertices.
 */
class Part1Test {

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

    @Test
    void bfsCountsHopsAlongALine() {
        GraphAdjList<Integer, String, Double> g = graph(4, new double[][] {{0, 1, 5}, {1, 2, 5}, {2, 3, 5}});
        HashMap<Integer, Integer> hops = new HashMap<>();
        HashMap<Integer, Integer> parent = new HashMap<>();
        RoutePlanner.bfs(g, 0, hops, parent);
        assertEquals(Map.of(0, 0, 1, 1, 2, 2, 3, 3), hops);
        assertEquals(Map.of(1, 0, 2, 1, 3, 2), parent, "the source has no parent");
    }

    @Test
    void bfsIgnoresEdgeLengths() {
        // 0 -> 1 -> 2 -> 3 is short in meters; 0 -> 4 -> 3 has fewer hops.
        GraphAdjList<Integer, String, Double> g = graph(5, new double[][] {
            {0, 1, 1}, {1, 2, 1}, {2, 3, 1}, {0, 4, 500}, {4, 3, 500},
        });
        HashMap<Integer, Integer> hops = new HashMap<>();
        HashMap<Integer, Integer> parent = new HashMap<>();
        RoutePlanner.bfs(g, 0, hops, parent);
        assertEquals(2, hops.get(3), "0 -> 4 -> 3 is 2 hops");
        assertEquals(4, parent.get(3));
    }

    @Test
    void bfsFollowsEdgeDirection() {
        // 2 -> 0 is one-way, so 2 cannot be reached from 0.
        GraphAdjList<Integer, String, Double> g = graph(3, new double[][] {{0, 1, 1}, {1, 0, 1}, {2, 0, 1}});
        HashMap<Integer, Integer> hops = new HashMap<>();
        HashMap<Integer, Integer> parent = new HashMap<>();
        RoutePlanner.bfs(g, 0, hops, parent);
        assertEquals(Map.of(0, 0, 1, 1), hops, "a vertex BFS never reaches gets no entry");
        assertFalse(parent.containsKey(2));
    }

    @Test
    void routeToFollowsParentsBack() {
        HashMap<Integer, Integer> parent = new HashMap<>(Map.of(1, 0, 2, 1, 3, 2));
        assertEquals(List.of(0, 1, 2, 3), RoutePlanner.routeTo(parent, 0, 3), "source first, destination last");
        assertEquals(List.of(0), RoutePlanner.routeTo(parent, 0, 0), "from the source to itself");
    }

    @Test
    void routeToAnUnreachedVertexIsEmpty() {
        HashMap<Integer, Integer> parent = new HashMap<>(Map.of(1, 0));
        List<Integer> route = RoutePlanner.routeTo(parent, 0, 5);
        assertNotNull(route, "return an empty list, not null");
        assertTrue(route.isEmpty());
    }
}
