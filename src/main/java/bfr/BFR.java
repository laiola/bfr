package bfr;

import one.util.streamex.StreamEx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class BFR {
    static final int NUMBER_OF_ATTRIBUTES = 100;
    private static final int processors = Runtime.getRuntime().availableProcessors() - 1;
    private static final int MAX_ITERATIONS = 2000;
    private final CopyOnWriteArrayList<Cluster> discardSet;
    private final CopyOnWriteArrayList<Cluster> compressSet;
    private final RetainedSet retainedSet;
    private int numberOfClusters;
    private int numberOfAttributes;

    public BFR(int numberOfClusters, CopyOnWriteArrayList<Vector> vectors) {
        this.numberOfClusters = numberOfClusters;
        this.numberOfAttributes = vectors.get(0).getCoordinates().size();
        this.discardSet = new CopyOnWriteArrayList<>();
        this.compressSet = new CopyOnWriteArrayList<>();
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

    public void getInformation() {
        int i = 1;
        for (Cluster ds: discardSet) {
            System.out.println("[" + i + "]: " + ds.getStatistic().getN());
            i++;
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

        discardSet.parallelStream().forEach((cluster) -> {
            ArrayList<Double> vector = new ArrayList<>(numberOfAttributes);
            vector.add((double) 0);
            int j = 1;
            for (int k = 1; k < numberOfAttributes; k++) {
                vector.add((double) j);
            }
            j++;
            cluster.updateStatistic(new Vector(vector));
        });
    }

    private void initCS() {
        // Create sub-clusters
        CopyOnWriteArrayList<Vector> temp = new CopyOnWriteArrayList<>();

        StreamEx.of(retainedSet.getVectors()).parallel().pairMap( (v1, v2) -> {
            Vector vector1 = v1;
            Vector vector2 = v2;

            if (ConfidenceInterval.isEntered(vector1, vector2)) {
                compressSet.add(new Cluster(numberOfAttributes));
                temp.add(vector1);
                temp.add(vector2);
                int index = compressSet.size() - 1;
                compressSet.get(index).updateStatistic(vector2);
                compressSet.get(index).updateStatistic(vector1);
            }
            return null;
        });
        retainedSet.getVectors().removeAll(temp);
    }

    private void assignDS() {
        // watching rs
        CopyOnWriteArrayList<Vector> temp = new CopyOnWriteArrayList<>();

        StreamEx.of(retainedSet.getVectors()).parallel().forEachOrdered(vector -> {
            StreamEx.of(discardSet).parallel().forEachOrdered(cluster -> {
                if (ConfidenceInterval.isEntered(cluster, vector)) {
                    cluster.updateStatistic(vector);
                    temp.add(vector);
                }
            });
        });

        retainedSet.getVectors().removeAll(temp);

        CopyOnWriteArrayList<Cluster> temp2 = new CopyOnWriteArrayList<>();
        StreamEx.of(compressSet).parallel().forEachOrdered();
        // watching cs
        for (int i = 0; i < numberOfClusters; i++) {
            Cluster ds = discardSet.get(i);
            Iterator<Cluster> csIterator = compressSet.iterator();
            while (csIterator.hasNext()) {
                Cluster cs = csIterator.next();
                if (ConfidenceInterval.isEntered(cs, ds.getCentroid())) {
                    ds.updateStatistic(cs);
                    temp2.add(cs);
                    break;
                }
            }
        }
        compressSet.removeAll(temp2);
    }

    private void assignCS() {
        Iterator<Vector> rsIterator = retainedSet.getVectors().iterator();

        CopyOnWriteArrayList<Vector> tempV = new CopyOnWriteArrayList<>();
        while (rsIterator.hasNext()) {
            Vector vector = rsIterator.next();
            for (Cluster cs : compressSet) {
                if (ConfidenceInterval.isEntered(cs, vector)) {
                    cs.updateStatistic(vector);
                    tempV.add(vector);
                    break;
                }
            }
        }
        retainedSet.getVectors().removeAll(tempV);

        CopyOnWriteArrayList<Cluster> tempC = new CopyOnWriteArrayList<>();
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
                tempC.add(cs2);
            }
        }
        compressSet.removeAll(tempC);
    }

    private void assignRS() {
        //System.out.println("before: " + retainedSet.getVectors().size());
        retainedSet.updateRS();
        //System.out.println("after: " + retainedSet.getVectors().size());
    }

    private void finish() {
        double distance;
        ArrayList<Double> distances = new ArrayList<>();

        // cs
        Iterator<Cluster> csIterator = compressSet.iterator();
        CopyOnWriteArrayList<Cluster> tempCl = new CopyOnWriteArrayList<>();
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
            tempCl.add(cs);
            distances = new ArrayList<>();
        }
        compressSet.removeAll(tempCl);

        // retained vectors
        Iterator<Vector> rsIterator = retainedSet.getVectors().iterator();
        CopyOnWriteArrayList<Vector> temp = new CopyOnWriteArrayList<>();
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
            temp.add(vector);
            distances = new ArrayList<>();
        }
        retainedSet.getVectors().removeAll(temp);
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
            plotClusters();

            if (retainedSet.getVectors().isEmpty() || iteration > MAX_ITERATIONS) {
                finish = true;
            }
        }
    }

    public CopyOnWriteArrayList<Cluster> getDiscardSet() {
        return discardSet;
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
