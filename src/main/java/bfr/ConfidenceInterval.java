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
        double lowerLimit;
        double upperLimit;

        double[] sum = cluster.getStatistic().getSum();
        double[] sumsq = cluster.getStatistic().getSumsq();
        int n = cluster.getStatistic().getN();
        double deviation = 0;
        double means = 0;

        for (int i = 0, size = sum.length; i < size; i++) {
            double sigma = sumsq[i] / n - Math.pow(sum[i] / n, 2);
            deviation += Math.sqrt(sigma);
            means += sum[i];
        }

        deviation = deviation / n;
        means = means / n;

        double standartError = deviation / Math.sqrt(n);
        double marginOfError = COEFFICIENT * standartError;
        lowerLimit = means - marginOfError;
        upperLimit = means + marginOfError;

        if (lowerLimit < distance && distance < upperLimit) {
            return true;
        }
        return false;
    }

    public static boolean isEntered(Vector vector1, Vector vector2) {
        double distance = MahalanobisDistance.calculate(vector1, vector2);
        double lowerLimit;
        double upperLimit;

        int n = vector1.getCoordinates().size();
        double[] sum = new double[n];
        double[] sumsq = new double[n];

        for (int i = 0; i < n; i++) {
            double tmp = vector1.getCoordinates().get(i);
            sum[i] = tmp;
            sumsq[i] = Math.pow(tmp, 2);
        }
        double deviation = 0;
        double means = 0;

        for (int i = 0, size = sum.length; i < size; i++) {
            double sigma = sumsq[i] / n - Math.pow(sum[i] / n, 2);
            deviation += Math.sqrt(sigma);
            means += sum[i];
        }

        deviation = deviation / n;
        means = means / n;

        double standartError = deviation / Math.sqrt(n);
        double marginOfError = COEFFICIENT * standartError;
        lowerLimit = means - marginOfError;
        upperLimit = means + marginOfError;

        if (lowerLimit < distance && distance < upperLimit) {
            return true;
        }
        return false;
    }
}
