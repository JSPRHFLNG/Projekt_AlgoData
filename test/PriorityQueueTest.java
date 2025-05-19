import model.prioQ.PriorityQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PriorityQueueTest
{
    @Test
    public void testPriorityQueueWithCities() {
        PriorityQueue<String, Integer> cities = new PriorityQueue<>(10);

        cities.enqueue("Falun", 250000);
        cities.enqueue("Sao Paulo", 2400000);
        cities.enqueue("Valbo", 75);
        cities.enqueue("Sandviken", 150000);
        cities.enqueue("Oslo", 700000);

        assertFalse(cities.isEmpty());

        assertEquals("Valbo", cities.dequeue());
        assertEquals("Sandviken", cities.dequeue());
        assertEquals("Falun", cities.dequeue());
        assertEquals("Oslo", cities.dequeue());
        assertEquals("Sao Paulo", cities.dequeue());

        assertTrue(cities.isEmpty());
    }

    @Test
    public void testPriorityQueueThrowsOnEmptyDequeue() {
        PriorityQueue<String, Integer> queue = new PriorityQueue<>(5);
        assertThrows(IllegalArgumentException.class, queue::dequeue);
    }


    @Test
    public void testPriorityQueueThrowsOnOverflow() {
        PriorityQueue<String, Integer> queue = new PriorityQueue<>(2);
        queue.enqueue("A", 1);
        queue.enqueue("B", 2);
        assertThrows(IllegalArgumentException.class, () -> queue.enqueue("C", 3));
    }

            assertTrue(cities.isEmpty());
        }

        @Test
        public void testInitiallyEmpty() {
            assertTrue(queue.isEmpty());
        }

        @Test
        public void testEmptyQueueReturnsNull() {
            assertNull(queue.dequeue());
        }
}
