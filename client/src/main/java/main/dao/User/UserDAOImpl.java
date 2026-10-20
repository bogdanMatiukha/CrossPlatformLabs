package main.dao.User;

import main.dao.DBUtil;
import main.dao.User.UserDAO;
import main.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {
    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, full_name, role, email FROM users ORDER BY id";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(rs.getInt("id"), rs.getString("full_name"), rs.getString("role"), rs.getString("email")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка читання користувачів", e);
        }
        return users;
    }

    @Override
    public User getUserById(int id) {
        String sql = "SELECT id, full_name, role, email FROM users WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("full_name"), rs.getString("role"), rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка читання користувача", e);
        }
        return null;
    }

    @Override
    public void addUser(User user) {
        String sql = "INSERT INTO users (full_name, role, email) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getRole());
            ps.setString(3, user.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка додавання користувача", e);
        }
    }

    @Override
    public void updateUser(User user) {
        String sql = "UPDATE users SET full_name = ?, role = ?, email = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getRole());
            ps.setString(3, user.getEmail());
            ps.setInt(4, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка оновлення користувача", e);
        }
    }

    @Override
    public void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка видалення користувача", e);
        }
    }
}