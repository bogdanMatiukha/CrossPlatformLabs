package lab1;

import java.util.Scanner;

public class Task1 {

    public static void run(Scanner scanner) {

        double x;

        while (true) {
            System.out.print("Enter x: ");

            if (scanner.hasNextDouble()) {
                x = scanner.nextDouble();

                if (2 * x + 1 >= 0) {
                    break;
                }

                System.out.println("Error: The number must be greater than 0.");
            } else {
                System.out.println("Error: Enter a number.");
                scanner.next();
            }
        }

        double y = Math.sqrt(2 * x + 1);

        System.out.println("y = " + y);
    }
}