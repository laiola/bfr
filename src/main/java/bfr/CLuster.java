package bfr;

/**
 * Main clusters, all theirs points are discarded and are summarized.
 * And sub-clusters, all theirs points are discarded and are summarized.
 */
public class Cluster {
    private SufficientStatistic statistic;

    public Cluster(int numberOfAttributes) {
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

    public void updateStatistic(Cluster tmp) {
        this.statistic.update(tmp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cluster)) return false;

        Cluster cluster = (Cluster) o;

        return statistic != null ? statistic.equals(cluster.statistic) : cluster.statistic == null;

    }

    @Override
    public int hashCode() {
        return statistic != null ? statistic.hashCode() : 0;
    }
}
