package com.oyster.lab02.utils;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by andriybas on 10/12/14.
 */
public class Experiment {

    final Random rand = new Random();

    final int[] romik_m = new int[]{
            4, 6, 8, 10, 12, 15, 20
    };
    // with p == 0.95
    final double[] romik = new double[]{
            1.71, 2.10, 2.27, 2.41, 2.52, 2.64, 2.78
    };

    final int N;

    final double x1min = -10;
    final double x1max = 50;
    final double x2min = 20;
    final double x2max = 60;

    final double yMin = 190;
    final double yMax = 290;

    final int minM = 5; // min M


    List<Pair<Double, Double>> xMinMax;

    List<List<Double>> x = new ArrayList<>();
    List<List<Double>> y = new ArrayList<>();

    List<Double> yNSum = new ArrayList<>();
    List<Double> yNAvg = new ArrayList<>();

    List<Double> sigmaN = new ArrayList<>();
    List<List<Double>> Fnn = new ArrayList<>();

    public Experiment(List<Pair<Double, Double>> xMinMax) {
        this.xMinMax = xMinMax;

        int k = xMinMax.size();
        this.N = k;
        int n = (1 << k) - 1;

        // generate N == k experiments
        while (k > 0) {
            if (k >= n || rand.nextBoolean()) {
                k--;
                List<Double> xi = new ArrayList<>(k);
                for (int i = 0; i < k; i++)
                    xi.add(((1 << i) & n) > 0 ? -1.0 : 1.0);
                x.add(xi);
            }
            n--;
        }

        for (int i = 0; i < N; i++) {
            yNAvg.add(0.0);
            yNSum.add(0.0);
            sigmaN.add(0.0);

            List<Double> l = new ArrayList<Double>(N);
            for (int j = 0; j < N; j++)
                l.add(0.0);
            Fnn.add(l);

        }

        for (int i = 0; i < minM; i++)
            runExperiment();


    }

    private void runExperiment() {
        List<Double> yi = new ArrayList<>();

        int m = y.size() + 1;
        for (int i = 0; i < N; i++) {
            double r = yMin + (rand.nextDouble() * (yMax - yMin));
            yi.add(r);
            yNSum.set(i, yNSum.get(i) + r);
            yNAvg.set(i, yNSum.get(i) / m);
        }

        y.add(yi);

    }

    private boolean isDispersionUniform() {

        double m = y.size();
        for (int i = 0; i < N; i++) {

            double t = 0.0;
            for (int j = 0; j < y.size(); j++)
                t += Math.pow(y.get(i).get(j) - yNAvg.get(i), 2.0);
            sigmaN.set(i, t / m);
        }

        double sigmaMainDiff = Math.sqrt(2.0 * (2.0 * m - 2.0) / (m * (m - 4.0)));

        double Rkr = romik[0];
        int ii = 0;
        while (ii < romik.length && romik_m[ii] < y.size()) ii++;
        Rkr = romik[ii];

        // TODO : can optimize here, run only half of matrix
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {

                // 1-st formula
                double r = sigmaN.get(i) / sigmaN.get(j);
                if (r < 1.0)
                    r = 1.0 / r;

                // 2 - nd formula
                r = r * (m - 2.0) / m;

                r = Math.abs(r - 1.0) / sigmaMainDiff;

                Fnn.get(i).set(j, r);
            }
        }

        // checking
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++)
                if (Fnn.get(i).get(j) > Rkr)
                    return false;
        }

        return true;
    }


}