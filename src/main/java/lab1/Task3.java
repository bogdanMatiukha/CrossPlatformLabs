package lab1;

public class Task3 {

    public static void run() {
        double x1 = 0;
        double y1 = 0;

        double x2 = 5;
        double y2 = 12;

        double distance = Math.sqrt(
                Math.pow(x2 - x1, 2) +
                        Math.pow(y2 - y1, 2)
        );

        System.out.println("Distance = " + distance);
    }
}