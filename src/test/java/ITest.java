import bfr.BFR;
import bfr.BFRBuffer;
import bfr.Vector;

import java.util.ArrayList;

public class ITest {
    public static final int NUMBER_OF_CLUSTERS = 5;

    public void test(String path) {
        ArrayList<Long> times = new ArrayList<>(5);

        for (int i = 0; i < 1; i++) {
            ArrayList<Vector> vectors = BFRBuffer.getData(path);
            BFR algorithm = new BFR(NUMBER_OF_CLUSTERS, vectors);

            long start = System.nanoTime();
            algorithm.bfr();
            long end = System.nanoTime();
            times.add(end - start);
        }
        System.out.println(times.toString());
    }
}
