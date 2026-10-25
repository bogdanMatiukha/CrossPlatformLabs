package service;

import dao.Property.PropertyDAO;
import dao.Property.PropertyDAOImpl;
import model.PropertyItem;
import status.RequestState;
import status.ItemState;

import java.util.List;

public class PropertyService {
    private final PropertyDAO dao = new PropertyDAOImpl();
    private final IssueRequestService requestService = new IssueRequestService();

    public List<PropertyItem> getAllProperties() {
        return dao.getAllProperties();
    }

    public PropertyItem getPropertyById(int id) {
        return dao.getPropertyById(id);
    }

    public void addProperty(PropertyItem property) {
        dao.addProperty(property);
    }

    public void updateProperty(PropertyItem property) {
        PropertyItem oldProperty = dao.getPropertyById(property.getId());

        if (oldProperty == null) {
            throw new IllegalArgumentException("Майно не знайдено");
        }

        boolean hasPublishedRequest = requestService.getAllRequests().stream()
                .anyMatch(request ->
                        request.getPropertyId() == property.getId()
                                && request.getRequestStatus() == RequestState.PUBLISHED
                );

        if (hasPublishedRequest && property.getStatusItem() != ItemState.UNAVAILABLE) {
            throw new IllegalStateException(
                    "Майно видане за опублікованою заявкою. Зміну статусу заборонено."
            );
        }

        dao.updateProperty(property);
    }

    public void deleteProperty(int id) {
        dao.deleteProperty(id);
    }
}