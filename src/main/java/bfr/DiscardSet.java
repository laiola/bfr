package bfr;

/**
 * Main clusters, all theirs points are discarded and are summarized
 */
public class DiscardSet {
    private SufficientStatistic statistic;

    public DiscardSet(int numberOfAttributes) {
        this.statistic = new SufficientStatistic(numberOfAttributes);
    }

    public SufficientStatistic getStatistic() {
        return statistic;
    }

    public void setStatistic(SufficientStatistic statistic) {
        this.statistic = statistic;
    }

    public void updateStatistic(Vector vector) {
        this.statistic.update(vector);
    }

    public Vector getCentroid() {
        return statistic.getCentroid();
    }

    @Override
    public String toString() {
        return "statistic=" + statistic.toString() +
                '}';
    }

    public void updateStatistic(CompressSet tmp) {
        this.statistic.update(tmp);
    }
}
