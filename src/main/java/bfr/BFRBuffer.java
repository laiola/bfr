package bfr;

import org.apache.commons.collections.buffer.BoundedFifoBuffer;

import java.util.ArrayList;

public class BFRBuffer extends BoundedFifoBuffer {
    public static final int DEFAULT_SIZE = 1000;
    private final ArrayList<Vector> data; // TODO integration

    public BFRBuffer() {
        super(DEFAULT_SIZE);
        this.data = Vector.createRandomPoints(-1000, 1000, DEFAULT_SIZE * 2); // TODO integration
        for (int i = 0; i < DEFAULT_SIZE; i++) {
            add(data.get(i));
        }
    }

    public BFRBuffer(int size) {
        super(size);
        this.data = Vector.createRandomPoints(-1000, 1000, size * 10); // TODO integration
        for (int i = 0; i < size; i++) {
            add(data.get(i));
        }
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
}
