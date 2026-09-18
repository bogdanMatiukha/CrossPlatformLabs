package lab1;

import java.util.Scanner;

public class Task3 {

    public static void run(Scanner scanner) {

        int day;

        while (true) {
            System.out.print("Enter day number (1-7): ");

            if (scanner.hasNextInt()) {
                day = scanner.nextInt();

                if (day >= 1 && day <= 7) {
                    break;
                }

                System.out.println("Error: Enter a number from 1 to 7.");
            } else {
                System.out.println("Error: Enter an integer.");
                scanner.next();
            }
        }

        switch (day) {
            case 1:
                System.out.println("Monday");
                break;

            case 2:
                System.out.println("Tuesday");
                break;

            case 3:
                System.out.println("Wednesday");
                break;

            case 4:
                System.out.println("Thursday");
                break;

            case 5:
                System.out.println("Friday");
                break;

            case 6:
                System.out.println("Saturday");
                break;

            case 7:
                System.out.println("Sunday");
                break;
        }
    }
}