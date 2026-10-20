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

        if (activeTab == userTab) {
            bindButtonVisibility(userTable);
        } else if (activeTab == propertyTab) {
            bindButtonVisibility(propertyTable);
        } else if (activeTab == requestTab) {
            bindButtonVisibility(requestTable);
        } else if (activeTab == transferTab) {
            bindButtonVisibility(transferTable);
        }
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

    private void loadAllData() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                List<User> users = client.getAllUsers();
                List<PropertyItem> props = client.getAllProperties();
                List<IssueRequest> reqs = client.getAllRequests();
                List<Transfer> trans = client.getAllTransfers();

                javafx.application.Platform.runLater(() -> {
                    userTable.setItems(FXCollections.observableArrayList(users));
                    propertyTable.setItems(FXCollections.observableArrayList(props));
                    requestTable.setItems(FXCollections.observableArrayList(reqs));
                    transferTable.setItems(FXCollections.observableArrayList(trans));
                });
                return null;
            }
        };
        task.setOnSucceeded(e -> statusLabel.setText("Дані успішно оновлено"));
        task.setOnFailed(e -> statusLabel.setText("Помилка завантаження даних"));
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    private void runAction(Runnable action, String startMsg, String successMsg) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                updateMessage(startMsg);
                action.run();
                return null;
            }
        };
        statusLabel.textProperty().bind(task.messageProperty());
        task.setOnSucceeded(e -> {
            statusLabel.textProperty().unbind();
            statusLabel.setText(successMsg);
            clearForm();
            loadAllData();
        });
        task.setOnFailed(e -> {
            statusLabel.textProperty().unbind();
            statusLabel.setText("Помилка: " + task.getException().getMessage());
        });
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }
}