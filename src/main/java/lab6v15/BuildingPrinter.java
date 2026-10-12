package lab6v15;

public class BuildingPrinter implements Printer<Building> {
    @Override
    public void print(Building obj) {
        System.out.println(obj);
    }
}