package Test;

import dao.IssueRequest.IssueRequestDAO;
import dao.Property.PropertyDAO;
import dao.Transfer.TransferDAO;
import dao.User.UserDAO;

import model.IssueRequest;
import model.PropertyItem;
import model.Transfer;
import model.User;

import service.IssueRequestService;

import status.ItemState;
import status.RequestState;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpUniverPropertyTest {

    private IssueRequestService service;

    private TestIssueRequestDAO requestDAO;
    private TestPropertyDAO propertyDAO;
    private TestTransferDAO transferDAO;
    private TestUserDAO userDAO;

    @BeforeEach
    void setUp() {
        requestDAO = new TestIssueRequestDAO();
        propertyDAO = new TestPropertyDAO();
        transferDAO = new TestTransferDAO();
        userDAO = new TestUserDAO();

        service = new IssueRequestService(
                requestDAO,
                propertyDAO,
                transferDAO,
                userDAO
        );

        userDAO.addUser(new User(
                1, "Test User", "STUDENT", "test@example.com"
        ));

        propertyDAO.addProperty(new PropertyItem(
                1, "TEST-001", "Test Equipment",
                "Equipment", ItemState.AVAILABLE
        ));

        System.out.println("[SETUP] Test environment initialized.");
        System.out.println("[SETUP] User ID: 1, Property ID: 1");
        System.out.println("[SETUP] Property status: AVAILABLE");
    }

    private IssueRequest createRequest() {
        return new IssueRequest(
                1, 1, RequestState.CREATED,
                80, 70, 60
        );
    }

    // 1. Test integral index calculation

    @Test
    void testCalculateIntegralIndex() {
        System.out.println("\n[TEST 1] Integral index calculation.");
        System.out.println("[INPUT] F1=80, F2=70, F3=60");
        System.out.println("[FORMULA] 0.40*80 + 0.35*70 + 0.25*60");
        System.out.println("[EXPECTED] Integral index = 71.5");

        double result = IssueRequest.calculateIntegralIndex(
                80, 70, 60
        );

        assertEquals(71.5, result, 0.001);

        System.out.println("[SUCCESS] Actual integral index: " + result);
    }

    // 2. Test minimum factor values

    @Test
    void testMinimumFactorValues() {
        System.out.println("\n[TEST 2] Minimum factor values.");
        System.out.println("[INPUT] F1=0, F2=0, F3=0");
        System.out.println("[EXPECTED] Integral index = 0.0");
        System.out.println("[EXPECTED] Request count = 1");

        IssueRequest request = new IssueRequest(
                1, 1, RequestState.CREATED,
                0, 0, 0
        );

        service.addRequest(request);

        assertEquals(0.0, request.getIntegralIndex(), 0.001);
        assertEquals(1, requestDAO.getAllRequests().size());

        System.out.println("[SUCCESS] Actual integral index: "
                + request.getIntegralIndex());
        System.out.println("[SUCCESS] Actual request count: "
                + requestDAO.getAllRequests().size());
    }

    // 3. Test maximum factor values

    @Test
    void testMaximumFactorValues() {
        System.out.println("\n[TEST 3] Maximum factor values.");
        System.out.println("[INPUT] F1=100, F2=100, F3=100");
        System.out.println("[EXPECTED] Integral index = 100.0");
        System.out.println("[EXPECTED] Request count = 1");

        IssueRequest request = new IssueRequest(
                1, 1, RequestState.CREATED,
                100, 100, 100
        );

        service.addRequest(request);

        assertEquals(100.0, request.getIntegralIndex(), 0.001);
        assertEquals(1, requestDAO.getAllRequests().size());

        System.out.println("[SUCCESS] Actual integral index: "
                + request.getIntegralIndex());
        System.out.println("[SUCCESS] Actual request count: "
                + requestDAO.getAllRequests().size());
    }

    // 4. Test negative factor rejection

    @Test
    void testNegativeFactorRejected() {
        System.out.println("\n[TEST 4] Negative factor rejection.");
        System.out.println("[INPUT] F1=-1, F2=70, F3=60");
        System.out.println("[EXPECTED] IllegalArgumentException");

        IssueRequest request = createRequest();
        request.setUrgencyF1(-1);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addRequest(request)
        );

        System.out.println("[SUCCESS] Negative factor -1 rejected.");
    }

    // 5. Test factor above maximum rejection

    @Test
    void testFactorAboveMaximumRejected() {
        System.out.println("\n[TEST 5] Factor above maximum.");
        System.out.println("[INPUT] F1=80, F2=101, F3=60");
        System.out.println("[EXPECTED] IllegalArgumentException");

        IssueRequest request = createRequest();
        request.setCriticalityF2(101);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addRequest(request)
        );

        System.out.println("[SUCCESS] Factor value 101 rejected.");
    }

    // 6. Test CREATED -> APPROVED transition

    @Test
    void testCreatedToApproved() {
        System.out.println("\n[TEST 6] CREATED -> APPROVED.");
        System.out.println("[INPUT] Initial status: CREATED");
        System.out.println("[ACTION] Change status to APPROVED");
        System.out.println("[EXPECTED] Status: APPROVED");
        System.out.println("[EXPECTED] Transfer record count: 1");
        System.out.println("[EXPECTED] Transfer date: null");
        System.out.println("[EXPECTED] Return date: null");

        IssueRequest request = createRequest();
        service.addRequest(request);

        request.setRequestStatus(RequestState.APPROVED);
        service.updateRequest(request);

        assertEquals(
                RequestState.APPROVED,
                service.getRequestById(request.getId()).getRequestStatus()
        );

        assertEquals(1, transferDAO.getAllTransfers().size());

        Transfer transfer = transferDAO.getAllTransfers().get(0);

        assertNull(transfer.getTransferDate());
        assertNull(transfer.getReturnDate());

        System.out.println("[SUCCESS] New status: APPROVED");
        System.out.println("[SUCCESS] Transfer record created.");
        System.out.println("[SUCCESS] Transfer date: null");
        System.out.println("[SUCCESS] Return date: null");
    }

    // 7. Test APPROVED -> PUBLISHED transition

    @Test
    void testApprovedToPublished() {
        System.out.println("\n[TEST 7] APPROVED -> PUBLISHED.");
        System.out.println("[INPUT] Initial status: CREATED");
        System.out.println("[ACTION] CREATED -> APPROVED -> PUBLISHED");
        System.out.println("[EXPECTED] Status: PUBLISHED");
        System.out.println("[EXPECTED] Transfer date: current date");
        System.out.println("[EXPECTED] Return date: null");
        System.out.println("[EXPECTED] Property status: UNAVAILABLE");

        IssueRequest request = createRequest();
        service.addRequest(request);

        request.setRequestStatus(RequestState.APPROVED);
        service.updateRequest(request);

        request.setRequestStatus(RequestState.PUBLISHED);
        service.updateRequest(request);

        Transfer transfer = transferDAO.getAllTransfers().get(0);
        PropertyItem item = propertyDAO.getPropertyById(1);

        assertEquals(
                RequestState.PUBLISHED,
                service.getRequestById(request.getId()).getRequestStatus()
        );

        assertNotNull(transfer.getTransferDate());
        assertNull(transfer.getReturnDate());

        assertEquals(ItemState.UNAVAILABLE, item.getStatusItem());

        System.out.println("[SUCCESS] New status: PUBLISHED");
        System.out.println("[SUCCESS] Transfer date: "
                + transfer.getTransferDate());
        System.out.println("[SUCCESS] Return date: null");
        System.out.println("[SUCCESS] Property status: UNAVAILABLE");
    }

    // 8. Test PUBLISHED -> RETURNED transition

    @Test
    void testPublishedToReturned() {
        System.out.println("\n[TEST 8] PUBLISHED -> RETURNED.");
        System.out.println("[INPUT] Initial status: CREATED");
        System.out.println("[ACTION] CREATED -> APPROVED -> PUBLISHED -> RETURNED");
        System.out.println("[EXPECTED] Status: RETURNED");
        System.out.println("[EXPECTED] Transfer date: current date");
        System.out.println("[EXPECTED] Return date: current date");
        System.out.println("[EXPECTED] Property status: AVAILABLE");

        IssueRequest request = createRequest();
        service.addRequest(request);

        request.setRequestStatus(RequestState.APPROVED);
        service.updateRequest(request);

        request.setRequestStatus(RequestState.PUBLISHED);
        service.updateRequest(request);

        request.setRequestStatus(RequestState.RETURNED);
        service.updateRequest(request);

        Transfer transfer = transferDAO.getAllTransfers().get(0);
        PropertyItem item = propertyDAO.getPropertyById(1);

        assertEquals(
                RequestState.RETURNED,
                service.getRequestById(request.getId()).getRequestStatus()
        );

        assertNotNull(transfer.getTransferDate());
        assertNotNull(transfer.getReturnDate());

        assertEquals(ItemState.AVAILABLE, item.getStatusItem());

        System.out.println("[SUCCESS] New status: RETURNED");
        System.out.println("[SUCCESS] Transfer date: "
                + transfer.getTransferDate());
        System.out.println("[SUCCESS] Return date: "
                + transfer.getReturnDate());
        System.out.println("[SUCCESS] Property status: AVAILABLE");
    }

    // 9. Test forbidden CREATED -> PUBLISHED transition

    @Test
    void testCreatedToPublishedRejected() {
        System.out.println("\n[TEST 9] Forbidden CREATED -> PUBLISHED.");
        System.out.println("[INPUT] Initial status: CREATED");
        System.out.println("[ACTION] Attempt direct transition to PUBLISHED");
        System.out.println("[EXPECTED] IllegalStateException");
        System.out.println("[EXPECTED] Final status: CREATED");

        IssueRequest request = createRequest();
        service.addRequest(request);

        request.setRequestStatus(RequestState.PUBLISHED);

        assertThrows(
                IllegalStateException.class,
                () -> service.updateRequest(request)
        );

        assertEquals(
                RequestState.CREATED,
                service.getRequestById(request.getId()).getRequestStatus()
        );

        System.out.println("[SUCCESS] Forbidden transition rejected.");
        System.out.println("[SUCCESS] Final status remains: CREATED");
    }

    // 10. Test forbidden RETURNED -> APPROVED transition

    @Test
    void testReturnedToApprovedRejected() {
        System.out.println("\n[TEST 10] Forbidden RETURNED -> APPROVED.");
        System.out.println("[INPUT] Initial status: CREATED");
        System.out.println("[ACTION] CREATED -> APPROVED -> PUBLISHED -> RETURNED");
        System.out.println("[ACTION] Attempt transition to APPROVED");
        System.out.println("[EXPECTED] IllegalStateException");
        System.out.println("[EXPECTED] Final status: RETURNED");

        IssueRequest request = createRequest();
        service.addRequest(request);

        request.setRequestStatus(RequestState.APPROVED);
        service.updateRequest(request);

        request.setRequestStatus(RequestState.PUBLISHED);
        service.updateRequest(request);

        request.setRequestStatus(RequestState.RETURNED);
        service.updateRequest(request);

        request.setRequestStatus(RequestState.APPROVED);

        assertThrows(
                IllegalStateException.class,
                () -> service.updateRequest(request)
        );

        assertEquals(
                RequestState.RETURNED,
                service.getRequestById(request.getId()).getRequestStatus()
        );

        System.out.println("[SUCCESS] Forbidden transition rejected.");
        System.out.println("[SUCCESS] Final status remains: RETURNED");
    }

    // 11. Test null request rejection

    @Test
    void testNullRequestRejected() {
        System.out.println("\n[TEST 11] Null request rejection.");
        System.out.println("[INPUT] Request = null");
        System.out.println("[EXPECTED] IllegalArgumentException");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addRequest(null)
        );

        System.out.println("[SUCCESS] Null request rejected.");
    }

    // 12. Test missing user and property rejection

    @Test
    void testMissingUserAndPropertyRejected() {
        System.out.println("\n[TEST 12] Missing user and property.");
        System.out.println("[INPUT] Existing user ID: 1");
        System.out.println("[INPUT] Existing property ID: 1");
        System.out.println("[INPUT] Missing user ID: 999");
        System.out.println("[INPUT] Missing property ID: 999");
        System.out.println("[EXPECTED] IllegalArgumentException");

        IssueRequest requestWithMissingUser = createRequest();
        requestWithMissingUser.setUserId(999);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addRequest(requestWithMissingUser)
        );

        System.out.println("[SUCCESS] Missing user ID 999 rejected.");

        IssueRequest requestWithMissingProperty = createRequest();
        requestWithMissingProperty.setPropertyId(999);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addRequest(requestWithMissingProperty)
        );

        System.out.println("[SUCCESS] Missing property ID 999 rejected.");
    }

    // In-memory DAO implementations

    private static class TestIssueRequestDAO implements IssueRequestDAO {

        private final List<IssueRequest> requests = new ArrayList<>();
        private int nextId = 1;

        // Creates an independent copy of a request

        private IssueRequest copyRequest(IssueRequest request) {
            IssueRequest copy = new IssueRequest(
                    request.getUserId(),
                    request.getPropertyId(),
                    request.getRequestStatus(),
                    request.getUrgencyF1(),
                    request.getCriticalityF2(),
                    request.getDeficitF3()
            );

            copy.setId(request.getId());
            copy.setIntegralIndex(request.getIntegralIndex());

            return copy;
        }

        public List<IssueRequest> getAllRequests() {
            List<IssueRequest> result = new ArrayList<>();

            for (IssueRequest request : requests) {
                result.add(copyRequest(request));
            }

            return result;
        }

        public IssueRequest getRequestById(int id) {
            return requests.stream()
                    .filter(r -> r.getId() == id)
                    .findFirst()
                    .map(this::copyRequest)
                    .orElse(null);
        }

        public void addRequest(IssueRequest request) {
            request.setId(nextId++);
            requests.add(copyRequest(request));
        }

        public void updateRequest(IssueRequest request) {
            for (int i = 0; i < requests.size(); i++) {
                if (requests.get(i).getId() == request.getId()) {
                    requests.set(i, copyRequest(request));
                    return;
                }
            }
        }

        public void deleteRequest(int id) {
            requests.removeIf(r -> r.getId() == id);
        }
    }

    private static class TestPropertyDAO implements PropertyDAO {

        private final List<PropertyItem> properties = new ArrayList<>();

        public List<PropertyItem> getAllProperties() {
            return properties;
        }

        public PropertyItem getPropertyById(int id) {
            return properties.stream()
                    .filter(p -> p.getId() == id)
                    .findFirst()
                    .orElse(null);
        }

        public void addProperty(PropertyItem property) {
            properties.add(property);
        }

        public void updateProperty(PropertyItem property) {
            for (int i = 0; i < properties.size(); i++) {
                if (properties.get(i).getId() == property.getId()) {
                    properties.set(i, property);
                    return;
                }
            }
        }

        public void deleteProperty(int id) {
            properties.removeIf(p -> p.getId() == id);
        }
    }

    private static class TestTransferDAO implements TransferDAO {

        private final List<Transfer> transfers = new ArrayList<>();
        private int nextId = 1;

        public List<Transfer> getAllTransfers() {
            return transfers;
        }

        public Transfer getTransferById(int id) {
            return transfers.stream()
                    .filter(t -> t.getId() == id)
                    .findFirst()
                    .orElse(null);
        }

        public void addTransfer(Transfer transfer) {
            transfer.setId(nextId++);
            transfers.add(transfer);
        }

        public void updateTransfer(Transfer transfer) {
            for (int i = 0; i < transfers.size(); i++) {
                if (transfers.get(i).getId() == transfer.getId()) {
                    transfers.set(i, transfer);
                    return;
                }
            }
        }

        public void deleteTransfer(int id) {
            transfers.removeIf(t -> t.getId() == id);
        }
    }

    private static class TestUserDAO implements UserDAO {

        private final List<User> users = new ArrayList<>();

        public List<User> getAllUsers() {
            return users;
        }

        public User getUserById(int id) {
            return users.stream()
                    .filter(u -> u.getId() == id)
                    .findFirst()
                    .orElse(null);
        }

        public void addUser(User user) {
            users.add(user);
        }

        public void updateUser(User user) {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getId() == user.getId()) {
                    users.set(i, user);
                    return;
                }
            }
        }

        public void deleteUser(int id) {
            users.removeIf(u -> u.getId() == id);
        }
    }
}
