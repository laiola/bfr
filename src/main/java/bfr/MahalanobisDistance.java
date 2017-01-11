package bfr;

import java.util.ArrayList;

/**
 * Calculates Mahalanobis distance by using sufficient statistic
 * sigma = SUMSQi/N-(SUMi/N)^2
 * deviation = sqrt(sigma)
 */
public class MahalanobisDistance {
    public double msigma;

    public double calculate(Cluster ds, Vector vector) {
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
            double sigma = Math.pow(centroid.get(i) - coords.get(i), 2);
            distance += Math.pow((coords.get(i) - centroid.get(i)), 2);
            msigma += Math.sqrt(sigma);
        }
        return Math.sqrt(distance);
    }

    public double calculate(Vector vector1, Vector vector2) {
        ArrayList<Double> coords1 = vector1.getCoordinates();
        ArrayList<Double> coords2 = vector2.getCoordinates();
        double distance = 0;
        msigma = 0;

        int length = coords1.size();

        for (int i = 0; i < length; i++) {
            double sigma = Math.pow((coords1.get(i) - coords2.get(i)), 2);
            distance += Math.pow((coords1.get(i) - coords2.get(i)), 2);
            msigma += Math.sqrt(sigma);
        }
        return Math.sqrt(distance);
    }

    public double getSigma() {
        return msigma;
    }

    public double calculate(Cluster cs1, Cluster cs2) {
        double distance = 0;
        ArrayList<Double> cs1_centroid = cs1.getCentroid().getCoordinates();
        ArrayList<Double> cs2_centroid = cs2.getCentroid().getCoordinates();

        int length = cs1_centroid.size();
        SufficientStatistic statisticCS1 = cs1.getStatistic();
        SufficientStatistic statisticCS2 = cs2.getStatistic();

        int number = statisticCS1.getN() + statisticCS2.getN();
        double[] sum = statisticCS1.getSum();
        double[] sumsq = statisticCS1.getSumsq();
        msigma = 0;

        for (int i = 0; i < length; i++) {
            double sigma = Math.pow((cs2_centroid.get(i) - cs1_centroid.get(i)), 2);
            distance += Math.pow((cs2_centroid.get(i) - cs1_centroid.get(i)), 2);
            msigma += Math.sqrt(sigma);
        }
        return Math.sqrt(distance);
    }
}
