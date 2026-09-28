package lab3v15.tasks;

public final class Array2DTask {

    private Array2DTask() {}

    // Variant 15 params
    private static final int ROWS = 4;
    private static final int COLS = 5;
    private static final int ROW1 = 0;
    private static final int ROW2 = 3;
    private static final int COL1 = 0;
    private static final int COL2 = 4;

    public static void run() {

        System.out.println("\n--- Part B: 2D array (matrix) ---");

        int[][] m = new int[ROWS][COLS];

        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                m[i][j] = (i + 1) * 10 + (j + 1);
            }
        }

        print(m, "Initial matrix:");

        swapRows(m, ROW1, ROW2);
        print(m, "After swap rows 1 <-> 4:");

        swapCols(m, COL1, COL2);
        print(m, "After swap cols 0 <-> 3:");
    }

    private static void print(int[][] m, String title) {

        System.out.println(title);

        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                System.out.print(m[i][j] + " ");
            }

            System.out.println();
        }
    }

    private static void swapRows(int[][] m, int r1, int r2) {

        int[] t = m[r1];
        m[r1] = m[r2];
        m[r2] = t;
    }

    private static void swapCols(int[][] m, int c1, int c2) {

        for (int i = 0; i < m.length; i++) {

            int t = m[i][c1];
            m[i][c1] = m[i][c2];
            m[i][c2] = t;
        }
    }
}