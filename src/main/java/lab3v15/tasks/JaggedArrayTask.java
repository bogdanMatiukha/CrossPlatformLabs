package lab3v15.tasks;

public final class JaggedArrayTask {

    private JaggedArrayTask() {}

    // Variant 15 params
    private static final int ROWS = 4;
    private static final int BASE_LEN = 2;

    public static void run() {

        System.out.println("\n--- Part C: Jagged array ---");

        int[][] j = new int[ROWS][];

        for (int i = 0; i < ROWS; i++) {

            int len = BASE_LEN + (i % 3);

            j[i] = new int[len];

            for (int k = 0; k < len; k++) {
                j[i][k] = (i + 1) * 100 + (k + 1);
            }
        }

        print(j);
    }

    private static void print(int[][] j) {

        for (int i = 0; i < j.length; i++) {

            System.out.print("Row " + i
                    + " (len=" + j[i].length + "): ");

            for (int v : j[i]) {
                System.out.print(v + " ");
            }

            System.out.println();
        }
    }
}