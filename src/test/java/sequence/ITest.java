package sequence;

import bfr.BFR;
import bfr.BFRBuffer;
import bfr.Vector;

import java.util.ArrayList;

public class ITest {
    public static final int NUMBER_OF_CLUSTERS = 5;

    public void test(String path) {
        ArrayList<Long> times = new ArrayList<>();
        ArrayList<Vector> vectors = BFRBuffer.getData(path);

        for (int i = 0; i < 10; i++) {
            BFR algorithm = new BFR(NUMBER_OF_CLUSTERS, vectors);

            long start = System.nanoTime();
            algorithm.bfr();
            long end = System.nanoTime();
            times.add(end - start);

            // Вывод результатов
            /*ArrayList<Cluster> res = algorithm.getDiscardSet();
            for (Cluster cluster: res) {
                System.out.println(cluster.getStatistic().getN());
            }*/
        }
        System.out.println(times.toString());
    }
}
