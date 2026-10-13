package lab9;

public class Main {
    public static void main(String[] args) {
        Environment environment = new Environment();

        environment.addOrganism(new Animal("Вовк"));
        environment.addOrganism(new Plant("Дуб"));

        System.out.println("=== БАЗОВИЙ ЗАПУСК ===");
        environment.changeCondition("Дощ");

        System.out.println("\n=== ЗМІНА ПОВЕДІНКИ ===");
        environment.changeCondition("Посуха");

        System.out.println("\n=== ДОДАВАННЯ НОВОГО ОРГАНІЗМУ ===");
        environment.addOrganism(new Mushroom("Білий гриб"));
        environment.changeCondition("Похолодання");
    }
}
