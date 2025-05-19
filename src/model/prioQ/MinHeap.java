package model.prioQ;

public class MinHeap<T extends Comparable<? super T>> implements MinHeapInterface<T>
{

    private T[] storage;
    private int maxSize;
    private int size;

    public MinHeap(T[] array)
    {
        this.storage = array;
        this.maxSize = array.length;
        this.size = 0;
    }

    private int parent(int idx) {
        return (idx - 1) / 2;
    }

    private int leftChild(int idx) {
        return idx * 2 + 1;
    }

    private int rightChild(int idx) {
        return idx * 2 + 2;
    }

    private void reHeapUp(int idx) {
        int parent = parent(idx);
        while(idx <= 0 && storage[idx].compareTo(storage[parent(idx)]) <= 0) {
           return;
        }
        swap(idx, parent);
        reHeapUp(parent);
    }

    private void reHeapDown(int idx) {
        while(true) {
            int left = leftChild(idx);
            int right = rightChild(idx);
            int smallest = idx;

            if (left < size && storage[left].compareTo(storage[smallest]) < 0) {
                smallest = left;
            }

            if (right < size && storage[right].compareTo(storage[smallest]) < 0) {
                smallest = right;
            }
            if (smallest != idx) {
                swap(idx, smallest);
                reHeapDown(smallest);
            }
        }
    }

    private void swap(int i, int j) {
        T temp = storage[i];
        storage[i] = storage[j];
        storage[j] = temp;
    }

    @Override
    public void insert(T data)
    {
        if(size >= maxSize) {
           throw new IllegalArgumentException("Heap is full!");
        }

        storage[size] = data;
        reHeapUp(size);
        size++;
    }

    @Override
    public T extract()
    {
        if(size <= 0) {
            throw new IllegalArgumentException("Heap is empty");
        }

        T min = storage[0];
        storage[0] = storage[size - 1];
        size--;
        if(size > 0) {
            reHeapDown(0);
        }
        return min;
    }

    @Override
    public void clear()
    {
        size = 0;
        storage = null;
    }

    @Override
    public int size()
    {
        return this.size;
    }

    @Override
    public int maxSize()
    {
        return this.maxSize;
    }
}