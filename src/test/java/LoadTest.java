import bfr.BFRBuffer;
import bfr.Vector;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class LoadTest extends ITest{
    private static final String PATH = "src/test/resources/100_000.txt";

    public static void main (String[] args) throws IOException {
        new LoadTest().test();
        /*
        [23300443361, 23644617582, 23247538074, 23151162193, 24155564234, 22926721756, 23787779241, 22771874033, 22716148776, 22808545867]
         */
    }

    public void test() {
        super.test(PATH);
    }

    @Test
    public void testSize() {
        ArrayList<Vector> vectors = BFRBuffer.getData(PATH);
        assertEquals(100000, vectors.size());
        assertEquals(100, vectors.get(0).getCoordinates().size());
    }
}
