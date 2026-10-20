package main.dao.Property;

import dao.DBUtil;
import model.PropertyItem;
import status.ItemState;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PropertyDAOImpl implements PropertyDAO {
    @Override
    public List<PropertyItem> getAllProperties() {
        List<PropertyItem> list = new ArrayList<>();
        String sql = "SELECT id, inventory_number, name, category, status_item FROM property_items ORDER BY id";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new PropertyItem(
                        rs.getInt("id"),
                        rs.getString("inventory_number"),
                        rs.getString("name"),
                        rs.getString("category"),
                        ItemState.valueOf(rs.getString("status_item"))
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка читання майна", e);
        }
        return list;
    }

    @Override
    public PropertyItem getPropertyById(int id) {
        String sql = "SELECT id, inventory_number, name, category, status_item FROM property_items WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new PropertyItem(
                            rs.getInt("id"),
                            rs.getString("inventory_number"),
                            rs.getString("name"),
                            rs.getString("category"),
                            ItemState.valueOf(rs.getString("status_item"))
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка читання майна", e);
        }
        return null;
    }

    @Override
    public void addProperty(PropertyItem property) {
        String sql = "INSERT INTO property_items (inventory_number, name, category, status_item) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, property.getInventoryNumber());
            ps.setString(2, property.getName());
            ps.setString(3, property.getCategory());
            ps.setString(4, property.getStatusItem().name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка додавання майна", e);
        }
    }

    @Override
    public void updateProperty(PropertyItem property) {
        String sql = "UPDATE property_items SET inventory_number = ?, name = ?, category = ?, status_item = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, property.getInventoryNumber());
            ps.setString(2, property.getName());
            ps.setString(3, property.getCategory());
            ps.setString(4, property.getStatusItem().name());
            ps.setInt(5, property.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка оновлення майна", e);
        }
    }

    @Override
    public void deleteProperty(int id) {
        String sql = "DELETE FROM property_items WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка видалення майна", e);
        }
    }
}