package dao;

import lab11.model.Project;
import lab11.dao.ProjectDAOImpl;
import lab11.dao.DBUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ProjectDAOImplTest {
    private ProjectDAOImpl dao;

    @BeforeEach
    void setup() {
        dao = new ProjectDAOImpl();
    }

    @AfterEach
    void cleanDatabase() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM projects");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testAddProject() {
        Project project = new Project("JUnit DAO Project", "Менеджер А", 30);
        dao.add(project);
        List<Project> list = dao.getAll();
        boolean found = list.stream()
                .anyMatch(p -> p.getName().equals("JUnit DAO Project"));
        assertTrue(found);
    }

    @Test
    void testGetAll() {
        List<Project> list = dao.getAll();
        assertNotNull(list);
    }

    @Test
    void testUpdateProject() {
        Project project = new Project("Old Project", "Менеджер Б", 15);
        dao.add(project);
        List<Project> list = dao.getAll();
        Project last = list.get(list.size() - 1);
        last.setName("Updated DAO Project");
        dao.update(last);
        List<Project> updated = dao.getAll();
        boolean found = updated.stream()
                .anyMatch(p -> p.getName().equals("Updated DAO Project"));
        assertTrue(found);
    }

    @Test
    void testDeleteProject() {
        Project project = new Project("Delete DAO Project", "Менеджер В", 10);
        dao.add(project);
        List<Project> list = dao.getAll();
        Project last = list.get(list.size() - 1);
        dao.delete(last.getId());
        List<Project> after = dao.getAll();
        boolean exists = after.stream()
                .anyMatch(p -> p.getId() == last.getId());
        assertFalse(exists);
    }

    @Test
    void testFilterExact() {
        Project project = new Project("ExactDAO", "Менеджер Г", 20);
        dao.add(project);
        List<Project> result = dao.filterExact("name", "ExactDAO");
        assertFalse(result.isEmpty());
    }

    @Test
    void testFilterLike() {
        Project project = new Project("LikeDAO123", "Менеджер Д", 25);
        dao.add(project);
        List<Project> result = dao.filterLike("name", "LikeDAO");
        assertFalse(result.isEmpty());
    }
}