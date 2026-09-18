package lab1;

import java.util.Scanner;

public class Task1 {

    public static void run(Scanner scanner) {

        double x;

        while (true) {
            System.out.print("Enter x: ");

            if (scanner.hasNextDouble()) {
                x = scanner.nextDouble();

                if (x != 0) {
                    break;
                }

                System.out.println("Error: x must not be 0.");
            } else {
                System.out.println("Error: Enter a number.");
                scanner.next();
            }
        }

        double result = x * x * x;

        System.out.println("x^3 = " + result);
    }
}