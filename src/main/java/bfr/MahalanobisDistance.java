package bfr;

import java.util.ArrayList;

/**
 * Calculates Mahalanobis distance by using sufficient statistic
 * sigma = SUMSQi/N-(SUMi/N)^2
 * distance = sqrt(sigma)
 */
public class MahalanobisDistance {

    public static double calculate(DiscardSet ds, Vector vector) {
        double sigma = 0;
        ArrayList<Double> coords = vector.getCoordinates();
        int length = coords.size();
        SufficientStatistic statistic = ds.getStatistic();
        int number = statistic.getN();
        double[] sum = statistic.getSum();
        double[] sumsq = statistic.getSumsq();

        for (int i = 0; i < length; i++) {
            sigma += (sumsq[i] + Math.pow(coords.get(i), 2) )/ number - Math.pow(((sum[i] + coords.get(i)) / number), 2);
        }
        return Math.sqrt(sigma);
    }

    public static double calculate(Vector vector1, Vector vector2) {
        double sigma = 0;
        ArrayList<Double> coords1 = vector1.getCoordinates();
        ArrayList<Double> coords2 = vector2.getCoordinates();

        int length = coords1.size();

        for (int i = 0; i < length; i++) {
            sigma += (Math.pow(coords1.get(i), 2) + Math.pow(coords2.get(i), 2)) / 2
                    - Math.pow(((coords1.get(i) + coords2.get(i)) / 2), 2);

            //sigma += Math.pow(coords1.get(i) - coords2.get(i), 2);
        }
        //System.out.println(Math.sqrt(sigma));
        return Math.sqrt(sigma);
    }

}
