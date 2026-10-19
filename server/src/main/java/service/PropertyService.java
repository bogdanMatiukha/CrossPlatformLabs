package service;

import dao.Property.PropertyDAO;
import dao.Property.PropertyDAOImpl;
import model.PropertyItem;
import java.util.List;

public class PropertyService {
    private final PropertyDAO dao = new PropertyDAOImpl();

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
        dao.updateProperty(property);
    }

    public void deleteProperty(int id) {
        dao.deleteProperty(id);
    }
}