package lab8.service;

import lab8.exception.InvalidScoreException;
import lab8.model.Student;
import lab8.util.ErrorHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StudentService {

    public void processFile(String inputFile, String outputFile) {
        List<Student> students = new ArrayList<>();
        double totalScore = 0;
        int scoreCount = 0;
        int validCount = 0;
        int invalidCount = 0;

        try (BufferedReader reader =
                     Files.newBufferedReader(Path.of(inputFile))) {

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;

                    if (line.trim().equalsIgnoreCase(
                            "StudentID;Score1;Score2;Score3")) {
                        continue;
                    }
                }

                if (line.isBlank()) {
                    continue;
                }

                try {
                    Student student = parseStudent(line);
                    students.add(student);

                    totalScore += student.getScore1()
                            + student.getScore2()
                            + student.getScore3();

                    scoreCount += 3;
                    validCount++;

                } catch (NumberFormatException
                         | InvalidScoreException e) {
                    ErrorHandler.handle(e, line);
                    invalidCount++;
                    System.out.println(
                            "The error has been logged in error.log: " + line);
                }
            }

        } catch (IOException e) {
            ErrorHandler.handle(e, "Input file: " + inputFile);
            System.out.println("File read error: "
                    + e.getMessage());
            return;
        }

        writeResult(outputFile, students, totalScore,
                scoreCount, validCount, invalidCount);
    }

    private Student parseStudent(String line)
            throws InvalidScoreException {

        String[] parts = line.split(";", -1);

        if (parts.length != 4) {
            throw new NumberFormatException(
                    "Incorrect number of fields");
        }

        String studentID = parts[0].trim();

        if (studentID.isEmpty()) {
            throw new NumberFormatException(
                    "StudentID cannot be empty");
        }

        double score1 = Double.parseDouble(parts[1].trim());
        double score2 = Double.parseDouble(parts[2].trim());
        double score3 = Double.parseDouble(parts[3].trim());

        validateScore(score1);
        validateScore(score2);
        validateScore(score3);

        return new Student(studentID, score1, score2, score3);
    }

    private void validateScore(double score)
            throws InvalidScoreException {

        if (!Double.isFinite(score) || score < 0 || score > 100) {
            throw new InvalidScoreException(
                    "The score must be between 0 and 100: "
                            + score);
        }
    }

    private void writeResult(String outputFile,
                             List<Student> students,
                             double totalScore,
                             int scoreCount,
                             int validCount,
                             int invalidCount) {

        List<String> result = new ArrayList<>();

        result.add("STUDENT PROCESSING RESULTS");
        result.add("==========================");

        for (Student student : students) {
            result.add(student.toString());
        }

        double overallAverage = scoreCount > 0
                ? totalScore / scoreCount : 0;

        result.add("");
        result.add("Valid students: " + validCount);
        result.add("Invalid records: " + invalidCount);
        result.add("Overall average score: "
                + String.format(Locale.US, "%.2f",
                overallAverage));

        try (BufferedWriter writer =
                     Files.newBufferedWriter(Path.of(outputFile))) {
            for (String item : result) {
                writer.write(item);
                writer.newLine();
            }

            System.out.println("The result is recorded at " + outputFile);
            System.out.println("Number of students: " + validCount);
            System.out.println("Incorrect entries: " + invalidCount);
            System.out.printf(Locale.US,
                    "Overall average mark: %.2f%n",
                    overallAverage);

        } catch (IOException e) {
            ErrorHandler.handle(e, "Output file: " + outputFile);
            System.out.println("Error recording the result: "
                    + e.getMessage());
        }
    }
}