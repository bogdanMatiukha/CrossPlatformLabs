package lab1;

import java.util.Scanner;

public class Task5 {

    public static void run(Scanner scanner) {

        int N;

        while (true) {
            System.out.print("Enter N: ");

            if (scanner.hasNextInt()) {
                N = scanner.nextInt();
                break;
            }

            System.out.println("Error: Enter an integer.");
            scanner.next();
        }

        if (N % 10 == 0) {
            System.out.println("Number " + N + " is divisible by 10");
        } else {
            System.out.println("Number " + N + " is not divisible by 10");
        }
    }
}