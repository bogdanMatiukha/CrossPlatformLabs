package main.controller;

import main.HttpRequest.HttpUniverPropertyClient;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.model.IssueRequest;
import main.model.PropertyItem;
import main.model.Transfer;
import main.model.User;
import main.status.ItemState;
import main.status.RequestState;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

public class MainController {
    private static final Logger logger = Logger.getLogger(MainController.class.getName());

    @FXML private TabPane mainTabPane;
    @FXML private Tab requestTab, userTab, propertyTab, transferTab;

    // Кнопки дій
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    // Таблиці
    @FXML private TableView<IssueRequest> requestTable;
    @FXML private TableColumn<IssueRequest, Integer> reqIdCol, reqUserCol, reqPropCol;
    @FXML private TableColumn<IssueRequest, RequestState> reqStatusCol;
    @FXML private TableColumn<IssueRequest, Double> reqIndexCol;

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> userIdCol;
    @FXML private TableColumn<User, String> userFullNameCol, userRoleCol, userEmailCol;

    @FXML private TableView<PropertyItem> propertyTable;
    @FXML private TableColumn<PropertyItem, Integer> propIdCol;
    @FXML private TableColumn<PropertyItem, String> propInvCol, propNameCol, propCatCol;
    @FXML private TableColumn<PropertyItem, ItemState> propStatusCol;

    @FXML private TableView<Transfer> transferTable;
    @FXML private TableColumn<Transfer, Integer> transIdCol, transReqCol;
    @FXML private TableColumn<Transfer, LocalDate> transDateCol, transReturnCol;
    @FXML private TableColumn<Transfer, String> transNotesCol;

    // Контейнери форм для кожної вкладки
    @FXML private HBox userFormBox;
    @FXML private HBox propertyFormBox;
    @FXML private VBox requestFormBox;
    @FXML private HBox transferFormBox;

    // Поля введення для Користувачів
    @FXML private TextField userFullNameField, userRoleField, userEmailField;

    // Поля введення для Майна
    @FXML private TextField propInvField, propNameField, propCatField;
    @FXML private ComboBox<ItemState> propStatusCombo;

    // Поля введення для Заявок
    @FXML private TextField reqUserIdField, reqPropIdField, reqF1Field, reqF2Field, reqF3Field;
    @FXML private ComboBox<RequestState> reqStatusCombo;

    // Поля введення для Передач (з використанням DatePicker)
    @FXML private TextField transReqIdField, transNotesField;
    @FXML private DatePicker transDatePicker, transReturnDatePicker;

    @FXML private Label statusLabel;
    @FXML private ProgressBar requestProgressBar;
    @FXML private Button importCsvButton;

    private final HttpUniverPropertyClient client = new HttpUniverPropertyClient();

