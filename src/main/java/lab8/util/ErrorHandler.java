package lab8.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class ErrorHandler {
    private static final String LOG_FILE = "error.log";

    public static void handle(Exception e, String line) {
        String logEntry = LocalDateTime.now()
                + " | " + e.getClass().getSimpleName()
                + " | " + e.getMessage()
                + " | Record: " + line;

        try (BufferedWriter writer =
                     new BufferedWriter(
                             new FileWriter(LOG_FILE, true))) {
            writer.write(logEntry);
            writer.newLine();
        } catch (IOException ex) {
            System.out.println(
                    "The error could not be logged to error.log.");
        }
    }
}

