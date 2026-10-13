package lab8;

import lab8.service.StudentService;

public class Main {
    public static void main(String[] args) {
        String inputFile = "students.csv";
        String outputFile = "result.txt";

        StudentService service = new StudentService();
        service.processFile(inputFile, outputFile);

        System.out.println("Processing is complete.");
    }
}
