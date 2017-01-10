import bfr.BFRBuffer;
import bfr.Vector;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class SimpleTest extends ITest{
    private static final String PATH = "src/test/resources/1000.txt";

    public static void main (String[] args) throws IOException{
        new SimpleTest().test();
        /*
        [98110985, 38883663, 39387411, 33121129, 27016979, 30959361, 35838415, 37794530, 40389505, 27116819]

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
        assertEquals(1000, vectors.size());
        assertEquals(100, vectors.get(0).getCoordinates().size());
    }
}
