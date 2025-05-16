package model.prioQ;

public class Heap<T> implements HeapInterface<T>
{

    private T[] storage;
    private int maxSize;
    private int size;

    public Heap(T[] array)
    {
        this.storage = array;
        this.maxSize = array.length;
        this.size = 0;
    }

    @Override
    public void insert(T data)
    {

    }

    @Override
    public T extract()
    {
        return null;
    }

    @Override
    public void clear()
    {

    }

    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public int maxSize()
    {
        return 0;
    }
}