package bfr;

import java.util.ArrayList;
import java.util.Arrays;

public class SufficientStatistic {
    private final double[] sum;
    private final double[] sumsq;
    private final double[] centroid;

    private int n = 0;

    public SufficientStatistic(int numberOfAttributes) {
        this.centroid = new double[numberOfAttributes];
        this.sum = new double[numberOfAttributes];
        this.sumsq = new double[numberOfAttributes];
    }

    private void centr() {
        for (int i = 0; i < centroid.length; i++) {
            centroid[i]=sum[i]/n;
        }
    }

    public double[] getSum() {
        return sum;
    }

    public double[] getSumsq() {
        return sumsq;
    }

    public int getN() {
        return n;
    }

    public void update(Vector vector) {
        n++;
        for (int i = 0, length = sum.length; i < length; i++) {
            sum[i] += vector.getCoordinates().get(i);
            sumsq[i] += Math.pow(vector.getCoordinates().get(i), 2);
        }
        centr();
    }

    public Vector getCentroid() {
        ArrayList<Double> coordinates = new ArrayList<>();
        for (double aSum : sum) {
            if (n != 0) {
                coordinates.add(aSum / n);
            } else {
                return Vector.createRandomPoint(-100, 100);
            }
        }
        return new Vector(coordinates);
    }

    @Override
    public String toString() {
        //centr();
        return "SufficientStatistic{" +
                "n=" + n +
                ", sum=" + Arrays.toString(sum) +
                ", sumsq=" + Arrays.toString(sumsq) +
                '}';
    }

    public void update(Cluster tmp) {
        SufficientStatistic tempStat = tmp.getStatistic();
        double[] tmpsum = tempStat.getSum();
        double[] tmpsumsq = tempStat.getSumsq();

        n += tempStat.getN();
        for (int i = 0, length = sum.length; i < length; i++) {
            sum[i] += tmpsum[i];
            sumsq[i] += tmpsumsq[i];
        }
        centr();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SufficientStatistic)) return false;

        SufficientStatistic that = (SufficientStatistic) o;

        if (n != that.n) return false;
        if (!Arrays.equals(sum, that.sum)) return false;
        if (!Arrays.equals(sumsq, that.sumsq)) return false;
        return Arrays.equals(centroid, that.centroid);

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(sum);
        result = 31 * result + Arrays.hashCode(sumsq);
        result = 31 * result + Arrays.hashCode(centroid);
        result = 31 * result + n;
        return result;
    }
}
