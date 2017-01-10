package bfr;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Buffer of points that are not close enough to sub-clusters or cluster
 */
public class RetainedSet {
    private final BFRBuffer bfrBuffer;
    private final CopyOnWriteArrayList<Vector> vectors = new CopyOnWriteArrayList<>();
    private final int size;

    public RetainedSet(CopyOnWriteArrayList<Vector> vectors) {
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

    public CopyOnWriteArrayList<Vector> getVectors() {
        return vectors;
    }
}