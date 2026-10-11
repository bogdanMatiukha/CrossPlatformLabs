package lab5v15;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StudentGenerator {

    private static final String[] NAMES = {
            "Artem", "Bohdan", "Dana", "Mykhaylo", "Andrei",
            "Igor", "Vlad", "Ivan", "Alexander", "Maxim"
    };

    private static final Random random = new Random();

    public static List<Student> generate(int count) {
        List<Student> students = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            String name = NAMES[random.nextInt(NAMES.length)];
            int grade = 60 + random.nextInt(41);

            students.add(new Student(i, name, grade));
        }

        return students;
    }
}
