package lab11.model;

public class Project {
    private int id;
    private String name;
    private String manager;
    private int duration;

    public Project() {}

    public Project(int id, String name, String manager, int duration) {
        this.id = id;
        this.name = name;
        this.manager = manager;
        this.duration = duration;
    }

    public Project(String name, String manager, int duration) {
        this(0, name, manager, duration);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getManager() { return manager; }
    public void setManager(String manager) { this.manager = manager; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
}