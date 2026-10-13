package lab10.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import lab10.model.Project;
import lab10.service.ProjectService;

public class MainController {
    private final ProjectService service = new ProjectService();
    private final ObservableList<Project> data =
            FXCollections.observableArrayList();

    private TableView<Project> table;
    private TextField nameField;
    private TextField managerField;
    private TextField durationField;
    private VBox view;

    public MainController() {
        initView();
        refreshTable();
    }

    private void initView() {
        table = new TableView<>();

        TableColumn<Project, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getId()));

        TableColumn<Project, String> nameCol = new TableColumn<>("Назва");
        nameCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getName()));

        TableColumn<Project, String> managerCol =
                new TableColumn<>("Керівник");
        managerCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getManager()));

        TableColumn<Project, Number> durationCol =
                new TableColumn<>("Тривалість");
        durationCol.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getDuration()));

        table.getColumns().addAll(
                idCol, nameCol, managerCol, durationCol
        );
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
        table.setItems(data);

        nameField = new TextField();
        nameField.setPromptText("Назва проєкту");

        managerField = new TextField();
        managerField.setPromptText("Керівник");

        durationField = new TextField();
        durationField.setPromptText("Тривалість (у днях)");

        Button addButton = new Button("Додати");
        Button updateButton = new Button("Редагувати");
        Button deleteButton = new Button("Видалити");
        Button clearButton = new Button("Очистити");

        addButton.setOnAction(e -> addProject());
        updateButton.setOnAction(e -> updateProject());
        deleteButton.setOnAction(e -> deleteProject());
        clearButton.setOnAction(e -> clearFields());

        table.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldProject, selectedProject) -> {
                    if (selectedProject != null) {
                        nameField.setText(selectedProject.getName());
                        managerField.setText(selectedProject.getManager());
                        durationField.setText(
                                String.valueOf(selectedProject.getDuration())
                        );
                    }
                });

        HBox buttons = new HBox(
                10, addButton, updateButton, deleteButton, clearButton
        );

        VBox form = new VBox(
                10, nameField, managerField, durationField, buttons
        );

        view = new VBox(12, table, form);
        view.setPadding(new Insets(15));
    }

    private void addProject() {
        if (!validateFields()) {
            return;
        }

        int duration = Integer.parseInt(durationField.getText().trim());

        int id = service.getAllProjects().stream()
                .mapToInt(Project::getId)
                .max()
                .orElse(0) + 1;

        Project project = new Project(
                id,
                nameField.getText().trim(),
                managerField.getText().trim(),
                duration
        );

        service.addProject(project);
        refreshTable();
        clearFields();
    }

    private void updateProject() {
        Project selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showMessage("Виберіть проєкт для редагування.");
            return;
        }

        if (!validateFields()) {
            return;
        }

        Project updated = new Project(
                selected.getId(),
                nameField.getText().trim(),
                managerField.getText().trim(),
                Integer.parseInt(durationField.getText().trim())
        );

        service.updateProject(updated);
        refreshTable();
        clearFields();
    }

    private void deleteProject() {
        Project selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showMessage("Виберіть проєкт для видалення.");
            return;
        }

        service.deleteProject(selected.getId());
        refreshTable();
        clearFields();
    }

    private void refreshTable() {
        data.setAll(service.getAllProjects());
    }

    private void clearFields() {
        nameField.clear();
        managerField.clear();
        durationField.clear();
        table.getSelectionModel().clearSelection();
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

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Система обліку проєктів");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Parent getView() {
        return view;
    }
}

