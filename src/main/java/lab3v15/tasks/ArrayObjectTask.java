package lab3v15.tasks;

import java.util.Arrays;
import lab3v15.model.StudentRecord;

public final class ArrayObjectTask {

    private ArrayObjectTask() {}

    // Variant 15 params
    private static final int COUNT = 6;
    private static final int GRADE_BASE = 84;

    public static void run() {

        System.out.println("\n--- Part D: Array of objects ---");

        StudentRecord[] students = new StudentRecord[COUNT];

        for (int i = 0; i < students.length; i++) {

            String name = "Student_15_" + i;
            int grade = GRADE_BASE + (i % 5);

            students[i] = new StudentRecord(name, grade);
        }

        System.out.println(Arrays.toString(students));

        StudentRecord best = bestStudent(students);

        System.out.println("Best student: " + best);
    }

    private static StudentRecord bestStudent(StudentRecord[] students) {

        StudentRecord best = students[0];

        for (int i = 1; i < students.length; i++) {

            if (students[i].getGrade() > best.getGrade()) {
                best = students[i];
            }
        }

        return best;
    }
}