package bfr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class BFR {
    static final int NUMBER_OF_ATTRIBUTES = 100;
    private static final int MAX_ITERATIONS = 2000;
    private final ArrayList<Cluster> discardSet;
    private final ArrayList<Cluster> compressSet;
    private final RetainedSet retainedSet;
    private int numberOfClusters;
    private int numberOfAttributes;

    public BFR(int numberOfClusters, List<Vector> vectors) {
        this.numberOfClusters = numberOfClusters;
        this.numberOfAttributes = vectors.get(0).getCoordinates().size();
        this.discardSet = new ArrayList<>();
        this.compressSet = new ArrayList<>();
        this.retainedSet = new RetainedSet(vectors);
    }

    public void bfr() {
        init();
        calculate();
        finish();
    }

    public ArrayList<Cluster> getInformation() {
        return discardSet;
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
        /*// Set Random Centroids
        for (int i = 0; i < numberOfClusters; i++) {
            discardSet.get(i).updateStatistic(Vector.createRandomPoint(BFRBuffer.MIN, BFRBuffer.MAX));
        }*/

        // Set Centroids for 10.txt
        /*ArrayList<Double> vector = new ArrayList<>(numberOfAttributes);
        vector.add((double) 1);
        vector.add((double) 1);

        ArrayList<Double> vector2 = new ArrayList<>(numberOfAttributes);
        vector2.add((double) 5);
        vector2.add((double) 1);
        discardSet.get(0).updateStatistic(new Vector(vector));
        discardSet.get(1).updateStatistic(new Vector(vector2));*/

        // Set Centroids
        for (int i = 0, j = 1; i < numberOfClusters; i++) {
            ArrayList<Double> vector = new ArrayList<>(numberOfAttributes);
            vector.add((double) 0);
            for (int k = 1; k < numberOfAttributes; k++) {
                vector.add((double) j);
            }
            j++;
            discardSet.get(i).updateStatistic(new Vector(vector));
        }
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
            else rsIterator.previous();

        }
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

        if (!compressSet.isEmpty()) {
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
    }

    private void assignCS() {
        if (!compressSet.isEmpty()) {
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
    }

    private void assignRS() {
        retainedSet.updateRS();
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

            if (retainedSet.getVectors().isEmpty() || iteration > MAX_ITERATIONS) {
                finish = true;
            }
        }
    }

    public ArrayList<Cluster> getDiscardSet() {
        return discardSet;
    }

}
