package bfr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

public class BFR {
    static final int NUMBER_OF_ATTRIBUTES = 100;
    private static final int MAX_ITERATIONS = 200;
    private final ArrayList<Cluster> discardSet;
    private final ArrayList<Cluster> compressSet;
    private final RetainedSet retainedSet;
    private int numberOfClusters;
    private int numberOfAttributes;

    public BFR(int numberOfClusters, ArrayList<Vector> vectors) {
        this.numberOfClusters = numberOfClusters;
        this.numberOfAttributes = vectors.get(0).getCoordinates().size();
        this.discardSet = new ArrayList<>();
        this.compressSet = new ArrayList<>();
        this.retainedSet = new RetainedSet(vectors);
    }

    /*public static void main(String[] args) {
        for (int i = 0; i < 2; i++) {
            BFR bfr = new BFR(5, 10000);
            bfr.init();
            bfr.calculate();
            bfr.finish();
            int res = 0;
            for (Cluster ds: bfr.discardSet) {
                res += ds.getStatistic().getN();
            }
            System.out.println("\nres[" + i + "]: " + res);
        }
    }*/

    public void bfr() {
        /*init();
        calculate();
        finish();
        int res = 0;
        for (Cluster ds: discardSet) {
            res += ds.getStatistic().getN();
        }
        System.out.println("\nres: " + res);*/

        for (int i = 0; i < 1; i++) {
            init();
            calculate();
            finish();
            int res = 0;
            for (Cluster ds: discardSet) {
                res += ds.getStatistic().getN();
            }
            System.out.println("\nres[" + i + "]: " + res);
        }
    }

    //Initializes the process
    private void init() {
        initDS(); // initialization of clusters
        initCS(); // initialization of sub-clusters
    }

    private void initDS() {
        // Create Clusters
        for (int i = 0; i < numberOfClusters; i++) {
            discardSet.add(new Cluster(numberOfAttributes));
        }
        // Set Random Centroids
        for (int i = 0; i < numberOfClusters; i++) {
            discardSet.get(i).updateStatistic(Vector.createRandomPoint(BFRBuffer.MIN, BFRBuffer.MAX));
        }

        // Set Centroids
        /*for (int i = 0, j = 1; i < numberOfClusters; i++) {
            ArrayList<Double> vector = new ArrayList<>(numberOfAttributes);
            vector.add((double) 0);
            for (int k = 1; k < numberOfAttributes; k++) {
                vector.add((double) j);
            }
            j++;
            discardSet.get(i).updateStatistic(new Vector(vector));
        }*/
    }

    private void initCS() {
        // Create sub-clusters
        ListIterator<Vector> rsIterator = retainedSet.getVectors().listIterator();

        while (rsIterator.hasNext()) {
            Vector vector1 = rsIterator.next();
            Vector vector2 = null;
            if (rsIterator.hasNext()) {
                vector2 = rsIterator.next();
            }
            if (vector1 == null || vector2 == null) break;

            if (ConfidenceInterval.isEntered(vector1, vector2)) {
                compressSet.add(new Cluster(numberOfAttributes));
                rsIterator.remove();
                int index = compressSet.size()-1;
                compressSet.get(index).updateStatistic(vector2);
                compressSet.get(index).updateStatistic(vector1);
                rsIterator.previous();
                rsIterator.remove();
            }
        }
        System.out.println("initCS()");
        //plotClusters();
    }

    private void assignDS() {
        // watching rs
        Iterator<Vector> rsIterator = retainedSet.getVectors().iterator();
        while (rsIterator.hasNext()) {
            Vector vector = rsIterator.next();
            for (int i = 0; i < numberOfClusters; i++) {
                Cluster c = discardSet.get(i);

                if (ConfidenceInterval.isEntered(c, vector)) {
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
                if (ConfidenceInterval.isEntered(cs, ds.getCentroid())) {
                    ds.updateStatistic(cs);
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
                if (ConfidenceInterval.isEntered(cs, vector)) {
                    cs.updateStatistic(vector);
                    rsIterator.remove();
                    break;
                }
            }
        }

        // Merging compressed sets in the CS
        ListIterator<Cluster> csIterator = compressSet.listIterator();
        while (csIterator.hasNext()) {
            Cluster cs1 = csIterator.next();
            Cluster cs2 = null;
            if (csIterator.hasNext()) {
                cs2 = csIterator.next();
            }
            if (cs1 == null || cs2 == null) break;

            if (ConfidenceInterval.isEntered(cs1, cs2)) {
                cs1.updateStatistic(cs2);
                csIterator.remove();
            }
        }
    }

    private void assignRS() {
        System.out.println("before: " + retainedSet.getVectors().size());
        retainedSet.updateRS();
        System.out.println("after: " + retainedSet.getVectors().size());
    }

    private void finish() {
        double distance;
        ArrayList<Double> distances = new ArrayList<>();

        // cs
        Iterator<Cluster> csIterator = compressSet.iterator();
        while (csIterator.hasNext()) {
            Cluster cs = csIterator.next();
            for (int i = 0; i < numberOfClusters; i++) {
                Cluster ds = discardSet.get(i);
                distance = new MahalanobisDistance().calculate(ds, cs.getCentroid());
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
            discardSet.get(id).updateStatistic(cs);
            csIterator.remove();
            distances = new ArrayList<>();
        }

        // retained vectors
        Iterator<Vector> rsIterator = retainedSet.getVectors().iterator();
        while (rsIterator.hasNext()) {
            Vector vector = rsIterator.next();
            for (int i = 0; i < numberOfClusters; i++) {
                Cluster ds = discardSet.get(i);
                distance = new MahalanobisDistance().calculate(ds, vector);
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
            double retainedSetSizeBefore = retainedSet.getVectors().size();
            assignDS();

            if (compressSet.size() == 0) {
                initCS();
                assignCS();
            }
            else {
                assignCS();
            }

            // if vectors in buffer is not enough close
            // compress this vectors and update points in buffer
            if (retainedSet.getVectors().size() == retainedSetSizeBefore) {
                finish();
            }

            assignRS();

            iteration++;

            System.out.println("Iteration: " + iteration);
            //plotClusters();

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
