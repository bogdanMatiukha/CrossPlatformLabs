package lab6v15;

public class Building {
    private final int id;
    private final String name;
    private final int floors;

    public Building(int id, String name, int floors) {
        this.id = id;
        this.name = name;
        this.floors = floors;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getFloors() {
        return floors;
    }

    @Override
    public String toString() {
        return "Building{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", floors=" + floors +
                '}';
    }
}

