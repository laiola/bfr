import bfr.BFR;
import bfr.BFRBuffer;
import bfr.Cluster;
import bfr.Vector;
import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ITest {
    public static final int NUMBER_OF_CLUSTERS = 5;
    private ArrayList<ArrayList<Cluster>> results = new ArrayList<>();
    private ArrayList<Cluster> result = new ArrayList<>();

    public synchronized void combine() {
        for (int i = 0, size = results.size() - 1; i < size; i++) {
            ArrayList<Cluster> clusters1 = results.get(i);
            ArrayList<Cluster> clusters2 = results.get(i + 1);

            for (int j = 0, n = clusters1.size(); j < n; j++) {
                clusters2.get(j).updateStatistic(clusters1.get(j));
            }
        }
        result = results.get(results.size() - 1);
        int m = 1;
        for (Cluster ds: result) {
            System.out.println("[" + m + "]: " + ds.getStatistic().getN() + "\t" + ds.getStatistic().toString());
            m++;
        }
    }

    public void test(String path) {
        CopyOnWriteArrayList<Long> times = new CopyOnWriteArrayList<>();
        ArrayList<Vector> vectors = BFRBuffer.getData(path);

        int numberOfThreads = 8;
        int chunksSize = vectors.size() / numberOfThreads;
        List<List<Vector>> chunks = ListUtils.partition(vectors, chunksSize);

        //for (int i = 0; i < 1; i++) {
            chunks.parallelStream().forEach(element -> {
                BFR algorithm = new BFR(NUMBER_OF_CLUSTERS, element);

                long start = System.nanoTime();
                algorithm.bfr();
                results.add(algorithm.getInformation());
                long end = System.nanoTime();
                times.add(end - start);
            });

        //}7755673005 7836604477
        //3462037790  7666459118
        System.out.println(times.toString());
        System.out.println(times.stream().reduce((s1, s2) -> s1 + s2).orElse(null ));
    }
}
