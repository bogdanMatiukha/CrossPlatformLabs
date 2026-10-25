package service;

import dao.IssueRequest.IssueRequestDAO;
import dao.IssueRequest.IssueRequestDAOImpl;
import dao.Property.PropertyDAO;
import dao.Property.PropertyDAOImpl;
import model.IssueRequest;
import model.PropertyItem;
import status.ItemState;
import status.RequestState;
import java.util.List;

public class IssueRequestService {
    private final IssueRequestDAO dao = new IssueRequestDAOImpl();
    private final PropertyDAO propertyDAO = new PropertyDAOImpl();

    public List<IssueRequest> getAllRequests() {return dao.getAllRequests();}
    public IssueRequest getRequestById(int id) {return dao.getRequestById(id);}
    public void addRequest(IssueRequest request) {
        PropertyItem item = propertyDAO.getPropertyById(request.getPropertyId());

        if (item == null) {
            throw new IllegalArgumentException("Майно не знайдено");
        }

        if (item.getStatusItem() == ItemState.UNAVAILABLE
                || isPropertyPublishedInAnotherRequest(request.getPropertyId(), -1)) {
            throw new IllegalStateException("Майно наразі недоступне. Створити заявку неможливо.");
        }

        request.setRequestStatus(RequestState.CREATED);
        dao.addRequest(request);
    }
    public void updateRequest(IssueRequest request) {
        IssueRequest oldRequest = dao.getRequestById(request.getId());

        if (oldRequest == null) {
            throw new IllegalArgumentException("Заявку не знайдено");
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
        dao.updateRequest(request);
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