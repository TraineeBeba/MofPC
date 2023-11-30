package org.dbms;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.Scanner;

public class Cells1 {
    private static int N; // Number of cells in the crystal
    private static int K; // Number of impurity atoms
    private static double p; // Probability of atom movement

    private static int[] cells; // Array to store the number of atoms in each cell
    private static CyclicBarrier threadsBarrier;

    private static int step = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of cells (N): ");
        N = scanner.nextInt();

        System.out.print("Enter the number of impurity atoms (K): ");
        K = scanner.nextInt();

        System.out.print("Enter the probability of atom movement (p): ");
        p = scanner.nextDouble();

        scanner.close();

        cells = new int[N];
        cells[0] = K; // Initial concentration of impurity atoms in the first cell
        threadsBarrier = new CyclicBarrier(K, () -> snapshot());

        snapshot();

        for (int i = 0; i < K; i++) {
            int finalI = i;
            new Thread(() -> {
                int currentPosition = 0;
                for (int j = 0; j < 10; j++) {
                    double m = Math.random();
                    if (m > p) {
                        if (currentPosition < N - 1) {
                            currentPosition++;
                            cells[currentPosition]++;
                            cells[currentPosition - 1]--;
                        }
                    } else {
                        if (currentPosition > 0) {
                            currentPosition--;
                            cells[currentPosition]++;
                            cells[currentPosition + 1]--;
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
//                    System.out.print(" " + finalI);
                }
            }).start();
        }

        for (int j = 0; j < 10; j++) {
            snapshot();
        }
    }

    // The snapshot method remains the same.

    private static void snapshot() {
        int[] snapshot = new int[N];
        synchronized (cells) {
            System.arraycopy(cells, 0, snapshot, 0, N);
        }

        int m = 0;
        for (int j : snapshot) {
            m += j;
        }

        System.out.println();
        System.out.println();
        System.out.print("\nStep " + step + ": " + java.util.Arrays.toString(snapshot) + " m = " + m);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        step++;
    }
}
