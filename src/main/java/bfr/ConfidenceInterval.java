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
        double distance = MahalanobisDistance.calculate(cluster, vector);

        int n = cluster.getStatistic().getN() + 1;
        double deviation = Math.sqrt(MahalanobisDistance.getSigma()) / n;

        double standardError = deviation / Math.sqrt(n);

        return distance < standardError * 2;
    }

    public static boolean isEntered(Vector vector1, Vector vector2) {
        double distance = MahalanobisDistance.calculate(vector1, vector2);

        int n = 2;
        double deviation = Math.sqrt(MahalanobisDistance.getSigma()) / n;
        double standardError = deviation / Math.sqrt(n);

        return distance < standardError * 2;
    }
}
