package lab1;

import java.util.Scanner;

public class Task4 {

    public static void run(Scanner scanner) {

        int a;

        while (true) {
            System.out.print("Enter a: ");

            if (scanner.hasNextInt()) {
                a = scanner.nextInt();
                break;
            }

            System.out.println("Error: Enter an integer.");
            scanner.next();
        }

        int result = a * a * a * a * a * a * a;

        System.out.println("a^7 = " + result);
    }
}