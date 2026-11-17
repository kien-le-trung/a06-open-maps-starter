package comp210.a6;

import bridges.base.GraphAdjList;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Your tests for Part 1. Write at least three, then right-click this file in
 * IntelliJ and choose Run 'Part1Test'. The README's "Writing your tests"
 * section has ideas.
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

    // Write your tests here. Each one is a method marked @Test, for example:
    //
    // @Test
    // void bfsGivesTheSourceZeroSteps() {
    //     GraphAdjList<Integer, String, Double> g = graph(2, new double[][] {{0, 1, 5}});
    //     HashMap<Integer, Integer> steps = new HashMap<>();
    //     RoutePlanner.bfs(g, 0, steps, new HashMap<>());
    //     assertEquals(0, steps.get(0));
    // }

    //minimize edge, not length
    @Test
    void bfsTestOne() {
        GraphAdjList<Integer, String, Double> graph = new GraphAdjList<>();
        graph.addVertex(0, "Source");
        graph.addVertex(1, "Node 1");
        graph.addVertex(2, "Node 2");
        graph.addEdge(0, 2, 100.0);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 2, 1.0);

        HashMap<Integer, Integer> steps = new HashMap<>();
        HashMap<Integer, Integer> parent = new HashMap<>();
        RoutePlanner.bfs(graph, 0, steps, parent);

        assertEquals(1, steps.get(2).intValue());
        assertEquals(0, parent.get(2).intValue());
    }

    // nodes on a chain
    @Test
    void bfsTestTwo() {
        GraphAdjList<Integer, String, Double> graph = new GraphAdjList<>();
        graph.addVertex(0, "Source");
        graph.addVertex(1, "Node 1");
        graph.addVertex(2, "Node 2");
        graph.addEdge(0, 1, 5.0);
        graph.addEdge(1, 2, 5.0);

        HashMap<Integer, Integer> steps = new HashMap<>();
        HashMap<Integer, Integer> parent = new HashMap<>();
        RoutePlanner.bfs(graph, 0, steps, parent);

        assertEquals(Map.of(0, 0, 1, 1, 2, 2), steps);
        assertEquals(Map.of(1, 0, 2, 1), parent);
    }

    @Test
    void routeToTest() {
        HashMap<Integer, Integer> parent = new HashMap<>();
        parent.put(1, 0);
        parent.put(2, 1);

        List<Integer> route = RoutePlanner.routeTo(parent, 0, 2);

        assertEquals(List.of(0, 1, 2), route);
    }
}
