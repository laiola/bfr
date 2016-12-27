package bfr;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;

public class BFR {
    private static final int MAX_ITERATIONS = 5000;
    public static final int NUMBER_OF_ATTRIBUTES = 4;
    private final ArrayList<DiscardSet> discardSet;
    private final ArrayList<CompressSet> compressSet;
    private final RetainedSet retainedSet;
    //public int numberOfAttributes;
    private int numberOfClusters = 3;
    private double confidenceInterval = 500.0;

    public BFR(int numberOfClusters) {
        this.retainedSet = new RetainedSet();
        this.discardSet = new ArrayList<>(); // DiscardSet(numberOfAttributes);
        this.compressSet = new ArrayList<>(); // CompressSet(numberOfAttributes);
        this.numberOfClusters = numberOfClusters;
    }

    public BFR(int numberOfVectors, int numberOfClusters) {
        this.retainedSet = new RetainedSet(numberOfVectors);
        this.discardSet = new ArrayList<>(); // new DiscardSet(numberOfAttributes);
        this.compressSet = new ArrayList<>(); // new CompressSet(numberOfAttributes);
        this.numberOfClusters = numberOfClusters;
    }

    public static void main(String[] args) {
        BFR bfr = new BFR(3);
        bfr.init();
        bfr.calculate();
        bfr.finish();
    }

    public double getConfidenceInterval() {
        return confidenceInterval;
    }

    public void setConfidenceInterval(double confidenceInterval) {
        this.confidenceInterval = confidenceInterval;
    }

    //Initializes the process
    private void init() {
        initDS();
        initCS();
        plotClusters();
    }

    private void initDS() {
        // Create Clusters
        for (int i = 0; i < numberOfClusters; i++) {
            discardSet.add(new DiscardSet(NUMBER_OF_ATTRIBUTES));
        }
        // Set Random Centroids
        for (int i = 0; i < numberOfClusters; i++) {
            discardSet.get(i).updateStatistic(Vector.createRandomPoint(-100, 100));
        }
    }

    private void initCS() {
        // Create sub-clusters
        double distance;
        ListIterator<Vector> rsIterator = retainedSet.getVectors().listIterator();

        while (rsIterator.hasNext()) {
            Vector vector1 = rsIterator.next();
            Vector vector2 = null;
            if (rsIterator.hasNext()) {
                vector2 = rsIterator.next();
            }
            if (vector1 == null || vector2 == null) break;

            distance = MahalanobisDistance.calculate(vector1, vector2);
            if (distance < confidenceInterval) { // todo check
                //confidenceInterval = distance;
                compressSet.add(new CompressSet(NUMBER_OF_ATTRIBUTES));
                rsIterator.remove();
                compressSet.get(compressSet.size()-1).updateStatistic(vector2); // todo check
            }
        }
        plotClusters();
    }

    private void assignDS() {
        double distance;

        // watching rs
        Iterator<Vector> rsIterator = retainedSet.getVectors().iterator();
        while (rsIterator.hasNext()) {
            Vector vector = rsIterator.next();
            for (int i = 0; i < numberOfClusters; i++) {
                DiscardSet c = discardSet.get(i);
                distance = MahalanobisDistance.calculate(c, vector);

                if (distance < confidenceInterval) {
                    //confidenceInterval = distance;
                    c.updateStatistic(vector);
                    rsIterator.remove();
                    break;
                }
            }
        }

        // watching cs
        for (int i = 0; i < numberOfClusters; i++) {
            DiscardSet c = discardSet.get(i);
            Iterator<CompressSet> csIterator = compressSet.iterator();
            while (csIterator.hasNext()) {
                CompressSet aCompressSet = csIterator.next();
                distance = MahalanobisDistance.calculate(aCompressSet, c.getCentroid());
                /*JOptionPane.showMessageDialog(null, "size: " + compressSet.size()
                    + "\nCS centroid: " + aCompressSet.getCentroid()
                    + "\nRS centroid: " + c.getCentroid()
                    + "\ndistanceL: " + distance);*/
                if (distance < confidenceInterval) {
                    //confidenceInterval = distance;
                    c.updateStatistic(aCompressSet.getCentroid());
                    csIterator.remove();
                    continue;
                }
            }
        }
    }

