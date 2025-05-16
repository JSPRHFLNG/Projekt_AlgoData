package model.prioQ;

public interface PriorityQueueInterface<DATA, WEIGHT extends Comparable<WEIGHT>>
{
    void enqueue(DATA data, WEIGHT weight);
    DATA dequeue();
    boolean isEmpty();
}
