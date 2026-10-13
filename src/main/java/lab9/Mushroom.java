package lab9;

public class Mushroom implements Observer {
    private final String name;

    public Mushroom(String name) {
        this.name = name;
    }

    @Override
    public void update(String condition) {
        System.out.println(name + " (гриб): " + switch (condition) {
            case "Дощ" -> "активно поглинає вологу.";
            case "Посуха" -> "сповільнює розвиток.";
            case "Похолодання" -> "знижує активність.";
            default -> "пристосовується до середовища.";
        });
    }
}