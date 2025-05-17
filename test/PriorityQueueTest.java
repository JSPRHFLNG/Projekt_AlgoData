import model.prioQ.PriorityQueue;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PriorityQueueTest
{
        /**
         * Enqueues cities, dequeues in order by priority (WEIGHT).
         * Lower weight equals higher priority.
         */
        @Test
        public void testEnqueueDequeue() {
            PriorityQueue<String, Integer> cities = new PriorityQueue<>(5);

            cities.enqueue("Falun", 250000);
            cities.enqueue("Sao Paulo", 2400000);
            cities.enqueue("Valbo", 75);
            cities.enqueue("Sandviken", 150000);
            cities.enqueue("Oslo", 700000);

            assertEquals("Valbo", cities.dequeue());
            assertEquals("Sandviken", cities.dequeue());
            assertEquals("Falun", cities.dequeue());
            assertEquals("Oslo", cities.dequeue());
            assertEquals("Sao Paulo", cities.dequeue());

            assertTrue(cities.isEmpty());
        }

        @Test
        public void testInitiallyEmpty() {
            PriorityQueue<String, Integer> queue = new PriorityQueue<>(10);
            assertTrue(queue.isEmpty());
        }


        @Test
        public void testEmptyQueueReturnsNull() {
            PriorityQueue<String, Integer> queue = new PriorityQueue<>(10);
            assertNull(queue.dequeue());
        }
}
