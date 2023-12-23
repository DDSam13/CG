package com.example.cg;

import java.util.ArrayList;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealVector;

public class CubicSpline {

    private ArrayList<Double> x;
    private ArrayList<Double> y;
    private Double[] d;
    private Double[] b;
    private Double[] c;

    public CubicSpline(ArrayList<Double> x, ArrayList<Double> y) {
        this.x = x;
        this.y = y;
    }

    public void solution() {
        if (x == null || x.size() < 2) {
            return;
        }

        ArrayList<Double> h = discreteDifference(x);

        double[][] a = calculateA();
        Double[] b_tmp = calculateB(a);

        Array2DRowRealMatrix matrix = new Array2DRowRealMatrix(a);
        RealVector constantsVector = new ArrayRealVector(b_tmp);

        DecompositionSolver solver = new LUDecomposition(matrix).getSolver();
        RealVector cCurr = solver.solve(constantsVector);

        c = realVectorToArray(cCurr);
        d = new Double[x.size() - 1];

        b = new Double[x.size() - 1];

        for (int i = 0; i < x.size() - 1; i++) {
            d[i] = (c[i + 1] - c[i]) / (3.0 * h.get(i));

            double hi = x.get(i + 1) - x.get(i - 1 + 1);
//            double hi_plus_1 = x.get(i + 1 + 1) - x.get(i + 1 + 1 - 1);

            b[i] = (y.get(i + 1) - y.get(i)) / hi - hi * (this.c[i + 1] + 2.0 * this.c[i]) / 3.0;
        }
    }

    private Double[] realVectorToArray(RealVector vector) {
        double[] data = vector.toArray();
        Double[] result = new Double[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = data[i];
        }
        return result;
    }

    public ArrayList<Double> getX() {

        return x;
    }

    public Double point(double param) {


        int i = searchIndex(param);
        double dx = param - x.get(i);
        double result = y.get(i) + b[i] * dx + c[i] * (dx * dx) + d[i] * (dx * dx * dx);
        return result;
    }

    public int searchIndex(Double xx) {
        int lo = 0;
        int hi = x.size() - 1;
        while (lo < hi) {
            int mid = (lo + hi) / 2;
            if (xx < x.get(mid)) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        return lo - 1;
    }

    private double[][] calculateA() {
        int n = x.size();
        double[][] result = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = 0.0;
            }
        }
        result[0][0] = 1.0;

        for (int i = 0; i < n - 1; i++) {
            if (i != (n - 2)) {
//                result[i + 1, i + 1] = 2.0 * (h[i] + h[i + 1])

                double hi = x.get(i + 1) - x.get(i - 1 + 1);
                double hi_plus_1 = x.get(i + 1 + 1) - x.get(i + 1 + 1 - 1);

                result[i + 1][i + 1] = 2.0 * (hi + hi_plus_1);
            }
            result[i + 1][i] = x.get(i + 1) - x.get(i);
            result[i][i + 1] = x.get(i + 1) - x.get(i);
        }

        result[0][1] = 0.0;
        result[n - 1][n - 2] = 0.0;
        result[n - 1][n - 1] = 1.0;
        return result;
    }
    private Double[] calculateB(double[][] a) {
        int n = x.size();
        Double[] result = new Double[n];
        for (int i = 0; i < n; i++) {
            result[i] = 0.0;
        }

//        tb = (self.a[i + 1] - self.a[i]) / h[i] - h[i] * (self.c[i + 1] + 2.0 * self.c[i]) / 3.0

        for (int i = 0; i < n - 2; i++) {
            double hi = x.get(i + 1) - x.get(i - 1 + 1);
            double hi_plus_1 = x.get(i + 1 + 1) - x.get(i + 1 + 1 - 1);

            result[i + 1] = 3.0 * ((y.get(i + 2) - y.get(i + 1)) / hi_plus_1 - (y.get(i + 1) - y.get(i)) / hi);

        }
        return result;
    }

    static ArrayList<Double> discreteDifference(ArrayList<Double> list) {
        ArrayList<Double> diff = new ArrayList<>();
        for (int i = 1; i < list.size(); i++) {
            diff.add(list.get(i) - list.get(i - 1));
        }
        return diff;
    }
}
