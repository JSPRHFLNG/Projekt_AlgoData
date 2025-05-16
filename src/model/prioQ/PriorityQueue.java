package model.prioQ;

import java.lang.reflect.Array;

public class PriorityQueue<DATA, WEIGHT extends Comparable<WEIGHT>> implements PriorityQueueInterface<DATA, WEIGHT>
{

    private HeapInterface<QueueNode> heap;

    public PriorityQueue(int capacity) {
        @SuppressWarnings("unchecked")
        QueueNode[] array = (QueueNode[]) Array.newInstance(
                QueueNode.class, capacity
        );
        heap = new Heap<QueueNode>(array);

    }
    private class QueueNode implements Comparable<QueueNode>
    {
        private final DATA data;
        private final WEIGHT weight;

        public QueueNode(DATA data, WEIGHT weight){
            this.data = data;
            this.weight = weight;
        }

        /**
         *
         * @param other the object to be compared.
         * @return 1, -1 or 0.
         */
        @Override
        public int compareTo(QueueNode other)
        {
            return this.weight.compareTo(other.weight);
        }
    }


    /**
     * @param data
     * @param weight
     */
    @Override
    public void enqueue(DATA data, WEIGHT weight)
    {
        heap.insert(new QueueNode(data, weight));
    }


    /** Return the data from the highest priority node (the root).
     * @return the data from the highest priority node.
     */
    @Override
    public DATA dequeue()
    {
        QueueNode qNode = heap.extract();
        if(qNode == null)
        {
            return null;
        }
        return qNode.data;
    }


    /**
     * Checks if heap is empty and returns a boolean.
     * @return true if heap is empty, otherwise false.
     */
    @Override
    public boolean isEmpty()
    {
        return heap.size() == 0;
    }


}