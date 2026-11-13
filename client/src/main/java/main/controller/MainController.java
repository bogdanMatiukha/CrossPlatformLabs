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
import main.service.CsvPropertyImporter;
import main.service.TableFilterManager;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

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

    @FXML private VBox formContainer;
    @FXML private VBox searchPanel;

    @FXML private Button togglePanelButton;
    @FXML private TextField searchField;
    @FXML private TextField filterValueField;
    @FXML private ComboBox<String> filterFieldCombo;

    private final ObservableList<User> allUsers =
            FXCollections.observableArrayList();
    private final ObservableList<PropertyItem> allProperties =
            FXCollections.observableArrayList();
    private final ObservableList<IssueRequest> allRequests =
            FXCollections.observableArrayList();
    private final ObservableList<Transfer> allTransfers =
            FXCollections.observableArrayList();

    private final FilteredList<User> filteredUsers =
            new FilteredList<>(allUsers);
    private final FilteredList<PropertyItem> filteredProperties =
            new FilteredList<>(allProperties);
    private final FilteredList<IssueRequest> filteredRequests =
            new FilteredList<>(allRequests);
    private final FilteredList<Transfer> filteredTransfers =
            new FilteredList<>(allTransfers);

    private boolean searchPanelVisible;

    private final HttpUniverPropertyClient client = new HttpUniverPropertyClient();

    private final CsvPropertyImporter csvImporter =
            new CsvPropertyImporter();

    private TableFilterManager filterManager;

    @FXML
    public void initialize() {
        logger.info("Initializing MainController...");

        // 1. Ініціалізація випадаючих списків статусів
        propStatusCombo.setItems(FXCollections.observableArrayList(ItemState.values()));
        propStatusCombo.getSelectionModel().selectFirst();

        reqStatusCombo.setItems(
                FXCollections.observableArrayList(RequestState.CREATED)
        );
        reqStatusCombo.setValue(RequestState.CREATED);

        // 2. Ініціалізація фільтрів
        filterManager = new TableFilterManager(
                requestTab,
                userTab,
                propertyTab,
                transferTab,
                filterFieldCombo,
                searchField,
                filterValueField,
                filteredUsers,
                filteredProperties,
                filteredRequests,
                filteredTransfers
        );

        filterManager.initialize();
        filterManager.updateFilterOptions(
                mainTabPane.getSelectionModel().getSelectedItem()
        );

        // 3. Налаштування колонок таблиць
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

        // 4. Налаштування кнопок
        setupButtonBindings();

        // 5. Обробка перемикання вкладок
        mainTabPane.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldTab, newTab) -> {
                    clearForm();
                    updateFormVisibility(newTab);
                    setupButtonBindings();

                    filterManager.updateFilterOptions(newTab);
                    filterManager.applyFilters();
                });

        // 6. Налаштування вибору записів
        setupSelectionListeners();

        // 7. Завантаження даних
        loadAllData();

        // 8. Відображення форми для активної вкладки
        updateFormVisibility(mainTabPane.getSelectionModel().getSelectedItem());

        // 9. Прив'язка даних до таблиць
        userTable.setItems(filteredUsers);
        propertyTable.setItems(filteredProperties);
        requestTable.setItems(filteredRequests);
        transferTable.setItems(filteredTransfers);

        // 10. Початкове налаштування панелі пошуку
        searchPanel.setVisible(false);
        searchPanel.setManaged(false);
    }

    @FXML
    private void handleTogglePanel() {
        searchPanelVisible = !searchPanelVisible;

        formContainer.setVisible(!searchPanelVisible);
        formContainer.setManaged(!searchPanelVisible);

        searchPanel.setVisible(searchPanelVisible);
        searchPanel.setManaged(searchPanelVisible);

        Label icon = (Label) togglePanelButton.getGraphic();
        icon.setText(searchPanelVisible ? "⚙" : "⌕");
        togglePanelButton.setTooltip(new Tooltip(
                searchPanelVisible
                        ? "Панель керування"
                        : "Пошук і фільтри"
        ));
    }

    @FXML
    private void handleResetFilters() {
        filterManager.resetFilters();
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
                    updateAllowedRequestStatuses(selected.getRequestStatus());
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


    private void updateAllowedRequestStatuses(RequestState currentStatus) {
        List<RequestState> allowedStatuses = switch (currentStatus) {
            case CREATED -> List.of(
                    RequestState.CREATED,
                    RequestState.APPROVED,
                    RequestState.CANCELLED
            );
            case APPROVED -> List.of(
                    RequestState.APPROVED,
                    RequestState.PUBLISHED,
                    RequestState.CANCELLED
            );
            case PUBLISHED -> List.of(
                    RequestState.PUBLISHED,
                    RequestState.RETURNED
            );
            case RETURNED -> List.of(RequestState.RETURNED);
            case CANCELLED -> List.of(RequestState.CANCELLED);
        };

        reqStatusCombo.setItems(
                FXCollections.observableArrayList(allowedStatuses)
        );
        reqStatusCombo.setValue(currentStatus);
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
                RequestState state = RequestState.CREATED;
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
        if (!propStatusCombo.getItems().isEmpty()) {
            propStatusCombo.getSelectionModel().selectFirst();
        }

        reqUserIdField.clear(); reqPropIdField.clear();
        reqStatusCombo.setItems(
                FXCollections.observableArrayList(RequestState.CREATED)
        );
        reqStatusCombo.setValue(RequestState.CREATED);
        reqF1Field.clear(); reqF2Field.clear(); reqF3Field.clear();

        transReqIdField.clear(); transDatePicker.setValue(null); transReturnDatePicker.setValue(null); transNotesField.clear();

        userTable.getSelectionModel().clearSelection();
        propertyTable.getSelectionModel().clearSelection();
        requestTable.getSelectionModel().clearSelection();
        transferTable.getSelectionModel().clearSelection();
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
                List<PropertyItem> items = csvImporter.readProperties(file);

                int total = items.size();
                int imported = 0;

                for (PropertyItem item : items) {
                    if (isCancelled()) {
                        break;
                    }

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
                    allUsers.setAll(users);
                    allProperties.setAll(props);
                    allRequests.setAll(reqs);
                    allTransfers.setAll(trans);

                    filterManager.applyFilters();
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