    private void assignCS() {
        double distance;
        Iterator<Vector> rsIterator = retainedSet.getVectors().iterator();

        while (rsIterator.hasNext()) {
            Vector vector = rsIterator.next();
            for (CompressSet aCompressSet : compressSet) {
                distance = MahalanobisDistance.calculate(aCompressSet, vector);
                if (distance < confidenceInterval) {
                    //confidenceInterval = distance;
                    aCompressSet.updateStatistic(vector);
                    rsIterator.remove();
                    break;
                }
            }
        }
    }

    private void assignRS() {
        System.out.println("before: " + retainedSet.getVectors().size());
        retainedSet.updateRS();
        System.out.println("after: " + retainedSet.getVectors().size());
    }

    // TODO
    private void finish() {
        double distance;
        ArrayList<Double> distances = new ArrayList<>();

        Iterator<CompressSet> csIterator = compressSet.iterator();
        while (csIterator.hasNext()) {
            CompressSet tmp = csIterator.next();
            for (int i = 0; i < numberOfClusters; i++) {
                DiscardSet c = discardSet.get(i);
                distance = MahalanobisDistance.calculate(c, tmp.getCentroid());
                distances.add(distance);
            }
            int id = 0;
            Double min = distances.get(0);
            for (int i = 1; i < numberOfClusters; i++) {
                if (Double.compare(min, distances.get(i)) == 1) {
                    min = distances.get(i);
                    id = i;
                }
            }
            discardSet.get(id).updateStatistic(tmp.getCentroid());
            csIterator.remove();
            distances = new ArrayList<>();
        }

        Iterator<Vector> rsIterator = retainedSet.getVectors().iterator();
        while (rsIterator.hasNext()) {
            Vector vector = rsIterator.next();
            for (int i = 0; i < numberOfClusters; i++) {
                DiscardSet c = discardSet.get(i);
                distance = MahalanobisDistance.calculate(c, vector);
                distances.add(distance);
                }
            int id = 0;
            Double min = distances.get(0);
            for (int i = 1; i < numberOfClusters; i++) {
                if (Double.compare(min, distances.get(i)) == 1) {
                    min = distances.get(i);
                    id = i;
                }
            }
            discardSet.get(id).updateStatistic(vector);
            rsIterator.remove();
            distances = new ArrayList<>();
        }

        plotClusters();
    }

    private void calculate() {
        boolean finish = false;
        int iteration = 0;

        // Add in new data, one at a time, recalculating centroids with each new one.
        while (!finish) {
            //Clear cluster state
            //clearClusters();

            //List lastCentroids = getCentroids();

            // Assign points to the closer cluster
            assignDS();

            // Assign points to the closer sub-cluster
            if (compressSet.size() == 0) {
                initCS();
                assignCS();
            }
            else assignCS();

            // TODO
           /* // Assign points to the closer sub-cluster
            // if (compressSet.size() == 0) {
            initCS();
            // }
            //else
            assignCS();*/

            // Updating rs (getting new vectors from buffer)
            assignRS();

            iteration++;

            // Calculates total distance between new and old Centroids
            /*double distance = 0;
            for(int i = 0; i < lastCentroids.size(); i++) {
                distance += MahalanobisDistance.calculate(lastCentroids.get(i),currentCentroids.get(i));
            }*/
            System.out.println("Iteration: " + iteration);
            //System.out.println("Centroid distances: " + distance);
            plotClusters();

            if (retainedSet.getVectors().equals(Collections.EMPTY_LIST) || iteration > MAX_ITERATIONS) {
                finish = true;
            }
        }
    }

    private void plotClusters() {
        System.out.println("rS: " + retainedSet.getVectors().size());
        System.out.println("discardSet " + discardSet.size());
        for (int i = 0; i < numberOfClusters; i++) {
            System.out.println(discardSet.get(i).toString());
        }
        System.out.println("compressSet " + compressSet.size());
        for (int i = 0; i < compressSet.size() && !compressSet.isEmpty(); i++) {
            System.out.println(compressSet.get(i).toString());
        }
        System.out.println("+++++++++++++++++++");
    }
}
