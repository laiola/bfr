package parallel;

import bfr.BFRBuffer;
import bfr.Cluster;
import bfr.Vector;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class SimpleParallelTest extends IParallelTest {
    private static final String PATH = "src/test/resources/1000.txt";

    public static void main (String[] args) throws IOException{
        new SimpleParallelTest().test();
    }

    public ArrayList<Cluster> test() {
        super.test(PATH);
        return super.combine();
    }

    @Test
    public void testOfTheSameResult() {
        ArrayList<Cluster> actual = test();

        IParallelTest test = new IParallelTest();
        test.test(PATH);
        ArrayList<Cluster> expected = test.combine();

        System.out.println("Expected: " + expected.toString());
        System.out.println("Actual: " + actual.toString());
    }

    @Test
    public void testSize() {
        ArrayList<Vector> vectors = BFRBuffer.getData(PATH);
        assertEquals(1000, vectors.size());
        assertEquals(100, vectors.get(0).getCoordinates().size());
    }
}
