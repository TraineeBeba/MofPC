package org.dbms;

import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("synchronization")
public class Cells0 {
    private static int N; // Number of cells in the crystal
    private static int K; // Number of impurity atoms
    private static double p; // Probability of atom movement

    private static AtomicInteger[] cells; // Array to store the number of atoms in each cell
    private static CyclicBarrier threadsBarrier;

    private static int step = 0;

    static long startTime = 0, endTime = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of cells (N): ");
        N = scanner.nextInt();

        System.out.print("Enter the number of impurity atoms (K): ");
        K = scanner.nextInt();

        System.out.print("Enter the probability of atom movement (p): ");
        p = scanner.nextDouble();

        scanner.close();
        int[] data = new int[N];

        cells =  new AtomicInteger[N];

        for (int i = 0; i < cells.length; i++) {
            cells[i] = new AtomicInteger();
            cells[i].set(0);
        }

        cells[0].set(K);
        threadsBarrier = new CyclicBarrier(K, () -> snapshot());

        snapshot();

        for (int i = 0; i < K; i++) {
            int finalI = i;
            new Thread(() -> {
                int currentPosition = 0;
                for (int j = 0; j < 10; j++) {
                    long endTime;
                    long startTime = System.currentTimeMillis();
                    do{
                    double m = Math.random();
                        if (m > p) {
                            if (currentPosition < N - 1) {
                                synchronized (cells) {
                                    cells[currentPosition].getAndDecrement();
                                }
                                    currentPosition++;
                                synchronized (cells) {
                                    cells[currentPosition].getAndIncrement();
                                }
                            }
                        } else {
                            if (currentPosition > 0) {
                                synchronized (cells) {
                                    cells[currentPosition].getAndDecrement();
                                }
                                currentPosition--;
                                synchronized (cells) {
                                    cells[currentPosition].getAndIncrement();
                                }
                            }
                        }

//                    if (m > p) {
//                        if (currentPosition < N - 1) {
//                            synchronized (cells[currentPosition]) {
//                                cells[currentPosition].getAndDecrement();
//                            }
//                            currentPosition++;
//                            synchronized (cells[currentPosition]) {
//                                cells[currentPosition].getAndIncrement();
//                            }
//                        }
//                    } else {
//                        if (currentPosition > 0) {
//                            synchronized (cells[currentPosition]) {
//                                cells[currentPosition].getAndDecrement();
//                            }
//                            currentPosition--;
//                            synchronized (cells[currentPosition]) {
//                                cells[currentPosition].getAndIncrement();
//                            }
//                        }
//                    }
                        endTime = System.currentTimeMillis();
                    } while (endTime - startTime < 1000);
                    try {
                        threadsBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

    private static void snapshot() {
        endTime = System.currentTimeMillis();
//        System.out.println("\nTime of processes: " + (endTime - startTime) + " milliseconds");

        AtomicInteger[] snapshot = new AtomicInteger[N];
        for (int i = 0; i < N; i++) {
            snapshot[i] = new AtomicInteger();
            synchronized (cells[i]){
                snapshot[i].set(cells[i].get());
            }
        }

        AtomicInteger m = new AtomicInteger(0);
        for (int i = 0; i < snapshot.length; i++) {
            m.getAndAdd(snapshot[i].get());
        }

        System.out.print("\nStep " + step + ": " + java.util.Arrays.toString(snapshot) + " m = " + m);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        step++;
        startTime = System.currentTimeMillis();
    }
}
