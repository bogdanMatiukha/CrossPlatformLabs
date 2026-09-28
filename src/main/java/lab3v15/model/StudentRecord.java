package lab3v15.model;

public class StudentRecord {
    private final String name;
    private final int grade;
    public StudentRecord(String name, int grade) {
        this.name = name;
        this.grade = grade;
    }
    public String getName() { return name; }
    public int getGrade() { return grade; }
    @Override
    public String toString() {
        return "StudentRecord{name='" + name + "', grade=" + grade + "}";
    }
}
