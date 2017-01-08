package bfr;

import java.util.ArrayList;

/**
 * Buffer of points that are not close enough to sub-clusters or cluster
 */
public class RetainedSet {
    private final BFRBuffer bfrBuffer;
    private final ArrayList<Vector> vectors = new ArrayList<>();
    private final int size;

    public RetainedSet(ArrayList<Vector> vectors) {
        this.bfrBuffer = new BFRBuffer(vectors);
        this.size = BFRBuffer.DEFAULT_SIZE;

        for (int i = 0; i < BFRBuffer.DEFAULT_SIZE; i++) {
            vectors.add(bfrBuffer.remove());
        }
    }

    public boolean isEnd() {
        return bfrBuffer.isEmpty();
    }

    public void updateRS() {
        bfrBuffer.UpdateBuffer();
        while (!bfrBuffer.isEmpty() && vectors.size() < size) {
            vectors.add(bfrBuffer.remove());
        }
    }

    public ArrayList<Vector> getVectors() {
        return vectors;
    }
}