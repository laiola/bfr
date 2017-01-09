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
    }

    public void test() {
        super.test(PATH);
    }

    @Test
    public void testSize() {
        ArrayList<Vector> vectors = BFRBuffer.getData(PATH);
        assertEquals(1000, vectors.size());
        assertEquals(100, vectors.get(0).getCoordinates().size());
    }
}
