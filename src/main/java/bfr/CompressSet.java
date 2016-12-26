package bfr;

/**
 * Sub-clusters, all theirs points are discarded and are summarized
 */
public class CompressSet extends DiscardSet {
    public CompressSet(int numberOfAttributes) {
        super(numberOfAttributes);
    }
}
