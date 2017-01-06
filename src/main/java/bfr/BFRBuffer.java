package bfr;

import org.apache.commons.collections.buffer.BoundedFifoBuffer;

import java.util.ArrayList;
import java.util.Iterator;

public class BFRBuffer extends BoundedFifoBuffer {
    public static final int MIN = 0;
    public static final int MAX = 10;

    public static final int DEFAULT_SIZE = 1000;
    private final ArrayList<Vector> data; // TODO integration
    private final int size;

    public BFRBuffer() {
        super(DEFAULT_SIZE);
        this.size = DEFAULT_SIZE;
        this.data = Vector.createRandomPoints(MIN, MAX, DEFAULT_SIZE * 2); // TODO integration
        Iterator<Vector> iterator = data.listIterator();
        int i = 0;
        while (iterator.hasNext() && i < DEFAULT_SIZE) {
            Vector tmp = iterator.next();
            add(tmp);
            iterator.remove();
            ++i;
        }
        /*for (int i = 0; i < DEFAULT_SIZE; i++) {
            add(data.get(i));
        }*/
    }

    public BFRBuffer(int size) {
        super(size);
        this.size = size;
        this.data = Vector.createRandomPoints(MIN, MAX, size * 10); // TODO integration
        Iterator<Vector> iterator = data.listIterator();
        int i = 0;
        while (iterator.hasNext() && i < size) {
            Vector tmp = iterator.next();
            add(tmp);
            iterator.remove();
            ++i;
        }/*
        for (int i = 0; i < size; i++) {
            add(data.get(i));
        }*/
    }

    private void add(Vector element) {
        super.add(element);
    }

    public Vector get() {
        return (Vector) super.get();
    }

    public Vector remove() {
        return (Vector) super.remove();
    }

    protected void UpdateBuffer() {
        Iterator<Vector> iterator = data.listIterator();
        while (iterator.hasNext() && !this.isFull()) {
            Vector tmp = iterator.next();
            add(tmp);
            iterator.remove();
        }
    }
}
