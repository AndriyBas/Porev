package com.oyster.lab02.utils;

import android.util.Pair;

import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Experiment {

    public final int N;
    public final double yMin = 190;
    public final double yMax = 290;
    public final int minM = 5; // min M
    final Random rand = new Random();

    //    false double x1min = -10;
//    final double x1max = 50;
//    final double x2min = 20;
//    final double x2max = 60;
    final int[] romik_m = new int[]{
            4, 6, 8, 10, 12, 15, 20
    };
    // with p == 0.95
    final double[] romik = new double[]{
            1.71, 2.10, 2.27, 2.41, 2.52, 2.64, 2.78
    };
    // with q == 0.05
    // first 16 values
    final double[] stud = new double[]{
            12.71, 4.303, 3.182, 2.776, 2.571, 2.447, 2.365, 2.306,
            2.262, 2.228, 2.201, 2.179, 2.160, 2.145, 2.131, 2.120
    };

    final int K;

    private int f1;
    private int f2;
    private boolean[] student;

    public List<Pair<Double, Double>> xMinMax;
    public List<List<Double>> x = new ArrayList<>();
    public List<List<Double>> y = new ArrayList<>();
    public List<Double> yNSum = new ArrayList<>();
    public List<Double> yNAvg = new ArrayList<>();
    public List<Double> sigmaN = new ArrayList<>();
    public List<List<Double>> Fnn = new ArrayList<>();
    public SimpleMatrix res;
    public List<Double> naturalCoef = new ArrayList<Double>();

    public Experiment(List<Pair<Double, Double>> xMinMax) {
        this.xMinMax = xMinMax;

        K = xMinMax.size();
        int k = K + 1;
        this.N = K + 1;
        int n = (1 << K) - 1;

        // generate N == K experiments
        while (k > 0) {
            if (k >= n || rand.nextBoolean()) {
                k--;
                List<Double> xi = new ArrayList<>(K);
                for (int i = 0; i < K; i++)
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

        while (!isDispersionUniform()) {
            runExperiment();
        }

        calculateCoefficients();
        calculateNaturalCoef();
//        Gp();
        f1 = y.size() - 1;
        f2 = N;
        student = student();
        double f = fisher();

    }

    public int getN() {
        return N;
    }

    public int getK() {
        return K;
    }

    public SimpleMatrix getNormCoefficients() {
        return res;
    }

    public List<List<Double>> getY() {
        return y;
    }

    public List<List<Double>> getX() {
        return x;
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
                t += Math.pow(y.get(j).get(i) - yNAvg.get(i), 2.0);
            sigmaN.set(i, t / m);
        }

        double sigmaMainDiff = Math.sqrt(2.0 * (2.0 * m - 2.0) / (m * (m - 4.0)));

        int ii = 0;
        while (ii < romik.length && romik_m[ii] < y.size()) ii++;
        double Rkr = romik[ii];

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

    public double Gp() {
        double maxS = Double.MIN_VALUE;
        double sumS = 0.0;

        double m = y.size();
        f1 = y.size() - 1;
        f2 = N;
        for (int i = 0; i < N; i++) {

            double t = 0.0;
            for (int j = 0; j < y.size(); j++)
                t += Math.pow(y.get(j).get(i) - yNAvg.get(i), 2.0);
            if (t > maxS)
                maxS = t;
            sumS += t;
            sigmaN.set(i, t / m);
        }

        return maxS / sumS;
    }

    public boolean[] student() {
        // стітистично незначущі коефіцієнти позначатимуться false
        boolean[] isCoefGood = new boolean[K+1];
        double m = y.size();

        int f3 = f1*f2;

        /*double sumS = 0.0;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < y.size(); j++) {
                sumS += Math.pow(y.get(j).get(i) - yNAvg.get(i), 2.0);
            }
        }

        double SAvg = sumS/(N*1.0);*/
        // S^2{b}
        double SAvg = varianceAvg();

        double S2b = SAvg/(N*m);
        double Sb = Math.sqrt(S2b);

        for (int i = 0; i < (K + 1); i++) {
            double beta = 0.0;
            for (int j = 0; j < N; j++) {
                if(i == 0) {
                    beta += yNAvg.get(j)*1.0;
                }else{
                    beta += yNAvg.get(j)*x.get(j).get(i-1);
                }
            }
            double b = beta/N;
            //t.add(i, Math.abs(b)/Sb);
            double ti = Math.abs(b)/Sb;

            //piece of shit
            if(f3 > 16){
                if (ti < 2)
                    isCoefGood[i] = false;
                else
                    isCoefGood[i] = true;
            }else{
                if (ti < stud[f3 - 1])
                    isCoefGood[i] = false;
                else
                    isCoefGood[i] = true;

            }

        }
        return isCoefGood;
    }

    public double fisher () {
        double sum =0.0;
        double m = y.size();
        for (int i = 0; i < N; i++) {
            sum += Math.pow(getYi(i)-yNAvg.get(i), 2.0);
        }
        // число значущих коефіцієнтів
        int d = 0;

        for (int i = 0; i <student.length; i++) {
            if(student[i])
                d++;
        }

        double Sad = sum*(m/(N-d));

        return Sad/(varianceAvg());
    }

    private void calculateCoefficients() {

        int m = y.size();
        int n = m * N;

        double[][] a = new double[n][K + 1];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < m; j++) {
                a[i * N + j][0] = 1.0;
                for (int z = 1; z <= K; z++)
                    a[i * N + j][z] = x.get(i).get(z - 1);
            }
        }

        double[][] newY = new double[n][1];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < m; j++) {
                // y is inverse
                newY[i * N + j][0] = y.get(j).get(i);
            }
        }

        SimpleMatrix A = new SimpleMatrix(a);

        SimpleMatrix AT = A.transpose();
        SimpleMatrix ATA = AT.mult(A);
        SimpleMatrix ATA_1 = ATA.invert();
        SimpleMatrix ATA_1AT = ATA_1.mult(AT);

        SimpleMatrix Y = new SimpleMatrix(newY);

        res = ATA_1AT.mult(Y);
    }

    public SimpleMatrix getNaturalCoefficients() {
        SimpleMatrix s = new SimpleMatrix(3,1);
        for (int i = 0; i < naturalCoef.size(); i++) {
            s.set(i, 0, naturalCoef.get(i));
        }
        if (K ==2)
            return s;
        else
            return res;
    }

    private double varianceAvg() {

        double sumS = 0.0;
        double m = y.size();

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < y.size(); j++) {
                sumS += Math.pow(y.get(j).get(i) - yNAvg.get(i), 2.0);
            }
        }

        return sumS/(N*1.0);
    }

    private double getYi(int n) {
        double y = 0.0;
        for (int i = 0; i < student.length; i++) {
            if(i==0 && student[i] ){
                y = res.get(i,0);
            }else{
                if (student[i]) {
                    y += res.get(i,0)*x.get(n).get(i-1);
                }
            }
        }
        return y;
    }
    // for k == 2
    private void calculateNaturalCoef() {
        double[] dx = new double[2];
        double[] x0 = new double[2];
        for (int i = 0; i < 2; i++) {
            dx[i] = (1/2.0)*Math.abs(xMinMax.get(i).second - xMinMax.get(i).first);
            x0[i] = (1/2.0)*(xMinMax.get(i).second + xMinMax.get(i).first);
        }
        for (int i = 0; i <3; i++) {
            if(i == 0)
                naturalCoef.add(res.get(i,0) - res.get(1,0)*(x0[0]/dx[0]) - res.get(2,0)*(x0[1]/dx[1]));
            else
                naturalCoef.add(res.get(i,0)/dx[i-1]);

        }
    }

}