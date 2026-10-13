package lab9;

public class Animal implements Observer {
    private final String name;

    public Animal(String name) {
        this.name = name;
    }

    @Override
    public void update(String condition) {
        System.out.println(name + " (тварина): " + switch (condition) {
            case "Дощ" -> "шукає укриття.";
            case "Посуха" -> "шукає воду.";
            case "Похолодання" -> "шукає тепле місце.";
            default -> "реагує на зміну середовища.";
        });
    }
}