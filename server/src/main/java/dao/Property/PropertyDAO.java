package dao.Property;

import model.PropertyItem;
import java.util.List;

public interface PropertyDAO {
    List<PropertyItem> getAllProperties();
    PropertyItem getPropertyById(int id);
    void addProperty(PropertyItem property);
    void updateProperty(PropertyItem property);
    void deleteProperty(int id);
}