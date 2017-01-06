package bfr;

import java.util.ArrayList;

/**
 * Calculates Mahalanobis distance by using sufficient statistic
 * sigma = SUMSQi/N-(SUMi/N)^2
 * deviation = sqrt(sigma)
 */
public class MahalanobisDistance {
    public static double msigma;

    public static double calculate(Cluster ds, Vector vector) {
        double distance = 0;
        ArrayList<Double> coords = vector.getCoordinates();
        ArrayList<Double> centroid = ds.getCentroid().getCoordinates();

        int length = coords.size();
        SufficientStatistic statistic = ds.getStatistic();
        int number = statistic.getN() + 1;
        double[] sum = statistic.getSum();
        double[] sumsq = statistic.getSumsq();
        msigma = 0;

        for (int i = 0; i < length; i++) {
            double sigma = (sumsq[i] + Math.pow(coords.get(i), 2) )/ number
                    - Math.pow(((sum[i] + coords.get(i)) / number), 2);
            distance += Math.pow(((coords.get(i) - centroid.get(i)) / Math.sqrt(sigma)), 2);
            msigma += sigma;
        }
        return Math.sqrt(distance);
    }

    public static double calculate(Vector vector1, Vector vector2) {
        ArrayList<Double> coords1 = vector1.getCoordinates();
        ArrayList<Double> coords2 = vector2.getCoordinates();
        double distance = 0;
        msigma = 0;

        int length = coords1.size();

        for (int i = 0; i < length; i++) {
            double sigma = (Math.pow(coords1.get(i), 2) + Math.pow(coords2.get(i), 2)) / 2
                    - Math.pow(((coords1.get(i) + coords2.get(i)) / 2), 2);
            distance += Math.pow(((coords1.get(i) - coords2.get(i)) / Math.sqrt(sigma)), 2);
            msigma += sigma;
        }
        return Math.sqrt(distance);
    }

    public static double getSigma() {
        return msigma;
    }
}
