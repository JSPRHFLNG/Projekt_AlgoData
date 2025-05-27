import model.graph.Edge;
import model.graph.Graph;
import model.graph.Vertex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GraphTest {

    public Graph<String> graph;

    @BeforeEach
    public void setup() {
        graph = new Graph<>();
    }


    @Test
    public void testGetAllVertices() {
        graph.addVertex(100, 100, "A");
        graph.addVertex(200, 150, "B");
        graph.addVertex(300, 100, "C");

        List<Vertex<String>> vertices = graph.getAllVertices();

        assertEquals(3, graph.numberOfVertices());
        assertEquals(3, vertices.size());
    }


    @Test
    public void testGetEdges() {
        graph.addVertex(100, 100, "A");
        graph.addVertex(100, 100, "B");
        graph.addEdge("A", "B");

        List<Edge<String>> edgesFromA = graph.getEdges("A");
        assertNotNull(edgesFromA);
        assertEquals(1, edgesFromA.size());

        Edge<String> edge = edgesFromA.getFirst();
        assertEquals("A", edge.getFrom().getInfo());
        assertEquals("B", edge.getTo().getInfo());
    }


    @Test
    public void testRemoveNonExisting() {
        graph.addVertex(10, 10, "A");
        graph.remove("B");

        assertEquals(1, graph.numberOfVertices());
    }


    @Test
    public void testRemoveExisting() {
        graph.addVertex(10, 10, "A");
        graph.remove("A");

        assertEquals(0, graph.numberOfVertices());
    }


    @Test
    public void testNumberOfEdges() {
        graph.addVertex(10, 10, "A");
        graph.addVertex(20, 15, "B");
        graph.addVertex(40, 10, "C");

        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("C", "A");

        assertEquals(3, graph.numberOfEdges());
    }


    @Test
    public void testNumberOfVertices() {
        graph.addVertex(10, 10, "A");
        graph.addVertex(20, 15, "B");
        graph.addVertex(40, 10, "C");

        assertEquals(3, graph.numberOfVertices());
    }

}