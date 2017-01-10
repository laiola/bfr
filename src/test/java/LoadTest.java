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
        [ 3692892135, 3592977497, 3691572319, 3779686550, 3821843868, 3733543613, 3804707591, 3796024395, 3759568454, 3730510311]

         */
    }

    public void test() {
        super.test(PATH);
    }

    @Test
    public void testOfTheSameResult() {
       /* ArrayList<Vector> vectors = BFRBuffer.getData(PATH);

        BFR algorithm = new BFR(ITest.NUMBER_OF_CLUSTERS, vectors);

        algorithm.bfr();
        ArrayList<Cluster> actual = algorithm.getDiscardSet();

        vectors = BFRBuffer.getData(PATH);
        algorithm = new BFR(5, vectors);
        algorithm.bfr();
        ArrayList<Cluster> expected = algorithm.getDiscardSet();

        assertEquals(expected, actual);*/
    }

    @Test
    public void testSize() {
        ArrayList<Vector> vectors = BFRBuffer.getData(PATH);
        assertEquals(100000, vectors.size());
        assertEquals(100, vectors.get(0).getCoordinates().size());
    }
}
