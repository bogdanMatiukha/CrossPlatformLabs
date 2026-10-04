package lab4v15;

import java.util.List;

public final class WildcardDemo {

    private WildcardDemo() {
    }

    // Unbounded wildcard: ?
    public static void printList(List<?> list) {
        System.out.println("Unbounded wildcard List<?>:");

        for (Object x : list) {
            System.out.println(" " + x);
        }
    }

    // Upper bounded wildcard: ? extends Long
    public static void sumNumbers(List<? extends Long> list) {
        System.out.println("Upper bounded wildcard List<? extends Long>:");

        long sum = 0;

        for (Long number : list) {
            sum += number;
        }

        System.out.println(" sum = " + sum);
    }

    // Lower bounded wildcard: ? super Long
    public static void addNumbers(List<? super Long> list) {
        System.out.println("Lower bounded wildcard List<? super Long>:");

        list.add(10L);
        list.add(20L);
        list.add(30L);

        for (Object x : list) {
            System.out.println(" " + x);
        }
    }
}