package lab1;

import java.util.Scanner;

public class Task3 {

    public static void run(Scanner scanner) {

        double x1;
        double y1;
        double x2;
        double y2;

        while (true) {
            System.out.print("Enter x1: ");

            if (scanner.hasNextDouble()) {
                x1 = scanner.nextDouble();
                break;
            }

            System.out.println("Error: enter a number.");
            scanner.next();
        }

        while (true) {
            System.out.print("Enter y1: ");

            if (scanner.hasNextDouble()) {
                y1 = scanner.nextDouble();
                break;
            }

            System.out.println("Error: Enter a number.");
            scanner.next();
        }

        while (true) {
            System.out.print("Enter x2: ");

            if (scanner.hasNextDouble()) {
                x2 = scanner.nextDouble();
                break;
            }

            System.out.println("Error: Enter a number.");
            scanner.next();
        }

        while (true) {
            System.out.print("Enter y2: ");

            if (scanner.hasNextDouble()) {
                y2 = scanner.nextDouble();
                break;
            }

            System.out.println("Error: enter a number.");
            scanner.next();
        }

        double distance = Math.sqrt(
                Math.pow(x2 - x1, 2) +
                        Math.pow(y2 - y1, 2)
        );

        System.out.println("Distance = " + distance);
    }
}