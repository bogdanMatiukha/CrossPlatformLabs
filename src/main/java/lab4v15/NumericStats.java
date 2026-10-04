package lab4v15;

public class NumericStats<T extends Number> {

    private final T[] numbers;

    public NumericStats(T[] numbers) {
        this.numbers = numbers;
    }

    public double sum() {
        double result = 0;

        for (T number : numbers) {
            result += number.doubleValue();
        }

        return result;
    }

    public double average() {
        if (numbers.length == 0) {
            return 0;
        }

        return sum() / numbers.length;
    }

    public T min() {
        if (numbers.length == 0) {
            return null;
        }

        T result = numbers[0];

        for (T number : numbers) {
            if (number.doubleValue() < result.doubleValue()) {
                result = number;
            }
        }

        return result;
    }

    public T max() {
        if (numbers.length == 0) {
            return null;
        }

        T result = numbers[0];

        for (T number : numbers) {
            if (number.doubleValue() > result.doubleValue()) {
                result = number;
            }
        }

        return result;
    }
}