package lab4v15;

import java.util.ArrayList;
import java.util.List;

public final class ErasureDemo {

    private ErasureDemo() {
    }

    public static void run() {

        List<String> a = new ArrayList<>();
        List<Integer> b = new ArrayList<>();

        a.add("hello");
        b.add(123);

        System.out.println("Type erasure demo:");

        System.out.println(" a.getClass() = " + a.getClass().getName());
        System.out.println(" b.getClass() = " + b.getClass().getName());

        System.out.println(
                " a.getClass() == b.getClass() ? " +
                        (a.getClass() == b.getClass())
        );

        // if (a instanceof List<String>) {
        // }
        // compile-time error:
        // generic type cannot be safely checked at runtime

        // new T();
        // compile-time error:
        // cannot create an instance of a type parameter
    }
}