package lab9;

public class Plant implements Observer {
    private final String name;

    public Plant(String name) {
        this.name = name;
    }

    @Override
    public void update(String condition) {
        System.out.println(name + " (рослина): " + switch (condition) {
            case "Дощ" -> "поглинає воду.";
            case "Посуха" -> "зменшує втрату вологи.";
            case "Похолодання" -> "уповільнює ріст.";
            default -> "адаптується до нових умов.";
        });
    }
}