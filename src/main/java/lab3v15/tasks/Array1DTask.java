package lab3v15.tasks;

import java.util.Arrays;

public final class Array1DTask {

    private Array1DTask() {}

    // Variant 15 params
    private static final int N = 12;
    private static final int A = 4;
    private static final int B = 10;
    private static final int I1 = 2;
    private static final int I2 = 9;
    private static final int COMPRESS_MODE = 3;

    public static void run() {

        System.out.println("\n--- Part A: 1D array ---");

        int[] x = new int[N];

        for (int i = 0; i < x.length; i++) {
            x[i] = A * i - B;
        }

        print(x, "Initial x:");

        System.out.println("Min = " + min(x));
        System.out.println("Max = " + max(x));
        System.out.println("Sum = " + sum(x));

        int[] sorted = x.clone();
        Arrays.sort(sorted);

        print(sorted, "Sorted:");

        int[] swapped = sorted.clone();
        swap(swapped, I1, I2);

        print(swapped, "After swap [0] <-> [9]:");

        int[] compressed = compress(swapped);
        print(compressed, "Compressed (remove even elements):");

        copyDemo(compressed);
    }

    private static void print(int[] x, String title) {
        System.out.println(title);
        System.out.println(Arrays.toString(x));
    }

    private static int min(int[] x) {
        int m = x[0];

        for (int i = 1; i < x.length; i++) {
            if (x[i] < m) {
                m = x[i];
            }
        }

        return m;
    }

    private static int max(int[] x) {
        int m = x[0];

        for (int i = 1; i < x.length; i++) {
            if (x[i] > m) {
                m = x[i];
            }
        }

        return m;
    }

    private static long sum(int[] x) {
        long s = 0;

        for (int v : x) {
            s += v;
        }

        return s;
    }

    private static void swap(int[] x, int i, int j) {
        int t = x[i];
        x[i] = x[j];
        x[j] = t;
    }

    private static int[] compress(int[] x) {

        int[] tmp = new int[x.length];
        int k = 0;

        for (int v : x) {
            if (COMPRESS_MODE == 1 && v >= 0) {
                tmp[k++] = v;
            } else if (COMPRESS_MODE == 2 && v != 0) {
                tmp[k++] = v;
            } else if (COMPRESS_MODE == 3 && v % 2 != 0) {
                tmp[k++] = v;
            }
        }

        return Arrays.copyOf(tmp, k);
    }

    private static void copyDemo(int[] x) {

        System.out.println("\nCOPY DEMO:");

        int[] ref = x;
        int[] clone = x.clone();
        int[] copyOf = Arrays.copyOf(x, x.length);

        int[] sysCopy = new int[x.length];
        System.arraycopy(x, 0, sysCopy, 0, x.length);

        if (ref.length > 0) {
            ref[0] += 999;
        }

        System.out.println("x after ref[0]+=999: " + Arrays.toString(x));
        System.out.println("clone (independent): " + Arrays.toString(clone));
        System.out.println("copyOf(independent): " + Arrays.toString(copyOf));
        System.out.println("sysCopy(independent): " + Arrays.toString(sysCopy));

        System.out.println("Arrays.equals(x, clone) = "
                + Arrays.equals(x, clone));

        System.out.println("Arrays.equals(copyOf, sysCopy) = "
                + Arrays.equals(copyOf, sysCopy));
    }
}