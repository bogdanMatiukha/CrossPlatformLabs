package lab5v15;

import java.util.Comparator;

public class StudentIdComparator implements Comparator<Student> {

    @Override
    public int compare(Student first, Student second) {
        return Integer.compare(first.getId(), second.getId());
    }
}