    @FXML
    public void initialize() {
        logger.info("Initializing MainController...");

        // 1. Ініціалізація випадаючих списків статусів
        propStatusCombo.setItems(FXCollections.observableArrayList(ItemState.values()));
        propStatusCombo.getSelectionModel().selectFirst();

        reqStatusCombo.setItems(FXCollections.observableArrayList(RequestState.values()));
        reqStatusCombo.getSelectionModel().selectFirst();

        // 2. Налаштування колонок таблиць
        reqIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        reqUserCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        reqPropCol.setCellValueFactory(new PropertyValueFactory<>("propertyId"));
        reqStatusCol.setCellValueFactory(new PropertyValueFactory<>("requestStatus"));
        reqIndexCol.setCellValueFactory(new PropertyValueFactory<>("integralIndex"));

        userIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        userFullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        userRoleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        userEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        propIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        propInvCol.setCellValueFactory(new PropertyValueFactory<>("inventoryNumber"));
        propNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        propCatCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        propStatusCol.setCellValueFactory(new PropertyValueFactory<>("statusItem"));

        transIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        transReqCol.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        transDateCol.setCellValueFactory(new PropertyValueFactory<>("transferDate"));
        transReturnCol.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        transNotesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));

        setupButtonBindings();

        mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            clearForm();
            updateFormVisibility(newTab);
            setupButtonBindings();
        });

        setupSelectionListeners();
        loadAllData();
        updateFormVisibility(mainTabPane.getSelectionModel().getSelectedItem());
    }

    private void setupButtonBindings() {
        Tab activeTab = mainTabPane.getSelectionModel().getSelectedItem();

        updateButton.visibleProperty().unbind();
        updateButton.managedProperty().unbind();
        deleteButton.visibleProperty().unbind();
        deleteButton.managedProperty().unbind();
        importCsvButton.visibleProperty().unbind();
        importCsvButton.managedProperty().unbind();

        if (activeTab == userTab) {
            bindButtonVisibility(userTable);
        } else if (activeTab == propertyTab) {
            bindButtonVisibility(propertyTable);
        } else if (activeTab == requestTab) {
            bindButtonVisibility(requestTable);
        } else if (activeTab == transferTab) {
            bindButtonVisibility(transferTable);
        }

        importCsvButton.setVisible(activeTab == propertyTab);
        importCsvButton.setManaged(activeTab == propertyTab);
    }

    private <T> void bindButtonVisibility(TableView<T> table) {
        updateButton.visibleProperty().bind(table.getSelectionModel().selectedItemProperty().isNotNull());
        updateButton.managedProperty().bind(table.getSelectionModel().selectedItemProperty().isNotNull());

        deleteButton.visibleProperty().bind(table.getSelectionModel().selectedItemProperty().isNotNull());
        deleteButton.managedProperty().bind(table.getSelectionModel().selectedItemProperty().isNotNull());
    }

    private void updateFormVisibility(Tab selectedTab) {
        userFormBox.setVisible(false); userFormBox.setManaged(false);
        propertyFormBox.setVisible(false); propertyFormBox.setManaged(false);
        requestFormBox.setVisible(false); requestFormBox.setManaged(false);
        transferFormBox.setVisible(false); transferFormBox.setManaged(false);

        if (selectedTab == userTab) {
            userFormBox.setVisible(true); userFormBox.setManaged(true);
        } else if (selectedTab == propertyTab) {
            propertyFormBox.setVisible(true); propertyFormBox.setManaged(true);
        } else if (selectedTab == requestTab) {
            requestFormBox.setVisible(true); requestFormBox.setManaged(true);
        } else if (selectedTab == transferTab) {
            transferFormBox.setVisible(true); transferFormBox.setManaged(true);
        }
    }

    private void setupSelectionListeners() {
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                userFullNameField.setText(selected.getFullName());
                userRoleField.setText(selected.getRole());
                userEmailField.setText(selected.getEmail());
            }
        });

        propertyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                propInvField.setText(selected.getInventoryNumber());
                propNameField.setText(selected.getName());
                propCatField.setText(selected.getCategory());
                if (selected.getStatusItem() != null) {
                    propStatusCombo.setValue(selected.getStatusItem());
                }
            }
        });

        requestTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                reqUserIdField.setText(String.valueOf(selected.getUserId()));
                reqPropIdField.setText(String.valueOf(selected.getPropertyId()));
                if (selected.getRequestStatus() != null) {
                    reqStatusCombo.setValue(selected.getRequestStatus());
                }
                reqF1Field.setText(String.valueOf(selected.getUrgencyF1()));
                reqF2Field.setText(String.valueOf(selected.getCriticalityF2()));
                reqF3Field.setText(String.valueOf(selected.getDeficitF3()));
            }
        });

        transferTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                transReqIdField.setText(String.valueOf(selected.getRequestId()));
                transDatePicker.setValue(selected.getTransferDate());
                transReturnDatePicker.setValue(selected.getReturnDate());
                transNotesField.setText(selected.getNotes());
            }
        });
    }

    @FXML
    private void handleRefresh() {
        loadAllData();
        clearForm();
    }

    @FXML
    private void handleAdd() {
        Tab activeTab = mainTabPane.getSelectionModel().getSelectedItem();
        try {
            if (activeTab == userTab) {
                User user = new User(0, userFullNameField.getText().trim(), userRoleField.getText().trim(), userEmailField.getText().trim());
                runAction(() -> client.addUser(user), "Додавання користувача...", "Користувача успішно додано");
            } else if (activeTab == propertyTab) {
                ItemState state = propStatusCombo.getValue();
                PropertyItem item = new PropertyItem(0, propInvField.getText().trim(), propNameField.getText().trim(), propCatField.getText().trim(), state);
                runAction(() -> client.addProperty(item), "Додавання майна...", "Майно успішно додано");
            } else if (activeTab == requestTab) {
                int userId = Integer.parseInt(reqUserIdField.getText().trim());
                int propId = Integer.parseInt(reqPropIdField.getText().trim());
                RequestState state = reqStatusCombo.getValue();
                int f1 = Integer.parseInt(reqF1Field.getText().trim());
                int f2 = Integer.parseInt(reqF2Field.getText().trim());
                int f3 = Integer.parseInt(reqF3Field.getText().trim());
                IssueRequest req = new IssueRequest(userId, propId, state, f1, f2, f3);
                runAction(() -> client.addRequest(req), "Створення заявки...", "Заявку успішно створено");
            } else if (activeTab == transferTab) {
                int reqId = Integer.parseInt(transReqIdField.getText().trim());
                LocalDate tDate = transDatePicker.getValue();
                LocalDate rDate = transReturnDatePicker.getValue();
                Transfer tr = new Transfer(0, reqId, tDate, rDate, transNotesField.getText().trim());
                runAction(() -> client.addTransfer(tr), "Додавання передачі...", "Запис передачі додано");
            }
        } catch (Exception e) {
            statusLabel.setText("Помилка додавання: перевірте правильність заповнення полів");
        }
    }

    @FXML
    private void handleUpdate() {
        Tab activeTab = mainTabPane.getSelectionModel().getSelectedItem();
        try {
            if (activeTab == userTab) {
                User sel = userTable.getSelectionModel().getSelectedItem();
                if (sel == null) return;
                User user = new User(sel.getId(), userFullNameField.getText().trim(), userRoleField.getText().trim(), userEmailField.getText().trim());
                runAction(() -> client.updateUser(user), "Оновлення...", "Користувача оновлено");
            } else if (activeTab == propertyTab) {
                PropertyItem sel = propertyTable.getSelectionModel().getSelectedItem();
                if (sel == null) return;
                ItemState state = propStatusCombo.getValue();
                PropertyItem item = new PropertyItem(sel.getId(), propInvField.getText().trim(), propNameField.getText().trim(), propCatField.getText().trim(), state);
                runAction(() -> client.updateProperty(item), "Оновлення...", "Майно оновлено");
            } else if (activeTab == requestTab) {
                IssueRequest sel = requestTable.getSelectionModel().getSelectedItem();
                if (sel == null) return;
                int userId = Integer.parseInt(reqUserIdField.getText().trim());
                int propId = Integer.parseInt(reqPropIdField.getText().trim());
                RequestState state = reqStatusCombo.getValue();
                int f1 = Integer.parseInt(reqF1Field.getText().trim());
                int f2 = Integer.parseInt(reqF2Field.getText().trim());
                int f3 = Integer.parseInt(reqF3Field.getText().trim());
                IssueRequest req = new IssueRequest(sel.getId(), userId, propId, state, f1, f2, f3, IssueRequest.calculateIntegralIndex(f1, f2, f3));
                runAction(() -> client.updateRequest(req), "Оновлення...", "Заявку оновлено");
            } else if (activeTab == transferTab) {
                Transfer sel = transferTable.getSelectionModel().getSelectedItem();
                if (sel == null) return;
                int reqId = Integer.parseInt(transReqIdField.getText().trim());
                LocalDate tDate = transDatePicker.getValue();
                LocalDate rDate = transReturnDatePicker.getValue();
                Transfer tr = new Transfer(sel.getId(), reqId, tDate, rDate, transNotesField.getText().trim());
                runAction(() -> client.updateTransfer(tr), "Оновлення...", "Запис передачі оновлено");
            }
        } catch (Exception e) {
            statusLabel.setText("Помилка оновлення: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Tab activeTab = mainTabPane.getSelectionModel().getSelectedItem();
        try {
            if (activeTab == userTab) {
                User sel = userTable.getSelectionModel().getSelectedItem();
                if (sel == null) return;
                runAction(() -> client.deleteUser(sel.getId()), "Видалення...", "Користувача видалено");
            } else if (activeTab == propertyTab) {
                PropertyItem sel = propertyTable.getSelectionModel().getSelectedItem();
                if (sel == null) return;
                runAction(() -> client.deleteProperty(sel.getId()), "Видалення...", "Майно видалено");
            } else if (activeTab == requestTab) {
                IssueRequest sel = requestTable.getSelectionModel().getSelectedItem();
                if (sel == null) return;
                runAction(() -> client.deleteRequest(sel.getId()), "Видалення...", "Заявку видалено");
            } else if (activeTab == transferTab) {
                Transfer sel = transferTable.getSelectionModel().getSelectedItem();
                if (sel == null) return;
                runAction(() -> client.deleteTransfer(sel.getId()), "Видалення...", "Запис передачі видалено");
            }
        } catch (Exception e) {
            statusLabel.setText("Помилка видалення: " + e.getMessage());
        }
    }

    @FXML
    private void clearForm() {
        userFullNameField.clear(); userRoleField.clear(); userEmailField.clear();
        propInvField.clear(); propNameField.clear(); propCatField.clear();
        if (!propStatusCombo.getItems().isEmpty()) propStatusCombo.getSelectionModel().selectFirst();

        reqUserIdField.clear(); reqPropIdField.clear();
        if (!reqStatusCombo.getItems().isEmpty()) reqStatusCombo.getSelectionModel().selectFirst();
        reqF1Field.clear(); reqF2Field.clear(); reqF3Field.clear();

        transReqIdField.clear(); transDatePicker.setValue(null); transReturnDatePicker.setValue(null); transNotesField.clear();

        userTable.getSelectionModel().clearSelection();
        propertyTable.getSelectionModel().clearSelection();
        requestTable.getSelectionModel().clearSelection();
        transferTable.getSelectionModel().clearSelection();
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

    @FXML
    private void handleImportCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Імпорт майна з CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV файли", "*.csv")
        );

        File file = fileChooser.showOpenDialog(
                propertyTable.getScene().getWindow()
        );

        if (file == null) {
            return;
        }

        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() throws Exception {
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

                int total = dataLines.size() - startIndex;

                if (total <= 0) {
                    throw new IllegalArgumentException(
                            "CSV-файл не містить записів майна");
                }

                int imported = 0;

                for (int i = startIndex; i < dataLines.size(); i++) {
                    if (isCancelled()) {
                        break;
                    }

                    int rowNumber = i + 1;
                    List<String> fields = parseCsvLine(
                            dataLines.get(i), delimiter);

                    if (fields.size() != 4) {
                        throw new IllegalArgumentException(
                                "Рядок " + rowNumber
                                        + ": очікується 4 поля");
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
                                "Рядок " + rowNumber
                                        + ": заповніть усі поля");
                    }

                    ItemState state;

                    try {
                        state = ItemState.valueOf(
                                statusText.toUpperCase(java.util.Locale.ROOT));
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

                    updateMessage("Імпорт майна: "
                            + (imported + 1) + " із " + total);

                    client.addProperty(item);

                    imported++;
                    updateProgress(imported, total);
                }

                return imported;
            }
        };

        requestProgressBar.progressProperty().unbind();
        statusLabel.textProperty().unbind();

        showProgress();

        requestProgressBar.setProgress(0);
        requestProgressBar.progressProperty().bind(task.progressProperty());
        statusLabel.textProperty().bind(task.messageProperty());

        statusLabel.getStyleClass().removeAll("status-error");
        statusLabel.getStyleClass().removeAll("status-loading");
        statusLabel.getStyleClass().add("status-loading");

        task.setOnSucceeded(e -> {
            requestProgressBar.progressProperty().unbind();
            requestProgressBar.setProgress(1);

            statusLabel.textProperty().unbind();
            statusLabel.setText(
                    "Імпорт завершено. Додано записів: " + task.getValue());
            setStatusStyle(false);

            loadAllData();
        });

        task.setOnFailed(e -> {
            requestProgressBar.progressProperty().unbind();
            requestProgressBar.setProgress(0);

            statusLabel.textProperty().unbind();
            statusLabel.setText("Помилка імпорту: "
                    + task.getException().getMessage());
            setStatusStyle(true);

            loadAllData();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadAllData() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                updateProgress(0, 4);
                updateMessage("Завантаження користувачів...");
                List<User> users = client.getAllUsers();

                updateProgress(1, 4);
                updateMessage("Завантаження майна...");
                List<PropertyItem> props = client.getAllProperties();

                updateProgress(2, 4);
                updateMessage("Завантаження заявок...");
                List<IssueRequest> reqs = client.getAllRequests();

                updateProgress(3, 4);
                updateMessage("Завантаження передач...");
                List<Transfer> trans = client.getAllTransfers();

                javafx.application.Platform.runLater(() -> {
                    userTable.setItems(FXCollections.observableArrayList(users));
                    propertyTable.setItems(FXCollections.observableArrayList(props));
                    requestTable.setItems(FXCollections.observableArrayList(reqs));
                    transferTable.setItems(FXCollections.observableArrayList(trans));
                });

                updateProgress(4, 4);
                return null;
            }
        };

        requestProgressBar.progressProperty().unbind();
        statusLabel.textProperty().unbind();

        showProgress();

        requestProgressBar.progressProperty().bind(task.progressProperty());
        statusLabel.textProperty().bind(task.messageProperty());

        statusLabel.getStyleClass().removeAll("status-error", "status-loading");
        statusLabel.getStyleClass().add("status-loading");

        task.setOnSucceeded(e -> {
            requestProgressBar.progressProperty().unbind();
            statusLabel.textProperty().unbind();

            hideProgress();

            statusLabel.setText("Дані успішно оновлено");
            setStatusStyle(false);
        });

        task.setOnFailed(e -> {
            requestProgressBar.progressProperty().unbind();
            statusLabel.textProperty().unbind();

            hideProgress();

            statusLabel.setText("Помилка завантаження: "
                    + task.getException().getMessage());
            setStatusStyle(true);
        });

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    private void showProgress() {
        requestProgressBar.setVisible(true);
        requestProgressBar.setManaged(true);
    }

    private void hideProgress() {
        requestProgressBar.progressProperty().unbind();
        requestProgressBar.setProgress(0);
        requestProgressBar.setVisible(false);
        requestProgressBar.setManaged(false);
    }

    private void setStatusStyle(boolean error) {
        statusLabel.getStyleClass().removeAll(
                "status-error", "status-loading");

        if (error) {
            statusLabel.getStyleClass().add("status-error");
        }
    }

    private void runAction(Runnable action, String startMsg, String successMsg) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                updateMessage(startMsg);
                updateProgress(-1, 1);

                action.run();

                updateProgress(1, 1);
                return null;
            }
        };

        requestProgressBar.progressProperty().unbind();
        statusLabel.textProperty().unbind();

        showProgress();

        requestProgressBar.progressProperty().bind(task.progressProperty());
        statusLabel.textProperty().bind(task.messageProperty());

        statusLabel.getStyleClass().removeAll("status-error", "status-loading");
        statusLabel.getStyleClass().add("status-loading");

        task.setOnSucceeded(e -> {
            requestProgressBar.progressProperty().unbind();
            statusLabel.textProperty().unbind();

            hideProgress();

            statusLabel.setText(successMsg);
            setStatusStyle(false);

            clearForm();
            loadAllData();
        });

        task.setOnFailed(e -> {
            requestProgressBar.progressProperty().unbind();
            statusLabel.textProperty().unbind();

            hideProgress();

            statusLabel.setText("Помилка запиту: "
                    + task.getException().getMessage());
            setStatusStyle(true);
        });

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }
}