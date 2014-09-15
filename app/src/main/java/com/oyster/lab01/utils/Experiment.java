package com.oyster.lab01.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by andriybas on 9/15/14.
 */
public class Experiment {

    public static final double UPPER_BOUND = 20.0;
    private static final Random RAND = new Random();

    double[][] table = new double[10][7];
    double[] minElems = new double[3];
    double[] maxElems = new double[3];

    double yAverage = 0;
    double yFun;
    int yFunRow;

    public Experiment(double a0, double a1, double a2, double a3) {

        for (int i = 0; i < table.length; i++)
            Arrays.fill(table[i], -1);

        Arrays.fill(minElems, Double.MAX_VALUE);
        Arrays.fill(maxElems, Double.MIN_VALUE);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 3; i++) {
                double t = RAND.nextDouble() * UPPER_BOUND;

                if (t > maxElems[i])
                    maxElems[i] = t;

                if (t < minElems[i])
                    minElems[i] = t;

                table[i][j] = t;
            }

            table[i][3] = (a0 + a1 * table[i][0] + a2 * table[i][1] + a3 * table[i][2]);
            yAverage += table[i][3];
        }
        yAverage /= 8.0;

        for (int i = 0; i < 3; i++) {
            table[8][i] = minElems[i] + (maxElems[i] - minElems[i]) * 0.5;
            table[9][i] = table[8][i] - minElems[i];
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 3; j++) {
                table[i][4 + j] = (table[i][j] - table[8][i]) / table[9][i];
            }
        }

        yFun = Double.MIN_VALUE;
        for (int i = 0; i < 8; i++) {
            if (table[i][3] < yAverage && table[i][3] > yFun) {
                yFun = table[i][3];
                yFunRow = i;
            }
        }
    }
}
