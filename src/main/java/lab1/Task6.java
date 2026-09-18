package lab1;

import java.util.Scanner;

public class Task6 {

    public static void run(Scanner scanner) {

        do {
            System.out.print("Enter a number: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Error: Enter an integer.");
                scanner.next();
                continue;
            }

            int number = scanner.nextInt();

            if (number % 10 != 0) {
                System.out.println("The number is not divisible by 10.");
                continue;
            }

            System.out.println("The number " + number + " is divisible by 10.");
            break;

        } while (true);
    }
}