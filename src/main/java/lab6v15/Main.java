
package lab6v15;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) {
        List<Building> buildings = new ArrayList<>();

        buildings.add(new Building(1, "School", 3));
        buildings.add(new Building(2, "Office", 12));
        buildings.add(new Building(3, "Hospital", 7));
        buildings.add(new Building(4, "House", 4));
        buildings.add(new Building(5, "Skyscraper", 25));

        System.out.println("===== LR6: Lambda (Variant 15) =====");

        System.out.println("\nInitial list:");
        buildings.forEach(System.out::println);

        System.out.println("\n1) Named implementation:");
        Printer<Building> namedPrinter = new BuildingPrinter();
        namedPrinter.print(buildings.get(0));

        System.out.println("\n2) Anonymous class:");
        Printer<Building> anonymousPrinter = new Printer<Building>() {
            @Override
            public void print(Building obj) {
                System.out.println(obj);
            }
        };
        anonymousPrinter.print(buildings.get(1));

        System.out.println("\n3) Lambda:");
        Printer<Building> lambdaPrinter = obj -> System.out.println(obj);
        lambdaPrinter.print(buildings.get(2));

        System.out.println("\n4) Standard functional interfaces:");

        Predicate<Building> hasAtLeastFiveFloors =
                b -> b.getFloors() >= 5;

        Function<Building, Integer> getFloors =
                b -> b.getFloors();

        Consumer<Building> consumer =
                b -> System.out.println(b);

        Supplier<Building> defaultBuilding =
                () -> new Building(0, "Unknown", 0);

        System.out.println("Predicate for first building: "
                + hasAtLeastFiveFloors.test(buildings.get(0)));

        System.out.println("Function for second building: "
                + getFloors.apply(buildings.get(1)));

        consumer.accept(buildings.get(0));

        System.out.println("Supplier: " + defaultBuilding.get());

        System.out.println("\n5) forEach:");
        buildings.forEach(b -> System.out.println(b));

        System.out.println("\n6) Sort by floors:");
        buildings.sort((a, b) ->
                Integer.compare(a.getFloors(), b.getFloors()));

        buildings.forEach(System.out::println);

        System.out.println("\n7) removeIf floors < 5:");
        buildings.removeIf(b -> b.getFloors() < 5);

        buildings.forEach(System.out::println);

        System.out.println("\n8) Method references:");
        buildings.forEach(System.out::println);

        Supplier<ArrayList<Building>> factory = ArrayList::new;
        ArrayList<Building> emptyList = factory.get();

        System.out.println("Factory list size = " + emptyList.size());

        System.out.println("\n===== Program finished =====");
    }
}
