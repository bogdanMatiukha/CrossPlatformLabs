package lab1;

import java.util.Scanner;

public class Task2 {

    public static void run(Scanner scanner) {

        double first;
        double second;

        while (true) {
            System.out.print("Enter first number: ");

            if (scanner.hasNextDouble()) {
                first = scanner.nextDouble();
                break;
            }

            System.out.println("Error: Enter a number.");
            scanner.next();
        }

        while (true) {
            System.out.print("Enter second number: ");

            if (scanner.hasNextDouble()) {
                second = scanner.nextDouble();
                break;
            }

            System.out.println("Error: Enter a number.");
            scanner.next();
        }

        double average = (first + second) / 2;

        System.out.println("Arithmetic mean = " + average);
    }
}