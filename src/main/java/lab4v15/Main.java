package lab4v15;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    // Variant 15 parameters
    private static final double BOX_VALUE = 15.5;
    private static final String PAIR_KEY = "Variant";
    private static final int PAIR_VALUE = 15;

    public static void main(String[] args) {
        System.out.println("===== LR4: Generics (Variant 15) =====\n");

        demoGenericClass_Box();
        demoGenericClass_Pair();
        demoGenericMethod_PrintArray();
        demoBoundedTypes();
        demoWildcards();
        demoCompileTimeSafety();
        demoTypeErasure();

        System.out.println("\n===== Program finished =====");
    }

    // 1) Generic class: Box<T>
    private static void demoGenericClass_Box() {
        System.out.println("1) Generic class: Box<T>");

        Box<Double> box = new Box<>();
        box.set(BOX_VALUE);

        System.out.println(" " + box);
        System.out.println(" box.get() = " + box.get());

        System.out.println();
    }

    // 2) Generic class: Pair<K,V>
    private static void demoGenericClass_Pair() {
        System.out.println("2) Generic class: Pair<K,V>");

        Pair<String, Integer> pair = new Pair<>(PAIR_KEY, PAIR_VALUE);

        System.out.println(" " + pair);
        System.out.println(" pair.getKey() = " + pair.getKey());
        System.out.println(" pair.getValue() = " + pair.getValue());

        System.out.println();
    }

    // 3) Generic method: printArray(T[])
    private static void demoGenericMethod_PrintArray() {
        System.out.println("3) Generic method: printArray(T[])");

        Double[] array = {10.5, 20.5, 30.5, 40.5};

        Utils.printArray(array);

        System.out.println();
    }

    // 4) Bounded types: NumericStats<T extends Number>
    private static void demoBoundedTypes() {
        System.out.println("4) Bounded types: NumericStats<T extends Number>");

        Long[] nums = {10L, 20L, 30L, 40L, 50L};

        NumericStats<Long> stats = new NumericStats<>(nums);

        System.out.println(" numbers = " + Arrays.toString(nums));
        System.out.println(" sum = " + stats.sum());
        System.out.println(" average = " + stats.average());
        System.out.println(" minimum = " + stats.min());
        System.out.println(" maximum = " + stats.max());

        System.out.println();
    }

    // 5) Wildcards: ?, ? extends, ? super
    private static void demoWildcards() {
        System.out.println("5) Wildcards: ?, ? extends, ? super");

        List<Long> numbers = Arrays.asList(10L, 20L, 30L);

        WildcardDemo.printList(numbers);

        System.out.println();

        WildcardDemo.sumNumbers(numbers);

        System.out.println();

        List<Number> target = new ArrayList<>();
        WildcardDemo.addNumbers(target);

        System.out.println();
    }

    // 6) Compile-time type safety
    private static void demoCompileTimeSafety() {
        System.out.println("6) Compile-time type safety");

        Box<Double> box = new Box<>();
        box.set(15.5);

        System.out.println(" Box<Double> accepts only Double.");

        // box.set("wrong");
        // compile-time error: incompatible types

        Pair<String, Integer> pair = new Pair<>("Number", 15);

        System.out.println(" Pair<String,Integer> = " + pair);

        // Pair<String, Integer> p2 = new Pair<>(15, "wrong");
        // compile-time error

        List<Long> list = new ArrayList<>();
        list.add(100L);

        System.out.println(" List<Long> accepts only Long.");

        // list.add("wrong");
        // compile-time error

        System.out.println();
    }

    // 7) Type erasure
    private static void demoTypeErasure() {
        System.out.println("7) Type erasure");

        ErasureDemo.run();
    }
}