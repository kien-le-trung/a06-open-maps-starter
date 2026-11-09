package comp210.a6;

import bridges.base.Edge;
import bridges.base.GraphAdjList;
import bridges.base.SLelement;
import bridges.data_src_dependent.OsmVertex;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Run these when you finish Part 0. Right-click this file in IntelliJ and
 * choose Run 'Part0Test'. None of them touch the network: each one works in
 * a temporary folder that JUnit deletes afterward.
 *
 * These are a sample of what Gradescope runs, not all of it.
 */
class Part0Test {

    @TempDir
    Path dir;

    /** Three intersections, with two-way streets from 0 to 1 and from 0 to 2. */
    static GraphAdjList<Integer, OsmVertex, Double> threeCorners() {
        GraphAdjList<Integer, OsmVertex, Double> graph = new GraphAdjList<>();
        graph.addVertex(0, new OsmVertex(35.9099702, -79.053269));
        graph.getVertex(0).setLocation(977.2961869168789, -5075.894994299386);
        graph.addVertex(1, new OsmVertex(35.91058, -79.0487785));
        graph.getVertex(1).setLocation(977.7104, -5075.3001);
        graph.addVertex(2, new OsmVertex(35.9059451, -79.0456232));
        graph.getVertex(2).setLocation(978.5, -5076.25);
        graph.addEdge(0, 1, 84.2);
        graph.addEdge(1, 0, 84.2);
        graph.addEdge(0, 2, 12.5);
        graph.addEdge(2, 0, 12.5);
        return graph;
    }

    /** Each vertex's adjacency list as "to/length" strings, in list order. */
    static List<String> listOf(GraphAdjList<Integer, OsmVertex, Double> graph, int vertex) {
        List<String> edges = new ArrayList<>();
        SLelement<Edge<Integer, Double>> list = graph.getAdjacencyList(vertex);
        while (list != null) {
            edges.add(list.getValue().getTo() + "/" + list.getValue().getEdgeData());
            list = list.getNext();
        }
        return edges;
    }

    @Test
    void writeStartsWithTheHeader() throws IOException {
        Path file = dir.resolve("map.txt");
        MapLoader.write(threeCorners(), "Chapel Hill, North Carolina", "tertiary", file);
        List<String> lines = Files.readAllLines(file);
        assertEquals("place Chapel Hill, North Carolina", lines.get(0));
        assertEquals("level tertiary", lines.get(1));
        assertEquals("vertices 3", lines.get(2));
        assertEquals("edges 4", lines.get(6), "line 7 comes right after the three vertex lines");
        assertEquals(11, lines.size(), "3 header lines, 3 vertices, 1 edges line, 4 edges");
    }

    @Test
    void writeKeepsEveryDigit() throws IOException {
        Path file = dir.resolve("map.txt");
        MapLoader.write(threeCorners(), "Chapel Hill, North Carolina", "tertiary", file);
        String text = Files.readString(file);
        assertTrue(text.contains("0 35.9099702 -79.053269 977.2961869168789 -5075.894994299386"),
                "the line for vertex 0, every digit kept. Build each line with + so Java writes the whole double.");
    }

    @Test
    void readBuildsTheGraph() throws IOException {
        Path file = dir.resolve("map.txt");
        Files.writeString(file, String.join("\n",
                "place Chapel Hill, North Carolina",
                "level tertiary",
                "vertices 2",
                "4 35.91 -79.05 977.5 -5075.5",
                "9 35.92 -79.04 978.25 -5075.75",
                "edges 1",
                "4 9 120.5") + "\n");
        GraphAdjList<Integer, OsmVertex, Double> graph = MapLoader.read(file);

        assertEquals(2, graph.getVertices().size());
        assertEquals(35.92, graph.getVertexData(9).getLatitude());
        assertEquals(-79.04, graph.getVertexData(9).getLongitude());
        assertEquals(978.25, graph.getVertex(9).getVisualizer().getLocationX(), "map x, from setLocation");
        assertEquals(-5075.75, graph.getVertex(9).getVisualizer().getLocationY(), "map y, from setLocation");
        assertEquals(120.5, graph.getEdgeData(4, 9), "the edge from 4 to 9");
        assertNull(graph.getAdjacencyList(9), "9 has no edges out");
    }

    @Test
    void readRejectsAFileThatEndsEarly() throws IOException {
        Path file = dir.resolve("map.txt");
        Files.writeString(file, "place Chapel Hill, North Carolina\nlevel tertiary\nvertices 5\n4 35.91 -79.05 977.5 -5075.5\n");
        assertThrows(IOException.class, () -> MapLoader.read(file), "the file promises 5 vertices but has 1");
    }

    @Test
    void readUndoesWrite() throws IOException {
        GraphAdjList<Integer, OsmVertex, Double> graph = threeCorners();
        Path file = dir.resolve("map.txt");
        MapLoader.write(graph, "Chapel Hill, North Carolina", "tertiary", file);
        GraphAdjList<Integer, OsmVertex, Double> back = MapLoader.read(file);
        for (int v = 0; v < 3; v++) {
            assertEquals(graph.getVertexData(v).getLatitude(), back.getVertexData(v).getLatitude());
            assertEquals(listOf(graph, v), listOf(back, v),
                    "vertex " + v + "'s adjacency list, in the same order. See the README on edge order.");
        }
    }
}
