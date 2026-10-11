package lab5v15;


import java.util.Objects;

public class BadStudent {

    private final int id;
    private String name;
    private int grade;

    public BadStudent(int id, String name, int grade) {
        this.id = id;
        this.name = name;
        this.grade = grade;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof BadStudent)) {
            return false;
        }

        BadStudent student = (BadStudent) o;
        return id == student.id;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}