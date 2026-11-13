package main.service;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import main.model.IssueRequest;
import main.model.PropertyItem;
import main.model.Transfer;
import main.model.User;

import java.util.Locale;

public class TableFilterManager {

    private final Tab requestTab;
    private final Tab userTab;
    private final Tab propertyTab;
    private final Tab transferTab;

    private final ComboBox<String> filterFieldCombo;
    private final TextField searchField;
    private final TextField filterValueField;

    private final FilteredList<User> filteredUsers;
    private final FilteredList<PropertyItem> filteredProperties;
    private final FilteredList<IssueRequest> filteredRequests;
    private final FilteredList<Transfer> filteredTransfers;

    public TableFilterManager(
            Tab requestTab,
            Tab userTab,
            Tab propertyTab,
            Tab transferTab,
            ComboBox<String> filterFieldCombo,
            TextField searchField,
            TextField filterValueField,
            FilteredList<User> filteredUsers,
            FilteredList<PropertyItem> filteredProperties,
            FilteredList<IssueRequest> filteredRequests,
            FilteredList<Transfer> filteredTransfers) {

        this.requestTab = requestTab;
        this.userTab = userTab;
        this.propertyTab = propertyTab;
        this.transferTab = transferTab;
        this.filterFieldCombo = filterFieldCombo;
        this.searchField = searchField;
        this.filterValueField = filterValueField;
        this.filteredUsers = filteredUsers;
        this.filteredProperties = filteredProperties;
        this.filteredRequests = filteredRequests;
        this.filteredTransfers = filteredTransfers;
    }

    public void initialize() {
        searchField.textProperty().addListener(
                (obs, oldValue, newValue) -> applyFilters());

        filterValueField.textProperty().addListener(
                (obs, oldValue, newValue) -> applyFilters());

        filterFieldCombo.valueProperty().addListener(
                (obs, oldValue, newValue) -> applyFilters());

        updateFilterOptions(requestTab);
    }

    public void updateFilterOptions(Tab tab) {
        if (tab == requestTab) {
            filterFieldCombo.setItems(FXCollections.observableArrayList(
                    "Усі поля", "ID", "ID користувача",
                    "ID майна", "Статус", "Індекс"));
        } else if (tab == userTab) {
            filterFieldCombo.setItems(FXCollections.observableArrayList(
                    "Усі поля", "ID", "ПІБ", "Роль", "Email"));
        } else if (tab == propertyTab) {
            filterFieldCombo.setItems(FXCollections.observableArrayList(
                    "Усі поля", "ID", "Інвентарний номер",
                    "Назва", "Категорія", "Статус"));
        } else if (tab == transferTab) {
            filterFieldCombo.setItems(FXCollections.observableArrayList(
                    "Усі поля", "ID", "ID заявки",
                    "Дата передачі", "Дата повернення", "Примітки"));
        }

        filterFieldCombo.getSelectionModel().selectFirst();
    }

    public void applyFilters() {
        String search = normalize(searchField.getText());
        String filter = normalize(filterValueField.getText());
        String field = filterFieldCombo.getValue();

        filteredUsers.setPredicate(user ->
                matches(search, user.getId(), user.getFullName(),
                        user.getRole(), user.getEmail())
                        && matchesFilter(field, filter,
                        new String[]{"ID", "ПІБ", "Роль", "Email"},
                        new Object[]{user.getId(), user.getFullName(),
                                user.getRole(), user.getEmail()}));

        filteredProperties.setPredicate(item ->
                matches(search, item.getId(), item.getInventoryNumber(),
                        item.getName(), item.getCategory(), item.getStatusItem())
                        && matchesFilter(field, filter,
                        new String[]{"ID", "Інвентарний номер", "Назва",
                                "Категорія", "Статус"},
                        new Object[]{item.getId(), item.getInventoryNumber(),
                                item.getName(), item.getCategory(),
                                item.getStatusItem()}));

        filteredRequests.setPredicate(req ->
                matches(search, req.getId(), req.getUserId(),
                        req.getPropertyId(), req.getRequestStatus(),
                        req.getIntegralIndex())
                        && matchesFilter(field, filter,
                        new String[]{"ID", "ID користувача", "ID майна",
                                "Статус", "Індекс"},
                        new Object[]{req.getId(), req.getUserId(),
                                req.getPropertyId(), req.getRequestStatus(),
                                req.getIntegralIndex()}));

        filteredTransfers.setPredicate(tr ->
                matches(search, tr.getId(), tr.getRequestId(),
                        tr.getTransferDate(), tr.getReturnDate(), tr.getNotes())
                        && matchesFilter(field, filter,
                        new String[]{"ID", "ID заявки", "Дата передачі",
                                "Дата повернення", "Примітки"},
                        new Object[]{tr.getId(), tr.getRequestId(),
                                tr.getTransferDate(), tr.getReturnDate(),
                                tr.getNotes()}));
    }

    public void resetFilters() {
        searchField.clear();
        filterValueField.clear();
        filterFieldCombo.getSelectionModel().selectFirst();
        applyFilters();
    }

    private String normalize(String value) {
        return value == null
                ? ""
                : value.trim().toLowerCase(Locale.ROOT);
    }

    private boolean containsText(Object value, String text) {
        return value != null
                && String.valueOf(value).toLowerCase(Locale.ROOT)
                .contains(text);
    }

    private boolean matches(String text, Object... values) {
        for (Object value : values) {
            if (containsText(value, text)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesFilter(
            String selectedField,
            String filter,
            String[] fieldNames,
            Object[] values) {

        if (filter.isEmpty() || selectedField == null
                || selectedField.equals("Усі поля")) {
            return true;
        }

        for (int i = 0; i < fieldNames.length; i++) {
            if (fieldNames[i].equals(selectedField)) {
                return containsText(values[i], filter);
            }
        }

        return true;
    }
}