/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.electronmontecarlosimulation3d;

/**
 *
 * @author sgershaft
 */
public class Cholesky {
    // matrix is symmetric
    public static boolean isSymmetric(double[][] A) {
        int N = A.length;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < i; j++) {
                if (A[i][j] != A[j][i]) {
                    return false;
                }
            }
        }
        return true;
    }

    // matrix is square
    public static boolean isSquare(double[][] A) {
        int N = A.length;
        for (int i = 0; i < N; i++) {
            if (A[i].length != N) {
                return false;
            }
        }
        return true;
    }

    // return Cholesky factor L of psd matrix A = L L^T
    public static double[][] cholesky(double[][] A) {
        if (!isSquare(A)) {
            throw new RuntimeException("Matrix is not square");
        }
        if (!isSymmetric(A)) {
            throw new RuntimeException("Matrix is not symmetric");
        }

        int N = A.length;
        double[][] L = new double[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j <= i; j++) {
                double sum = 0.0;
                for (int k = 0; k < j; k++) {
                    sum += L[i][k] * L[j][k];
                }
                if (i == j) {
                    L[i][i] = Math.sqrt(A[i][i] - sum);
                } else {
                    L[i][j] = 1.0 / L[j][j] * (A[i][j] - sum);
                }
            }
            if (L[i][i] <= 0) {
                throw new RuntimeException("Matrix not positive definite");
            }
        }
        return L;
    }
    
    /*
    int N = 3;
        double[][] A = { { 4, 1,  1 },
                         { 1, 5,  3 },
                         { 1, 3, 15 }
                       };
        double[][] L = cholesky(A);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                StdOut.printf("%8.5f ", L[i][j]);
            }
            StdOut.println();
    */
    
    // Dr. Whitmer's cholesky solver method, copied here
    public static double[] choleskySolver(double[][] mat, double[] p, double[] b) {
        int n = mat.length;
        for (int i = 0; i < n; i++) {
          double sum = b[i];
          for (int k = i - 1; k >= 0; k--)
            sum -= mat[i][k]* b[k];
          b[i] = sum / p[i];
        }

        for (int i = n - 1; i >= 0; i--) {
          double sum = b[i];
          for (int k = i + 1; k < n; k++)
            sum -= mat[k][i]* b[k];
          b[i] = sum / p[i];
        }
        return b;
    }
    
    public static double[] extractDiagonal(double[][] mat) {
        int n = mat.length;
        double[] mainDiagonal = new double[n];

        // Find main diagonal
        for (int i = 0; i < n; i++) {
            mainDiagonal[i] = mat[i][i];
        }
        
        return mainDiagonal;
    }
    
    public static double dotProduct(double[] array1, double[] array2) {
        double result = 0.0;
        for (int i = 0; i < array1.length; i++) {
            result += array1[i] * array2[i];
        }
        return result;
    }
}
