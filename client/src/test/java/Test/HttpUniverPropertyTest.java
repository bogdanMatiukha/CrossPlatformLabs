package Test;

import main.HttpRequest.HttpUniverPropertyClient;
import main.model.IssueRequest;
import main.model.PropertyItem;
import main.model.Transfer;
import main.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HttpUniverPropertyTest {

    private HttpUniverPropertyClient client;

    private static int userId;
    private static int propertyId;
    private static int requestId;

    @BeforeEach
    void setUp() {
        client = new HttpUniverPropertyClient();
        System.out.println("[SETUP] HTTP client initialized.");
    }

    private void logUser(User user) {
        System.out.println("  ID: " + user.getId());
        System.out.println("  Full Name: " + user.getFullName());
        System.out.println("  Role: " + user.getRole());
        System.out.println("  Email: " + user.getEmail());
    }

    private void logProperty(PropertyItem property) {
        System.out.println("  ID: " + property.getId());
        System.out.println("  Inventory Number: " + property.getInventoryNumber());
        System.out.println("  Name: " + property.getName());
        System.out.println("  Category: " + property.getCategory());
        System.out.println("  Status: " + property.getStatusItem());
    }

    private void logRequest(IssueRequest request) {
        System.out.println("  ID: " + request.getId());
        System.out.println("  User ID: " + request.getUserId());
        System.out.println("  Property ID: " + request.getPropertyId());
        System.out.println("  Urgency (F1): " + request.getUrgencyF1());
        System.out.println("  Criticality (F2): " + request.getCriticalityF2());
        System.out.println("  Deficit (F3): " + request.getDeficitF3());
    }

    // GET: 4 tests

    @Test
    @Order(1)
    void testGetAllUsers() {
        System.out.println("[TEST] Getting all users.");

        List<User> users = client.getAllUsers();

        assertNotNull(users);
        System.out.println("[SUCCESS] Retrieved " + users.size() + " users.");
    }

    @Test
    @Order(2)
    void testGetAllProperties() {
        System.out.println("[TEST] Getting all properties.");

        List<PropertyItem> properties = client.getAllProperties();

        assertNotNull(properties);
        System.out.println("[SUCCESS] Retrieved " + properties.size() + " properties.");
    }

    @Test
    @Order(3)
    void testGetAllRequests() {
        System.out.println("[TEST] Getting all requests.");

        List<IssueRequest> requests = client.getAllRequests();

        assertNotNull(requests);
        System.out.println("[SUCCESS] Retrieved " + requests.size() + " requests.");
    }

    @Test
    @Order(4)
    void testGetAllTransfers() {
        System.out.println("[TEST] Getting all transfers.");

        List<Transfer> transfers = client.getAllTransfers();

        assertNotNull(transfers);
        System.out.println("[SUCCESS] Retrieved " + transfers.size() + " transfers.");
    }

    // ADD: 3 tests

    @Test
    @Order(5)
    void testAddUser() {
        System.out.println("\n========== ADD USER ==========");

        User user = new User();
        user.setFullName("JUnit Test User");
        user.setRole("STUDENT");
        user.setEmail("junit_test_user@example.com");

        client.addUser(user);

        User addedUser = client.getAllUsers().stream()
                .filter(u -> "junit_test_user@example.com".equals(u.getEmail()))
                .findFirst()
                .orElseThrow();

        userId = addedUser.getId();

        System.out.println("Created user:");
        logUser(addedUser);

        assertTrue(userId > 0);
        System.out.println("[SUCCESS] User created.\n");
    }

    @Test
    @Order(6)
    void testAddProperty() {
        System.out.println("\n========== ADD PROPERTY ==========");

        PropertyItem property = new PropertyItem();
        property.setInventoryNumber("JUNIT-TEST-001");
        property.setName("JUnit Test Property");
        property.setCategory("Equipment");

        client.addProperty(property);

        PropertyItem addedProperty = client.getAllProperties().stream()
                .filter(p -> "JUNIT-TEST-001".equals(p.getInventoryNumber()))
                .findFirst()
                .orElseThrow();

        propertyId = addedProperty.getId();

        System.out.println("Created property:");
        logProperty(addedProperty);

        assertTrue(propertyId > 0);
        System.out.println("[SUCCESS] Property created.\n");
    }

    @Test
    @Order(7)
    void testAddRequest() {
        System.out.println("[TEST] Adding an issue request.");

        IssueRequest request = new IssueRequest();
        request.setUserId(1);
        request.setPropertyId(1);
        request.setUrgencyF1(3);
        request.setCriticalityF2(3);
        request.setDeficitF3(3);

        List<IssueRequest> before = client.getAllRequests();

        client.addRequest(request);

        IssueRequest addedRequest = client.getAllRequests().stream()
                .filter(r -> !before.stream()
                        .anyMatch(old -> old.getId() == r.getId()))
                .filter(r -> r.getUserId() == 1
                        && r.getPropertyId() == 1)
                .findFirst()
                .orElseThrow();

        requestId = addedRequest.getId();

        assertTrue(requestId > 0);
        System.out.println("[SUCCESS] Issue request created with ID: " + requestId);
    }

    // UPDATE: 3 tests

    @Test
    @Order(8)
    void testUpdateUser() {
        System.out.println("\n========== UPDATE USER ==========");

        assertTrue(userId > 0);

        User user = client.getAllUsers().stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElseThrow();

        System.out.println("Before update:");
        logUser(user);

        user.setFullName("JUnit Updated User");
        client.updateUser(user);

        User updatedUser = client.getAllUsers().stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElseThrow();

        System.out.println("After update:");
        logUser(updatedUser);

        assertEquals("JUnit Updated User", updatedUser.getFullName());
        System.out.println("[SUCCESS] User updated.\n");
    }

    @Test
    @Order(9)
    void testUpdateProperty() {
        System.out.println("\n========== UPDATE PROPERTY ==========");

        assertTrue(propertyId > 0);

        PropertyItem property = client.getAllProperties().stream()
                .filter(p -> p.getId() == propertyId)
                .findFirst()
                .orElseThrow();

        System.out.println("Before update:");
        logProperty(property);

        property.setName("JUnit Updated Property");
        client.updateProperty(property);

        PropertyItem updatedProperty = client.getAllProperties().stream()
                .filter(p -> p.getId() == propertyId)
                .findFirst()
                .orElseThrow();

        System.out.println("After update:");
        logProperty(updatedProperty);

        assertEquals("JUnit Updated Property", updatedProperty.getName());
        System.out.println("[SUCCESS] Property updated.\n");
    }

    @Test
    @Order(10)
    void testUpdateRequest() {
        System.out.println("\n========== UPDATE REQUEST ==========");

        assertTrue(requestId > 0);

        IssueRequest request = client.getAllRequests().stream()
                .filter(r -> r.getId() == requestId)
                .findFirst()
                .orElseThrow();

        System.out.println("Before update:");
        logRequest(request);

        request.setUrgencyF1(2);
        client.updateRequest(request);

        IssueRequest updatedRequest = client.getAllRequests().stream()
                .filter(r -> r.getId() == requestId)
                .findFirst()
                .orElseThrow();

        System.out.println("After update:");
        logRequest(updatedRequest);

        assertEquals(2, updatedRequest.getUrgencyF1());
        System.out.println("[SUCCESS] Request updated.\n");
    }

    // DELETE: 3 tests

    @Test
    @Order(11)
    void testDeleteUser() {
        System.out.println("\n========== DELETE USER ==========");

        User user = client.getAllUsers().stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElseThrow();

        System.out.println("Deleting user:");
        logUser(user);

        client.deleteUser(userId);

        assertFalse(client.getAllUsers().stream()
                .anyMatch(u -> u.getId() == userId));

        System.out.println("[SUCCESS] User deleted.\n");
    }

    @Test
    @Order(12)
    void testDeleteProperty() {
        System.out.println("\n========== DELETE PROPERTY ==========");

        PropertyItem property = client.getAllProperties().stream()
                .filter(p -> p.getId() == propertyId)
                .findFirst()
                .orElseThrow();

        System.out.println("Deleting property:");
        logProperty(property);

        client.deleteProperty(propertyId);

        assertFalse(client.getAllProperties().stream()
                .anyMatch(p -> p.getId() == propertyId));

        System.out.println("[SUCCESS] Property deleted.\n");
    }

    @Test
    @Order(13)
    void testDeleteRequest() {
        System.out.println("\n========== DELETE REQUEST ==========");

        IssueRequest request = client.getAllRequests().stream()
                .filter(r -> r.getId() == requestId)
                .findFirst()
                .orElseThrow();

        System.out.println("Deleting request:");
        logRequest(request);

        client.deleteRequest(requestId);

        assertFalse(client.getAllRequests().stream()
                .anyMatch(r -> r.getId() == requestId));

        System.out.println("[SUCCESS] Request deleted.\n");
    }
}