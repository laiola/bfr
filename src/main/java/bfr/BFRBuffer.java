package bfr;

import org.apache.commons.collections.buffer.BoundedFifoBuffer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class BFRBuffer extends BoundedFifoBuffer {
    public static final int MIN = 0;
    public static final int MAX = 10;

    public static final int DEFAULT_SIZE = 100;
    private final CopyOnWriteArrayList<Vector> data; // TODO integration
    private final int size;

    public BFRBuffer(CopyOnWriteArrayList<Vector> vectors) {
        super(DEFAULT_SIZE);
        this.size = DEFAULT_SIZE;
        this.data = vectors;
        Iterator<Vector> iterator = data.listIterator();
        CopyOnWriteArrayList<Vector> temp = new CopyOnWriteArrayList();
        int i = 0;
        while (iterator.hasNext() && i < DEFAULT_SIZE) {
            Vector tmp = iterator.next();
            add(tmp);
            temp.add(tmp);
            ++i;
        }
        data.removeAll(temp);
    }

    public static ArrayList<Vector> getData(String file) {
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
        CopyOnWriteArrayList<Vector> temp = new CopyOnWriteArrayList<>();

        Iterator<Vector> iterator = data.listIterator();
        while (iterator.hasNext() && !this.isFull()) {
            Vector tmp = iterator.next();
            add(tmp);
            temp.add(tmp);
        }
        data.removeAll(temp);
    }
}
