import model.graph.Vertex;
import model.quadtree.Quadtree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QuadtreeTest {

    private Quadtree<String> quadtree;
    private Quadtree.Rectangle boundary;

    @BeforeEach
    void setUp() {
        boundary = new Quadtree.Rectangle(50, 50, 100, 100);
        quadtree = new Quadtree<>(boundary);
    }

    // Helps with creating a Vertex
    private Vertex<String> createVertex(double x, double y, String info) {
        return new Vertex<>(x, y, info);
    }

    @Test
    @DisplayName("Test insert into empty Quadtree")
    void testInsert() {
        Vertex<String> vertex = createVertex(25, 25, "A");
        assertTrue(quadtree.insert(vertex));
        assertEquals(1, quadtree.getAllVertices().size());
        assertTrue(quadtree.getAllVertices().contains(vertex));
        assertFalse(quadtree.divided);
    }


    @Test
    @DisplayName("Test query by finding vertices in range")
    void testQuery() {
        Vertex<String> v1 = createVertex(10, 10, "A");
        Vertex<String> v2 = createVertex(20, 20, "B");
        Vertex<String> v3 = createVertex(100, 100, "C");

        quadtree.insert(v1);
        quadtree.insert(v2);
        quadtree.insert(v3);

        List<Vertex<String>> found = new ArrayList<>();
        Quadtree.Rectangle range = new Quadtree.Rectangle(15, 15, 20, 20);

        quadtree.query(range, found);

        assertEquals(2, found.size());
        assertTrue(found.contains(v1));
        assertTrue(found.contains(v2));
        assertFalse(found.contains(v3));

    }

    @Test
    @DisplayName("Trigger subdivide")
    void testSubdivide() {
        assertTrue(quadtree.insert(createVertex(25, 25, "A")));
        assertTrue(quadtree.insert(createVertex(30, 30, "B")));
        assertTrue(quadtree.insert(createVertex(20, 20, "C")));
        assertTrue(quadtree.insert(createVertex(40, 15, "D")));

        //Should not have subdivided yet
        assertFalse(quadtree.divided);

        // Insert 5th vertex, should subdivide
        assertTrue(quadtree.insert(createVertex(40, 30, "E")));
        assertTrue(quadtree.divided);
        assertEquals(5, quadtree.getAllVertices().size());
        assertTrue(quadtree.divided);
    }


}
