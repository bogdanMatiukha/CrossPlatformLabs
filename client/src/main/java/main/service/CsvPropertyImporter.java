package main.service;

import main.model.PropertyItem;
import main.status.ItemState;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CsvPropertyImporter {

    public List<PropertyItem> readProperties(File file) throws IOException {
        List<String> lines = Files.readAllLines(
                file.toPath(), StandardCharsets.UTF_8);

        if (!lines.isEmpty()) {
            lines.set(0, lines.get(0).replace("\uFEFF", ""));
        }

        List<String> dataLines = new ArrayList<>();

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                dataLines.add(line);
            }
        }

        if (dataLines.isEmpty()) {
            throw new IllegalArgumentException("CSV-файл порожній");
        }

        char delimiter = dataLines.get(0).contains(";") ? ';' : ',';

        String firstColumn = parseCsvLine(
                dataLines.get(0), delimiter).get(0).trim();

        int startIndex = firstColumn.equalsIgnoreCase("inventoryNumber")
                || firstColumn.equalsIgnoreCase("inventory_number")
                || firstColumn.equalsIgnoreCase("інвентарний номер")
                ? 1 : 0;

        if (dataLines.size() - startIndex <= 0) {
            throw new IllegalArgumentException(
                    "CSV-файл не містить записів майна");
        }

        List<PropertyItem> items = new ArrayList<>();

        for (int i = startIndex; i < dataLines.size(); i++) {
            int rowNumber = i + 1;

            List<String> fields = parseCsvLine(
                    dataLines.get(i), delimiter);

            if (fields.size() != 4) {
                throw new IllegalArgumentException(
                        "Рядок " + rowNumber + ": очікується 4 поля");
            }

            String inventoryNumber = fields.get(0).trim();
            String name = fields.get(1).trim();
            String category = fields.get(2).trim();
            String statusText = fields.get(3).trim();

            if (inventoryNumber.isEmpty()
                    || name.isEmpty()
                    || category.isEmpty()
                    || statusText.isEmpty()) {
                throw new IllegalArgumentException(
                        "Рядок " + rowNumber + ": заповніть усі поля");
            }

            ItemState state;

            try {
                state = ItemState.valueOf(
                        statusText.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException(
                        "Рядок " + rowNumber
                                + ": невідомий статус " + statusText);
            }

            PropertyItem item = new PropertyItem(
                    0,
                    inventoryNumber,
                    name,
                    category,
                    state
            );

            items.add(item);
        }

        return items;
    }

    private List<String> parseCsvLine(String line, char delimiter) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                if (quoted && i + 1 < line.length()
                        && line.charAt(i + 1) == '"') {
                    field.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (ch == delimiter && !quoted) {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(ch);
            }
        }

        if (quoted) {
            throw new IllegalArgumentException(
                    "Незакриті лапки у CSV-рядку");
        }

        fields.add(field.toString());

        return fields;
    }
}