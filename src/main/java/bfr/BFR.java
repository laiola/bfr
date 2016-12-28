package bfr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

public class BFR {
    private static final int MAX_ITERATIONS = 20;
    public static final int NUMBER_OF_ATTRIBUTES = 4;
    private final ArrayList<Cluster> discardSet;
    private final ArrayList<Cluster> compressSet;
    private final RetainedSet retainedSet;
    //public int numberOfAttributes;
    private int numberOfClusters;
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
        BFR bfr = new BFR(5);
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
        initDS(); // initialization of clusters
        initCS(); // initialization of sub-clusters
    }

    private void initDS() {
        // Create Clusters
        for (int i = 0; i < numberOfClusters; i++) {
            discardSet.add(new Cluster(NUMBER_OF_ATTRIBUTES));
        }
        // Set Random Centroids
        for (int i = 0; i < numberOfClusters; i++) {
            discardSet.get(i).updateStatistic(Vector.createRandomPoint(-1000, 1000));
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
                compressSet.add(new Cluster(NUMBER_OF_ATTRIBUTES));
                rsIterator.remove();
                int index = compressSet.size()-1;
                compressSet.get(index).updateStatistic(vector2); // todo check
                compressSet.get(index).updateStatistic(vector1);
                rsIterator.previous();
                rsIterator.remove();
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
                Cluster c = discardSet.get(i);
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
            Cluster ds = discardSet.get(i);
            Iterator<Cluster> csIterator = compressSet.iterator();
            while (csIterator.hasNext()) {
                Cluster cs = csIterator.next();
                distance = MahalanobisDistance.calculate(cs, ds.getCentroid());
                /*JOptionPane.showMessageDialog(null, "size: " + compressSet.size()
                    + "\nCS centroid: " + aCompressSet.getCentroid()
                    + "\nRS centroid: " + c.getCentroid()
                    + "\ndistanceL: " + distance);*/
                if (distance < confidenceInterval) {
                    //confidenceInterval = distance;
                    ds.updateStatistic(cs.getCentroid());
                    csIterator.remove();
                    break;
                }
            }
        }
    }

    private void assignCS() {
        Iterator<Vector> rsIterator = retainedSet.getVectors().iterator();

        while (rsIterator.hasNext()) {
            Vector vector = rsIterator.next();
            for (Cluster cs : compressSet) {
                double distance = MahalanobisDistance.calculate(cs, vector);
                if (distance < confidenceInterval) {
                    //confidenceInterval = distance;
                    cs.updateStatistic(vector);
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

        Iterator<Cluster> csIterator = compressSet.iterator();
        while (csIterator.hasNext()) {
            Cluster tmp = csIterator.next();
            for (int i = 0; i < numberOfClusters; i++) {
                Cluster c = discardSet.get(i);
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
            discardSet.get(id).updateStatistic(tmp);
            csIterator.remove();
            distances = new ArrayList<>();
        }

        Iterator<Vector> rsIterator = retainedSet.getVectors().iterator();
        while (rsIterator.hasNext()) {
            Vector vector = rsIterator.next();
            for (int i = 0; i < numberOfClusters; i++) {
                Cluster c = discardSet.get(i);
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

        while (!finish) {

            if (iteration == 1) {
                System.out.println();
            }
            assignDS();

            if (compressSet.size() == 0) {
                initCS();
                assignCS();
            }
            else {
                assignCS();
            }

            assignRS();

            iteration++;


            System.out.println("Iteration: " + iteration);
            //System.out.println("Centroid distances: " + distance);
            plotClusters();

            if (retainedSet.getVectors().isEmpty() || iteration > MAX_ITERATIONS) {
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
