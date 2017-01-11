package bfr;

import java.util.ArrayList;
import java.util.Random;

public class Vector {
    private final int size;
    private ArrayList<Double> coordinates = new ArrayList<>();
    private int cluster_number = 0;

    public Vector(ArrayList<Double> coordinates) {
        this.size = coordinates.size();
        this.coordinates = coordinates;
    }

    public Vector(int size) {
        this.size = size;
    }

    //Creates random point
    static Vector createRandomPoint(int min, int max) {
        Random r = new Random();
        int n = BFR.NUMBER_OF_ATTRIBUTES ;
        ArrayList<Double> result = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            result.add(min + (max - min) * r.nextDouble());
        }
        return new Vector(result);
    }

    static ArrayList<Vector> createRandomPoints(int min, int max, int number) {
        ArrayList<Vector> result = new ArrayList<>(number);
        for (int i = 0; i < number; i++) {
            result.add(createRandomPoint(min, max));
        }
        return result;
    }

    public ArrayList<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<Double> coordinates) {
        this.coordinates = coordinates;
    }

    public int getCluster_number() {
        return cluster_number;
    }

    public void setCluster_number(int cluster_number) {
        this.cluster_number = cluster_number;
    }

    @Override
    public String toString() {
        String result = "Vector {";
        for (Double d : coordinates) {
            result += d + ", ";
        }
        result += "}";
        return result;
    }
}
