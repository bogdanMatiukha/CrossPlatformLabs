package lab1;

import java.util.Scanner;

public class Task4 {

    public static void run(Scanner scanner) {

        double number;

        while (true) {
            System.out.print("Enter number: ");

            if (scanner.hasNextDouble()) {
                number = scanner.nextDouble();
                break;
            }

            System.out.println("Error: Enter a number.");
            scanner.next();
        }

        if (number > 0) {
            System.out.println("positive");
        } else if (number < 0) {
            System.out.println("negative");
        } else {
            System.out.println("zero");
        }
    }
}