package bfr;

/**
 *  standart error = sigma / sqrt (n)
 *  margin of error = 2 * standartError
 *  lower limit = means - marginOfError
 *  upper limit = means + marginOfError
 */
public class ConfidenceInterval {
    // 68%
    private static final int COEFFICIENT = 1;

    public static boolean isEntered(Cluster cluster, Vector vector) {
        MahalanobisDistance md = new MahalanobisDistance();
        double distance = md.calculate(cluster, vector);
        double sigma = md.getSigma();

        int n = cluster.getStatistic().getN() + 1;

        double deviation = sigma / n;
        double standardError = deviation / Math.sqrt(n);

        return distance < 2;
    }

    public static boolean isEntered(Vector vector1, Vector vector2) {
        MahalanobisDistance md = new MahalanobisDistance();
        double distance = md.calculate(vector1, vector2);
        double sigma = md.getSigma();

        int n = 2;

        double deviation = sigma / n;
        double standardError = deviation / Math.sqrt(n);

        return distance < 2;
    }

    public static boolean isEntered(Cluster cs1, Cluster cs2) {
        MahalanobisDistance md = new MahalanobisDistance();
        double distance = md.calculate(cs1, cs2);
        double sigma = md.getSigma();

        int n = cs1.getStatistic().getN() + cs2.getStatistic().getN();

        double deviation = sigma / n;
        double standardError = deviation / Math.sqrt(n);

        return distance < 2;
    }
}
