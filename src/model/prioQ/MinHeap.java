package model.prioQ;

public class MinHeap<T extends Comparable<? super T>> implements MinHeapInterface<T> {

    private T[] storage;
    private int maxSize;
    private int size;

    public MinHeap(T[] array) {
        this.storage = array;
        this.maxSize = array.length;
        this.size = 0;
    }

    private int parent(int idx) {
        if (idx == 0) {
            return -1; // ingen förälder
        }
        return (idx - 1) / 2;
    }

    private int leftChild(int idx) {
        int leftIdx = 2 * idx + 1;

        if (leftIdx >= size) {
            return -1;
        }
        return leftIdx;
    }

    private int rightChild(int idx) {
        int rightIdx = 2 * idx + 2;

        if (rightIdx >= size) {
            return -1;
        }
        return rightIdx;
    }

    private void reHeapUp(int idx) {
        int parent = parent(idx);

        if (parent == -1) {
            return;
        }
        if (storage[idx].compareTo(storage[parent(idx)]) < 0) {
            if (idx <= 0 && storage[idx].compareTo(storage[parent(idx)]) <= 0) {

                swap(idx, parent);
                reHeapUp(parent);
            }
        }
    }

    private void reHeapDown(int idx) {

        int left = leftChild(idx);
        int right = rightChild(idx);
        int smallest = idx;

        if (left != -1 && storage[left].compareTo(storage[smallest]) < 0) {
            smallest = left;
        }

        if (right != -1 && storage[right].compareTo(storage[smallest]) < 0) {
            smallest = right;
        }
        if (smallest != idx) {
            swap(idx, smallest);
            reHeapDown(smallest);
        }

    }

    private void swap(int i, int j) {
        T temp = storage[i];
        storage[i] = storage[j];
        storage[j] = temp;
    }

    @Override
    public void insert(T data) {
        if (size >= maxSize) {
            throw new IllegalArgumentException("Heap is full!");
        }

        storage[size] = data;
        reHeapUp(size);
        size++;
    }

    @Override
    public T extract() {
        if (size <= 0) {
            throw new IllegalArgumentException("Heap is empty");
        }

        T min = storage[0];
        storage[0] = storage[size - 1];
        size--;
        if (size > 0) {
            reHeapDown(0);
        }
        return min;
    }

    @Override
    public void clear() {
        size = 0;
        storage = null;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public int maxSize() {
        return this.maxSize;
    }
}