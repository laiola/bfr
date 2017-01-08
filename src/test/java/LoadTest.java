import bfr.BFR;
import bfr.BFRBuffer;
import bfr.Vector;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class LoadTest {
    private static final String PATH = "src/test/resources/100_000.txt";

    public static void main (String[] args) throws IOException {
        ArrayList<Vector> vectors = BFRBuffer.getData(PATH);
        BFR algorithm = new BFR(5, vectors);
        algorithm.bfr();
    }

    @Test
    public void testSize() {
        ArrayList<Vector> vectors = BFRBuffer.getData(PATH);
        assertEquals(100000, vectors.size());
        assertEquals(100, vectors.get(0).getCoordinates().size());
    }
}
