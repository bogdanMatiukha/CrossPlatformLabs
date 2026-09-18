package lab1;

import java.util.Scanner;

public class Task5 {

    public static void run(Scanner scanner) {

        int a;
        int b;

        while (true) {
            System.out.print("Enter a: ");

            if (scanner.hasNextInt()) {
                a = scanner.nextInt();
                break;
            }

            System.out.println("Error: Enter an integer.");
            scanner.next();
        }

        while (true) {
            System.out.print("Enter b: ");

            if (scanner.hasNextInt()) {
                b = scanner.nextInt();
                break;
            }

            System.out.println("Error: Enter an integer.");
            scanner.next();
        }

        if (a > b) {
            int temp = a;
            a = b;
            b = temp;
        }

        int sum = 0;

        for (int i = a; i <= b; i++) {
            sum += i;
        }

        System.out.println("Sum = " + sum);
    }
}