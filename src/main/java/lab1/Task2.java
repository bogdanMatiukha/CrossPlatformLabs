package lab1;

import java.util.Scanner;

public class Task2 {

    public static void run(Scanner scanner) {

        double a;

        while (true) {
            System.out.print("Enter side a: ");

            if (scanner.hasNextDouble()) {
                a = scanner.nextDouble();

                if (a > 0) {
                    break;
                }

                System.out.println("Error: Side must be greater than 0.");
            } else {
                System.out.println("Error: Enter a number.");
                scanner.next();
            }
        }

        double S = (a * a * Math.sqrt(3)) / 4;

        System.out.println("Area = " + S);
    }
}