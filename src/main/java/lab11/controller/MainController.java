package lab11.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import lab11.model.Project;
import lab11.service.ProjectService;

import java.util.Map;

public class MainController {
    private final ProjectService service = new ProjectService();

    @FXML private TableView<Project> table;
    @FXML private TableColumn<Project, Number> idCol;
    @FXML private TableColumn<Project, String> nameCol;
    @FXML private TableColumn<Project, String> managerCol;
    @FXML private TableColumn<Project, Number> durationCol;

    @FXML private TextField nameField;
    @FXML private TextField managerField;
    @FXML private TextField durationField;

    @FXML private ComboBox<String> fieldComboBox;
    @FXML private TextField filterField;

    private final Map<String, String> fieldMap = Map.of(
            "ID", "id",
            "Назва", "name",
            "Керівник", "manager",
            "Тривалість", "duration"
    );

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getId()));

        nameCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getName()));

        managerCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getManager()));

        durationCol.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getDuration()));

        fieldComboBox.setItems(FXCollections.observableArrayList(
                "ID",
                "Назва",
                "Керівник",
                "Тривалість"
        ));

        fieldComboBox.getSelectionModel().select("Назва");

        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldProject, selectedProject) -> {
                    if (selectedProject != null) {
                        nameField.setText(selectedProject.getName());
                        managerField.setText(selectedProject.getManager());
                        durationField.setText(
                                String.valueOf(selectedProject.getDuration())
                        );
                    }
                }
        );

        refreshTable();
    }

    @FXML
    private void addProject() {
        if (!validateFields()) {
            return;
        }

        try {
            Project project = new Project(
                    nameField.getText().trim(),
                    managerField.getText().trim(),
                    Integer.parseInt(durationField.getText().trim())
            );

            service.addProject(project);
            refreshTable();
            clearFields();

        } catch (RuntimeException e) {
            showMessage(errorMessage(e));
        }
    }

    @FXML
    private void updateProject() {
        Project selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showMessage("Виберіть проєкт для редагування.");
            return;
        }

        if (!validateFields()) {
            return;
        }

        try {
            Project updated = new Project(
                    selected.getId(),
                    nameField.getText().trim(),
                    managerField.getText().trim(),
                    Integer.parseInt(durationField.getText().trim())
            );

            service.updateProject(updated);
            refreshTable();
            clearFields();

        } catch (RuntimeException e) {
            showMessage(errorMessage(e));
        }
    }

    @FXML
    private void deleteProject() {
        Project selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showMessage("Виберіть проєкт для видалення.");
            return;
        }

        try {
            service.deleteProject(selected.getId());
            refreshTable();
            clearFields();

        } catch (RuntimeException e) {
            showMessage(errorMessage(e));
        }
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
            showMessage("Оберіть поле та введіть значення для пошуку.");
            return;
        }

        try {
            String dbField = fieldMap.get(selectedField);

            var result = like
                    ? service.filterLike(dbField, value)
                    : service.filterExact(dbField, value);

            table.setItems(FXCollections.observableArrayList(result));

        } catch (RuntimeException e) {
            showMessage(errorMessage(e));
        }
    }

    @FXML
    private void resetFilter() {
        filterField.clear();
        refreshTable();
    }

    @FXML
    private void clearFields() {
        nameField.clear();
        managerField.clear();
        durationField.clear();
        table.getSelectionModel().clearSelection();
    }

    private void refreshTable() {
        try {
            table.setItems(
                    FXCollections.observableArrayList(
                            service.getAllProjects()
                    )
            );
        } catch (RuntimeException e) {
            showMessage(errorMessage(e));
        }
    }

    private boolean validateFields() {
        if (nameField.getText().trim().isEmpty()
                || managerField.getText().trim().isEmpty()
                || durationField.getText().trim().isEmpty()) {
            showMessage("Заповніть усі поля.");
            return false;
        }

        try {
            int duration = Integer.parseInt(durationField.getText().trim());

            if (duration <= 0) {
                showMessage("Тривалість має бути більшою за нуль.");
                return false;
            }

        } catch (NumberFormatException e) {
            showMessage("Тривалість повинна бути цілим числом.");
            return false;
        }

        return true;
    }

    private String errorMessage(Throwable error) {
        Throwable cause = error;

        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        return cause.getMessage() == null
                ? "Сталася помилка під час роботи з базою даних."
                : cause.getMessage();
    }

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Система обліку проєктів");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}