package service;

import dao.IssueRequest.IssueRequestDAO;
import dao.IssueRequest.IssueRequestDAOImpl;
import dao.Property.PropertyDAO;
import dao.Property.PropertyDAOImpl;
import dao.Transfer.TransferDAO;
import dao.Transfer.TransferDAOImpl;
import dao.User.UserDAO;
import dao.User.UserDAOImpl;
import model.IssueRequest;
import model.PropertyItem;
import model.Transfer;
import model.User;
import status.ItemState;
import status.RequestState;

import java.time.LocalDate;
import java.util.List;

public class IssueRequestService {

    private final IssueRequestDAO dao = new IssueRequestDAOImpl();
    private final PropertyDAO propertyDAO = new PropertyDAOImpl();
    private final TransferDAO transferDAO = new TransferDAOImpl();
    private final UserDAO userDAO = new UserDAOImpl();

    public List<IssueRequest> getAllRequests() {
        return dao.getAllRequests();
    }

    public IssueRequest getRequestById(int id) {
        return dao.getRequestById(id);
    }

    private void validateRequest(IssueRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Заявка не може бути порожньою");
        }

        validateFactor("F1 (терміновість)", request.getUrgencyF1());
        validateFactor("F2 (критичність)", request.getCriticalityF2());
        validateFactor("F3 (дефіцит)", request.getDeficitF3());

        User user = userDAO.getUserById(request.getUserId());

        if (user == null) {
            throw new IllegalArgumentException("Користувача не знайдено");
        }

        PropertyItem item = propertyDAO.getPropertyById(request.getPropertyId());

        if (item == null) {
            throw new IllegalArgumentException("Майно не знайдено");
        }

        if (item.getStatusItem() == ItemState.REPAIR) {
            throw new IllegalStateException(
                    "Майно перебуває на ремонті. Створення або зміна заявки неможливі."
            );
        }
    }

    private void validateFactor(String name, int value) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException(
                    name + " має бути в діапазоні від 0 до 100"
            );
        }
    }

    public void addRequest(IssueRequest request) {
        validateRequest(request);

        PropertyItem item = propertyDAO.getPropertyById(request.getPropertyId());

        if (item.getStatusItem() != ItemState.AVAILABLE
                || isPropertyPublishedInAnotherRequest(
                request.getPropertyId(), -1)) {
            throw new IllegalStateException(
                    "Майно недоступне або перебуває на ремонті. Створити заявку неможливо."
            );
        }

        request.setRequestStatus(RequestState.CREATED);

        request.setIntegralIndex(
                IssueRequest.calculateIntegralIndex(
                        request.getUrgencyF1(),
                        request.getCriticalityF2(),
                        request.getDeficitF3()
                )
        );

        dao.addRequest(request);
    }

    public void updateRequest(IssueRequest request) {
        validateRequest(request);

        IssueRequest oldRequest = dao.getRequestById(request.getId());

        if (oldRequest == null) {
            throw new IllegalArgumentException("Заявку не знайдено");
        }

        if (oldRequest.getPropertyId() != request.getPropertyId()) {
            throw new IllegalStateException(
                    "Не можна змінювати майно після створення заявки"
            );
        }

        if (oldRequest.getUserId() != request.getUserId()) {
            throw new IllegalStateException(
                    "Не можна змінювати користувача після створення заявки"
            );
        }

        RequestState oldStatus = oldRequest.getRequestStatus();
        RequestState newStatus = request.getRequestStatus();

        if (oldStatus != newStatus) {
            boolean allowed = switch (oldStatus) {
                case CREATED ->
                        newStatus == RequestState.APPROVED
                                || newStatus == RequestState.CANCELLED;
                case APPROVED ->
                        newStatus == RequestState.PUBLISHED
                                || newStatus == RequestState.CANCELLED;
                case PUBLISHED ->
                        newStatus == RequestState.RETURNED;
                case RETURNED, CANCELLED -> false;
            };

            if (!allowed) {
                throw new IllegalStateException(
                        "Недопустима зміна статусу з "
                                + oldStatus + " на " + newStatus
                );
            }
        }

        PropertyItem item = propertyDAO.getPropertyById(
                request.getPropertyId()
        );

        if (newStatus == RequestState.PUBLISHED
                && oldStatus != RequestState.PUBLISHED) {

            if (item.getStatusItem() != ItemState.AVAILABLE
                    || isPropertyPublishedInAnotherRequest(
                    request.getPropertyId(), request.getId())) {
                throw new IllegalStateException(
                        "Майно недоступне або перебуває на ремонті"
                );
            }
        }

        request.setIntegralIndex(
                IssueRequest.calculateIntegralIndex(
                        request.getUrgencyF1(),
                        request.getCriticalityF2(),
                        request.getDeficitF3()
                )
        );

        dao.updateRequest(request);

        List<Transfer> transfers = transferDAO.getAllTransfers();

        if (newStatus == RequestState.APPROVED
                && oldStatus != RequestState.APPROVED) {

            boolean exists = transfers.stream()
                    .anyMatch(t -> t.getRequestId() == request.getId());

            if (!exists) {
                transferDAO.addTransfer(
                        new Transfer(
                                0,
                                request.getId(),
                                (LocalDate) null,
                                (LocalDate) null,
                                ""
                        )
                );
            }
        }

        if (newStatus == RequestState.PUBLISHED
                && oldStatus != RequestState.PUBLISHED) {

            Transfer transfer = transfers.stream()
                    .filter(t -> t.getRequestId() == request.getId()
                            && t.getReturnDate() == null)
                    .findFirst()
                    .orElse(null);

            if (transfer == null) {
                transferDAO.addTransfer(
                        new Transfer(
                                0,
                                request.getId(),
                                LocalDate.now(),
                                null,
                                ""
                        )
                );
            } else {
                transfer.setTransferDate(LocalDate.now());
                transferDAO.updateTransfer(transfer);
            }

            item.setStatusItem(ItemState.UNAVAILABLE);
            propertyDAO.updateProperty(item);
        }

        if (newStatus == RequestState.RETURNED
                && oldStatus != RequestState.RETURNED) {

            Transfer transfer = transfers.stream()
                    .filter(t -> t.getRequestId() == request.getId()
                            && t.getTransferDate() != null
                            && t.getReturnDate() == null)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "Активний запис передачі не знайдено"
                    ));

            transfer.setReturnDate(LocalDate.now());
            transferDAO.updateTransfer(transfer);

            item.setStatusItem(ItemState.AVAILABLE);
            propertyDAO.updateProperty(item);
        }
    }
    public void deleteRequest(int id) {dao.deleteRequest(id);}

    public boolean isPropertyPublishedInAnotherRequest(
            int propertyId, int currentRequestId) {

        return dao.getAllRequests().stream()
                .anyMatch(request ->
                        request.getPropertyId() == propertyId
                                && request.getId() != currentRequestId
                                && request.getRequestStatus()
                                == RequestState.PUBLISHED);
    }
}