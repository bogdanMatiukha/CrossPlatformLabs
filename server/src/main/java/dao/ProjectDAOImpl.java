package dao;

import model.Project;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAOImpl implements ProjectDAO {

    @Override
    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT id, name, manager, duration FROM projects ORDER BY id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                projects.add(new Project(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("manager"),
                        rs.getInt("duration")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка читання проєктів", e);
        }
        return projects;
    }

    @Override
    public Project getProjectById(int id) {
        String sql = "SELECT id, name, manager, duration FROM projects WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Project(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("manager"),
                            rs.getInt("duration")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка читання проєкту", e);
        }
        return null;
    }

    @Override
    public void addProject(Project project) {
        String sql = "INSERT INTO projects (name, manager, duration) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, project.getName());
            ps.setString(2, project.getManager());
            ps.setInt(3, project.getDuration());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка додавання проєкту", e);
        }
    }

    @Override
    public void updateProject(Project project) {
        String sql = "UPDATE projects SET name = ?, manager = ?, duration = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, project.getName());
            ps.setString(2, project.getManager());
            ps.setInt(3, project.getDuration());
            ps.setInt(4, project.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка оновлення проєкту", e);
        }
    }

    @Override
    public void deleteProject(int id) {
        String sql = "DELETE FROM projects WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка видалення проєкту", e);
        }
    }
}