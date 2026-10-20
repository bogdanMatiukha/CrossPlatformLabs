package main.HttpRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import main.model.IssueRequest;
import main.model.PropertyItem;
import main.model.TransferRecord;
import main.model.User;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class HttpUniverPropertyClient {

    private static final String BASE_URL = "http://localhost:8080/university_property";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    // =========================================================================
    // 1. Users

    public List<User> getAllUsers() {
        String url = BASE_URL + "/users?format=json";
        HttpResponse<String> response = send(getRequest(url));
        checkStatus(response);
        try {
            User[] users = mapper.readValue(response.body(), User[].class);
            return Arrays.asList(users);
        } catch (Exception e) {
            throw new RuntimeException("Некоректна JSON-відповідь сервера (Users)", e);
        }
    }

    public void addUser(User user) {
        String form = "action=insert"
                + "&format=json"
                + "&fullName=" + enc(user.getFullName())
                + "&role=" + enc(user.getRole())
                + "&email=" + enc(user.getEmail());
        checkStatus(send(postRequest(BASE_URL + "/users", form)));
    }

    public void updateUser(User user) {
        String form = "action=update"
                + "&format=json"
                + "&id=" + user.getId()
                + "&fullName=" + enc(user.getFullName())
                + "&role=" + enc(user.getRole())
                + "&email=" + enc(user.getEmail());
        checkStatus(send(postRequest(BASE_URL + "/users", form)));
    }

    public void deleteUser(int id) {
        String url = BASE_URL + "/users?action=delete&format=json&id=" + id;
        checkStatus(send(getRequest(url)));
    }

    // =========================================================================
    // 2. Property Items

    public List<PropertyItem> getAllProperties() {
        String url = BASE_URL + "/properties?format=json";
        HttpResponse<String> response = send(getRequest(url));
        checkStatus(response);
        try {
            PropertyItem[] items = mapper.readValue(response.body(), PropertyItem[].class);
            return Arrays.asList(items);
        } catch (Exception e) {
            throw new RuntimeException("Некоректна JSON-відповідь сервера (Properties)", e);
        }
    }

    public void addProperty(PropertyItem property) {
        String form = "action=insert"
                + "&format=json"
                + "&inventoryNumber=" + enc(property.getInventoryNumber())
                + "&name=" + enc(property.getName())
                + "&category=" + enc(property.getCategory())
                + "&statusItem=" + enc(property.getStatusItem() != null ? property.getStatusItem().name() : "AVAILABLE");
        checkStatus(send(postRequest(BASE_URL + "/properties", form)));
    }

    public void updateProperty(PropertyItem property) {
        String form = "action=update"
                + "&format=json"
                + "&id=" + property.getId()
                + "&inventoryNumber=" + enc(property.getInventoryNumber())
                + "&name=" + enc(property.getName())
                + "&category=" + enc(property.getCategory())
                + "&statusItem=" + enc(property.getStatusItem() != null ? property.getStatusItem().name() : "AVAILABLE");
        checkStatus(send(postRequest(BASE_URL + "/properties", form)));
    }

    public void deleteProperty(int id) {
        String url = BASE_URL + "/properties?action=delete&format=json&id=" + id;
        checkStatus(send(getRequest(url)));
    }

    // =========================================================================
    // 3. Issue Request

    public List<IssueRequest> getAllRequests() {
        String url = BASE_URL + "/requests?format=json";
        HttpResponse<String> response = send(getRequest(url));
        checkStatus(response);
        try {
            IssueRequest[] requests = mapper.readValue(response.body(), IssueRequest[].class);
            return Arrays.asList(requests);
        } catch (Exception e) {
            throw new RuntimeException("Некоректна JSON-відповідь сервера (Requests)", e);
        }
    }

    public void addRequest(IssueRequest req) {
        String form = "action=insert"
                + "&format=json"
                + "&userId=" + req.getUserId()
                + "&propertyId=" + req.getPropertyId()
                + "&requestStatus=" + enc(req.getRequestStatus() != null ? req.getRequestStatus().name() : "CREATED")
                + "&urgencyF1=" + req.getUrgencyF1()
                + "&criticalityF2=" + req.getCriticalityF2()
                + "&deficitF3=" + req.getDeficitF3();
        checkStatus(send(postRequest(BASE_URL + "/requests", form)));
    }

    public void updateRequest(IssueRequest req) {
        String form = "action=update"
                + "&format=json"
                + "&id=" + req.getId()
                + "&userId=" + req.getUserId()
                + "&propertyId=" + req.getPropertyId()
                + "&requestStatus=" + enc(req.getRequestStatus() != null ? req.getRequestStatus().name() : "CREATED")
                + "&urgencyF1=" + req.getUrgencyF1()
                + "&criticalityF2=" + req.getCriticalityF2()
                + "&deficitF3=" + req.getDeficitF3();
        checkStatus(send(postRequest(BASE_URL + "/requests", form)));
    }

    public void deleteRequest(int id) {
        String url = BASE_URL + "/requests?action=delete&format=json&id=" + id;
        checkStatus(send(getRequest(url)));
    }

    // =========================================================================
    // 4. Transfers

    public List<TransferRecord> getAllTransfers() {
        String url = BASE_URL + "/transfers?format=json";
        HttpResponse<String> response = send(getRequest(url));
        checkStatus(response);
        try {
            TransferRecord[] transfers = mapper.readValue(response.body(), TransferRecord[].class);
            return Arrays.asList(transfers);
        } catch (Exception e) {
            throw new RuntimeException("Некоректна JSON-відповідь сервера (Transfers)", e);
        }
    }

    public void addTransfer(TransferRecord transfer) {
        String form = "action=insert"
                + "&format=json"
                + "&requestId=" + transfer.getRequestId()
                + "&transferDate=" + enc(transfer.getTransferDate() != null ? transfer.getTransferDate().toString() : "")
                + "&returnDate=" + enc(transfer.getReturnDate() != null ? transfer.getReturnDate().toString() : "")
                + "&notes=" + enc(transfer.getNotes());
        checkStatus(send(postRequest(BASE_URL + "/transfers", form)));
    }

    public void updateTransfer(TransferRecord transfer) {
        String form = "action=update"
                + "&format=json"
                + "&id=" + transfer.getId()
                + "&requestId=" + transfer.getRequestId()
                + "&transferDate=" + enc(transfer.getTransferDate() != null ? transfer.getTransferDate().toString() : "")
                + "&returnDate=" + enc(transfer.getReturnDate() != null ? transfer.getReturnDate().toString() : "")
                + "&notes=" + enc(transfer.getNotes());
        checkStatus(send(postRequest(BASE_URL + "/transfers", form)));
    }

    public void deleteTransfer(int id) {
        String url = BASE_URL + "/transfers?action=delete&format=json&id=" + id;
        checkStatus(send(getRequest(url)));
    }

    private HttpRequest getRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
    }

    private HttpRequest postRequest(String url, String form) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();
    }

    private HttpResponse<String> send(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Не вдалося звернутися до сервера", e);
        }
    }

    private void checkStatus(HttpResponse<String> response) {
        if (response.statusCode() >= 400) {
            throw new RuntimeException("HTTP " + response.statusCode() + ": " + response.body());
        }
    }

    private String enc(String value) {
        if (value == null) return "";
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}