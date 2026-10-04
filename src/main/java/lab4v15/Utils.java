package lab4v15;

public final class Utils {

    private Utils() {
    }

    public static <T> void printArray(T[] array) {
        for (T element : array) {
            System.out.println(" " + element);
        }
    }
}