package lab1;

import java.util.Scanner;

public class MainMenu {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n------MENU-----");
            System.out.println("1 - Task 1");
            System.out.println("2 - Task 2");
            System.out.println("3 - Task 3");
            System.out.println("4 - Task 4");
            System.out.println("5 - Task 5");
            System.out.println("0 - Exit");
            System.out.print("Choose program: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Error: enter a number from 0 to 5.");
                scanner.nextLine();
                continue;
            }

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    Task1.run(scanner);
                    break;

                case 2:
                    Task2.run(scanner);
                    break;

                case 3:
                    Task3.run(scanner);
                    break;

                case 4:
                    Task4.run(scanner);
                    break;

                case 5:
                    Task5.run(scanner);
                    break;

                case 0:
                    System.out.println("Program finished.");
                    scanner.close();
                    return;

                default:
                    System.out.println("Error: Choose a number from 0 to 5.");
            }
        }
    }
}