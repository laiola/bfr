package parallel;

import bfr.BFR;
import bfr.BFRBuffer;
import bfr.Cluster;
import bfr.Vector;
import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class IParallelTest {
    public static final int NUMBER_OF_CLUSTERS = 5;
    private ArrayList<ArrayList<Cluster>> results = new ArrayList<>();
    private ArrayList<Cluster> result = new ArrayList<>();
    private Long time = 0L;

    public  ArrayList<Cluster> combine() {
        long start = System.nanoTime();
        for (int i = 0, size = results.size() - 1; i < size; i++) {
            ArrayList<Cluster> clusters1 = results.get(i);
            ArrayList<Cluster> clusters2 = results.get(i + 1);

            for (int j = 0, n = clusters1.size(); j < n; j++) {
                clusters2.get(j).updateStatistic(clusters1.get(j));
            }
        }
        long end = System.nanoTime();
        this.time += end - start;
        System.out.println(time);

        result = results.get(results.size() - 1); // getting the last one
        int m = 1;
        int res = 0;

        for (Cluster ds: result) {
            res += ds.getStatistic().getN();

            System.out.println("[" + m + "]: " + ds.getStatistic().getN() + "\t" + ds.getStatistic().toString());
            m++;
        }
        System.out.println(res);
        return result;
    }

    public void test(String path) {
        CopyOnWriteArrayList<Long> times = new CopyOnWriteArrayList<>();
        ArrayList<Vector> vectors = BFRBuffer.getData(path);

        int numberOfThreads = 8;
        int chunksSize = vectors.size() / numberOfThreads;
        List<List<Vector>> chunks = ListUtils.partition(vectors, chunksSize);

        chunks.parallelStream().forEach(element -> {
            BFR algorithm = new BFR(NUMBER_OF_CLUSTERS, element);

            long start = System.nanoTime();
            algorithm.bfr();
            results.add(algorithm.getInformation());
            long end = System.nanoTime();
            times.add(end - start);
        });

        this.time = times.stream().reduce((s1, s2) -> s1 + s2).orElse(0L);
        // System.out.println(times.toString());
        //System.out.println(times.stream().reduce((s1, s2) -> s1 + s2).orElse(null ));
    }
}
