package model.prioQ;

public interface MinHeapInterface<T>
{
    void insert(T data);
    T extract();
    void clear();
    int size();
    int maxSize();
}
