package bfr;

import org.apache.commons.collections.buffer.BoundedFifoBuffer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
        this.data = Vector.createRandomPoints(MIN, MAX, DEFAULT_SIZE); // TODO integration
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
        this.data = Vector.createRandomPoints(MIN, MAX, size); // TODO integration
        Iterator<Vector> iterator = data.listIterator();
        int i = 0;
        while (iterator.hasNext() && i < size) {
            Vector tmp = iterator.next();
            add(tmp);
            iterator.remove();
            ++i;
        }
    }

    public BFRBuffer(ArrayList<Vector> vectors) {
        super(DEFAULT_SIZE);
        this.size = DEFAULT_SIZE;
        this.data = vectors;
        Iterator<Vector> iterator = data.listIterator();
        int i = 0;
        while (iterator.hasNext() && i < DEFAULT_SIZE) {
            Vector tmp = iterator.next();
            add(tmp);
            iterator.remove();
            ++i;
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

    protected void UpdateBuffer() {
        Iterator<Vector> iterator = data.listIterator();
        while (iterator.hasNext() && !this.isFull()) {
            Vector tmp = iterator.next();
            add(tmp);
            iterator.remove();
        }
    }

    public ArrayList<Vector> getData(String file) {
        ArrayList<Vector> vectors = new ArrayList<>();
        String str;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while ((str = reader.readLine()) != null) {
                String[] tempLine = str.split("\t");
                ArrayList<Double> tempList = new ArrayList<>();
                for (String temp : tempLine) {
                    tempList.add(Double.valueOf(temp));
                }
                vectors.add(new Vector(tempList));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return vectors;
    }
}
