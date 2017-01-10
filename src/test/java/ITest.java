import bfr.BFR;
import bfr.BFRBuffer;
import bfr.Vector;

import java.util.concurrent.CopyOnWriteArrayList;

public class ITest {
    public static final int NUMBER_OF_CLUSTERS = 5;

    public void test(String path) {
        CopyOnWriteArrayList<Long> times = new CopyOnWriteArrayList<>();

        for (int i = 0; i < 1; i++) {
            CopyOnWriteArrayList<Vector> vectors = new CopyOnWriteArrayList<>(BFRBuffer.getData(path));
            BFR algorithm = new BFR(NUMBER_OF_CLUSTERS, vectors);

            long start = System.nanoTime();
            algorithm.bfr();
            algorithm.getInformation();
            long end = System.nanoTime();
            times.add(end - start);
        }
        System.out.println(times.toString());
    }
}
