package lab1;

import java.util.Scanner;

public class Task5 {

    public static void run() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter N: ");
        int N = scanner.nextInt();

        if (N % 10 == 0) {
            System.out.println("Number " + N + " is divisible by 10");
        } else {
            System.out.println("Number " + N + " is not divisible by 10");
        }
    }
}