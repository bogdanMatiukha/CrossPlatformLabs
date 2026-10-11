package lab5v15;

import java.util.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("COLLECTIONS LAB 5");
        System.out.println("Variant 15");
        System.out.println();

        List<Student> students = new ArrayList<>(
                StudentGenerator.generate(75)
        );

        System.out.println("Number of students: " + students.size());
        System.out.println();

        System.out.println("Initial ArrayList:");
        for (Student student : students) {
            System.out.println(student);
        }

        System.out.println();
        System.out.println("Sorting by grade using Comparable:");

        Collections.sort(students);

        for (Student student : students) {
            System.out.println(student);
        }

        System.out.println();
        System.out.println("Sorting by id using Comparator:");

        students.sort(new StudentIdComparator());

        for (Student student : students) {
            System.out.println(student);
        }

        System.out.println();
        System.out.println("HashSet test:");

        Set<Student> studentSet = new HashSet<>(students);

        Student duplicate = new Student(
                students.get(0).getId(),
                "Duplicate",
                100
        );

        System.out.println("Set size before duplicate: " + studentSet.size());

        studentSet.add(duplicate);

        System.out.println("Set size after duplicate: " + studentSet.size());
        System.out.println("Duplicate added: " + studentSet.contains(duplicate));

        System.out.println();
        System.out.println("HashMap test:");

        Map<Integer, Student> studentMap = new HashMap<>();

        for (Student student : students) {
            studentMap.put(student.getId(), student);
        }

        System.out.println("HashMap size: " + studentMap.size());

        int searchId = 15;

        System.out.println(
                "Student with id " + searchId + ": "
                        + studentMap.get(searchId)
        );

        System.out.println();
        System.out.println("HashMap performance:");

        int[] sizes = {50, 1000, 10000};

        for (int size : sizes) {
            double time = measureHashMapGet(size);

            System.out.printf(
                    "%d elements: %.2f ns%n",
                    size,
                    time
            );
        }

        System.out.println();
        System.out.println("Collision experiment:");

        for (int size : sizes) {
            double normalTime = measureNormalHashMap(size);
            double badTime = measureBadHashMap(size);

            System.out.printf(
                    "%d elements -> normal hashCode: %.2f ns, bad hashCode: %.2f ns%n",
                    size,
                    normalTime,
                    badTime
            );
        }
    }

    private static double measureHashMapGet(int size) {

        Map<Integer, Student> map = new HashMap<>();

        for (Student student : StudentGenerator.generate(size)) {
            map.put(student.getId(), student);
        }

        int searches = 100000;
        int id = size / 2;

        for (int i = 0; i < 10000; i++) {
            map.get(id);
        }

        long start = System.nanoTime();

        for (int i = 0; i < searches; i++) {
            map.get(id);
        }

        long end = System.nanoTime();

        return (double) (end - start) / searches;
    }

    private static double measureNormalHashMap(int size) {

        Map<Student, Student> map = new HashMap<>();

        List<Student> students = StudentGenerator.generate(size);

        for (Student student : students) {
            map.put(student, student);
        }

        Student key = students.get(size / 2);

        int searches = 100000;

        for (int i = 0; i < 10000; i++) {
            map.get(key);
        }

        long start = System.nanoTime();

        for (int i = 0; i < searches; i++) {
            map.get(key);
        }

        long end = System.nanoTime();

        return (double) (end - start) / searches;
    }

    private static double measureBadHashMap(int size) {

        Map<BadStudent, BadStudent> map = new HashMap<>();

        List<BadStudent> students = new ArrayList<>();

        for (int i = 1; i <= size; i++) {
            BadStudent student = new BadStudent(
                    i,
                    "Student_" + i,
                    60 + i % 41
            );

            students.add(student);
            map.put(student, student);
        }

        BadStudent key = students.get(size / 2);

        int searches = 100000;

        for (int i = 0; i < 10000; i++) {
            map.get(key);
        }

        long start = System.nanoTime();

        for (int i = 0; i < searches; i++) {
            map.get(key);
        }

        long end = System.nanoTime();

        return (double) (end - start) / searches;
    }

}