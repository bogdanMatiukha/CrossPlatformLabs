package main.controller;

import main.client.HttpProjectClient;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.model.Project;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MainController {
    private static final Logger logger = Logger.getLogger(MainController.class.getName());

    @FXML private TableView<Project> tableView;
    @FXML private TableColumn<Project, Integer> idColumn;
    @FXML private TableColumn<Project, String> nameColumn;
    @FXML private TableColumn<Project, String> managerColumn;
    @FXML private TableColumn<Project, Integer> durationColumn;

    @FXML private TextField nameField;
    @FXML private TextField managerField;
    @FXML private TextField durationField;

    @FXML private ComboBox<String> fieldComboBox;
    @FXML private TextField filterField;
    @FXML private Label statusLabel;

    private final HttpProjectClient client = new HttpProjectClient();

    private final Map<String, String> fieldMap = Map.of(
            "ID", "id",
            "Назва", "name",
            "Керівник", "manager",
            "Тривалість", "duration"
    );

    @FXML
    public void initialize() {
        logger.info("Initializing MainController...");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        managerColumn.setCellValueFactory(new PropertyValueFactory<>("manager"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

        if (fieldComboBox != null) {
            fieldComboBox.setItems(FXCollections.observableArrayList(
                    "ID", "Назва", "Керівник", "Тривалість"
            ));
            fieldComboBox.getSelectionModel().select("Назва");
        }

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) -> {
            if (selected != null) {
                nameField.setText(selected.getName());
                managerField.setText(selected.getManager());
                durationField.setText(String.valueOf(selected.getDuration()));
            }
        });

        loadProjects();
    }

    @FXML
    private void handleRefresh() {
        logger.info("User requested project list refresh");
        loadProjects();
    }

    @FXML
    private void handleAdd() {
        logger.info("User clicked Add Project");
        Project project = readForm();
        if (project == null) return;
        runAction(() -> client.addProject(project), "Додавання...", "Проєкт успішно додано");
    }

    @FXML
    private void handleUpdate() {
        logger.info("User clicked Update Project");
        Project selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Оберіть проєкт для редагування");
            return;
        }

        String name = nameField.getText().trim();
        String manager = managerField.getText().trim();

        if (name.isEmpty() || manager.isEmpty()) {
            statusLabel.setText("Заповніть назву проєкту та керівника");
            return;
        }

        try {
            int duration = Integer.parseInt(durationField.getText().trim());
            if (duration <= 0) {
                statusLabel.setText("Тривалість має бути більшою за нуль");
                return;
            }

            Project project = new Project(selected.getId(), name, manager, duration);
            runAction(() -> client.updateProject(project), "Оновлення...", "Проєкт оновлено");

        } catch (NumberFormatException e) {
            statusLabel.setText("Тривалість має бути цілим числом");
        }
    }

    @FXML
    private void handleDelete() {
        logger.info("User clicked Delete Project");
        Project selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Оберіть проєкт для видалення");
            return;
        }
        runAction(() -> client.deleteProject(selected.getId()), "Видалення...", "Проєкт видалено");
    }

    @FXML
    private void filterExact() {
        filter(false);
    }

    @FXML
    private void filterLike() {
        filter(true);
    }

    private void filter(boolean like) {
        String selectedField = fieldComboBox.getValue();
        String value = filterField.getText().trim();

        if (selectedField == null || value.isEmpty()) {
            statusLabel.setText("Оберіть поле та введіть значення для пошуку.");
            return;
        }

        try {
            List<Project> all = client.getAllProjects();
            String dbField = fieldMap.get(selectedField);

            List<Project> filtered = all.stream().filter(p -> {
                String valStr = switch (dbField) {
                    case "id" -> String.valueOf(p.getId());
                    case "name" -> p.getName();
                    case "manager" -> p.getManager();
                    case "duration" -> String.valueOf(p.getDuration());
                    default -> "";
                };
                if (like) {
                    return valStr.toLowerCase().contains(value.toLowerCase());
                } else {
                    return valStr.equalsIgnoreCase(value);
                }
            }).collect(Collectors.toList());

            tableView.setItems(FXCollections.observableArrayList(filtered));
            statusLabel.setText("Знайдено записів: " + filtered.size());
        } catch (Exception e) {
            statusLabel.setText("Помилка фільтрації: " + e.getMessage());
        }
    }

    @FXML
    private void resetFilter() {
        if (filterField != null) filterField.clear();
        loadProjects();
    }

    private void loadProjects() {
        Task<List<Project>> task = new Task<>() {
            @Override
            protected List<Project> call() {
                updateMessage("Завантаження даних із сервера...");
                return client.getAllProjects();
            }
        };
        statusLabel.textProperty().bind(task.messageProperty());
        task.setOnSucceeded(event -> {
            statusLabel.textProperty().unbind();
            tableView.setItems(FXCollections.observableArrayList(task.getValue()));
            statusLabel.setText("Дані успішно завантажено");
        });
        task.setOnFailed(event -> {
            statusLabel.textProperty().unbind();
            statusLabel.setText("Помилка: " + task.getException().getMessage());
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void runAction(Runnable action, String startMessage, String successMessage) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                updateMessage(startMessage);
                action.run();
                return null;
            }
        };
        statusLabel.textProperty().bind(task.messageProperty());
        task.setOnSucceeded(event -> {
            statusLabel.textProperty().unbind();
            statusLabel.setText(successMessage);
            clearForm();
            loadProjects();
        });
        task.setOnFailed(event -> {
            statusLabel.textProperty().unbind();
            statusLabel.setText("Помилка: " + task.getException().getMessage());
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private Project readForm() {
        String name = nameField.getText().trim();
        String manager = managerField.getText().trim();
        if (name.isEmpty() || manager.isEmpty()) {
            statusLabel.setText("Заповніть назву проєкту та керівника");
            return null;
        }
        try {
            int duration = Integer.parseInt(durationField.getText().trim());
            if (duration <= 0) {
                statusLabel.setText("Тривалість має бути більшою за нуль");
                return null;
            }
            return new Project(name, manager, duration);
        } catch (NumberFormatException e) {
            statusLabel.setText("Тривалість має бути цілим числом");
            return null;
        }
    }

    private void clearForm() {
        nameField.clear();
        managerField.clear();
        durationField.clear();
        tableView.getSelectionModel().clearSelection();
    }
}