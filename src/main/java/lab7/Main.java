package lab7;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Restaurant> restaurants = new ArrayList<>();

        restaurants.add(new Restaurant(1, "Bella Italia", 5));
        restaurants.add(new Restaurant(2, "Burger House", 4));
        restaurants.add(new Restaurant(3, "Fast Food", 2));
        restaurants.add(new Restaurant(4, "Sushi Master", 3));
        restaurants.add(new Restaurant(5, "Golden Fork", 5));
        restaurants.add(new Restaurant(6, "Street Food", 1));
        restaurants.add(new Restaurant(7, "Green Garden", 4));

        System.out.println("=== Initial list of restaurants ===");
        restaurants.forEach(System.out::println);

        System.out.println("\n=== Filter: rating >= 4 ===");
        restaurants.stream()
                .filter(r -> r.getRating() >= 4)
                .forEach(System.out::println);

        System.out.println("\n=== Map: restaurant names ===");
        List<String> names = restaurants.stream()
                .map(Restaurant::getName)
                .toList();

        names.forEach(System.out::println);

        System.out.println("\n=== Sort by rating descending ===");
        restaurants.stream()
                .sorted((a, b) ->
                        Integer.compare(b.getRating(), a.getRating()))
                .forEach(System.out::println);

        System.out.println("\n=== Average rating (Stream API) ===");
        double averageRating = restaurants.stream()
                .mapToInt(Restaurant::getRating)
                .average()
                .orElse(0.0);

        System.out.println("Average rating = " + averageRating);

        System.out.println("\n=== Average rating (for loop) ===");
        int totalRating = 0;

        for (Restaurant restaurant : restaurants) {
            totalRating += restaurant.getRating();
        }

        double averageRatingFor =
                (double) totalRating / restaurants.size();

        System.out.println("Average rating = " + averageRatingFor);

        System.out.println("\n=== Logical check: any rating < 2 ===");
        boolean hasLowRating = restaurants.stream()
                .anyMatch(r -> r.getRating() < 2);

        System.out.println("Result: " + hasLowRating);

        System.out.println("\n=== Program finished ===");
    }
}
