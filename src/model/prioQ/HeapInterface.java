package model.prioQ;

public interface HeapInterface<T>
{
    void insert(T data);
    T extract();
    void clear();
    int size();
    int maxSize();
}
