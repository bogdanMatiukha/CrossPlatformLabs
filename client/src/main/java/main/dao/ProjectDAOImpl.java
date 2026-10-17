package main.dao;

import main.model.Project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProjectDAOImpl implements ProjectDAO {

    private static final Map<String, String> ALLOWED_FIELDS = Map.of(
            "id", "id",
            "name", "name",
            "manager", "manager",
            "duration", "duration"
    );

    @Override
    public void add(Project project) {
        String sql = "INSERT INTO projects (name, manager, duration) VALUES (?, ?, ?)";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, project.getName());
            statement.setString(2, project.getManager());
            statement.setInt(3, project.getDuration());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося додати проєкт.", e);
        }
    }

    @Override
    public List<Project> getAll() {
        String sql = "SELECT id, name, manager, duration FROM projects ORDER BY id";
        List<Project> projects = new ArrayList<>();

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                projects.add(mapRow(resultSet));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося завантажити проєкти.", e);
        }

        return projects;
    }

    @Override
    public void update(Project project) {
        String sql = "UPDATE projects SET name = ?, manager = ?, duration = ? WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, project.getName());
            statement.setString(2, project.getManager());
            statement.setInt(3, project.getDuration());
            statement.setInt(4, project.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося оновити проєкт.", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM projects WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося видалити проєкт.", e);
        }
    }

    @Override
    public List<Project> filterExact(String field, String value) {
        String dbField = validateField(field);
        String sql = "SELECT id, name, manager, duration FROM projects WHERE "
                + dbField + " = ? ORDER BY id";

        List<Project> projects = new ArrayList<>();

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (dbField.equals("id") || dbField.equals("duration")) {
                statement.setInt(1, parseInteger(value));
            } else {
                statement.setString(1, value);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    projects.add(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося виконати фільтрацію.", e);
        }

        return projects;
    }

    @Override
    public List<Project> filterLike(String field, String value) {
        String dbField = validateField(field);
        String sql = "SELECT id, name, manager, duration FROM projects WHERE "
                + dbField + " LIKE ? ORDER BY id";

        List<Project> projects = new ArrayList<>();

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, "%" + value + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    projects.add(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося виконати пошук.", e);
        }

        return projects;
    }

    private String validateField(String field) {
        String dbField = ALLOWED_FIELDS.get(field);

        if (dbField == null) {
            throw new IllegalArgumentException("Недозволене поле для пошуку.");
        }

        return dbField;
    }

    private int parseInteger(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Для поля ID або тривалості введіть ціле число."
            );
        }
    }

    private Project mapRow(ResultSet resultSet) throws SQLException {
        return new Project(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("manager"),
                resultSet.getInt("duration")
        );
    }
}