package service;

import main.model.Project;
import main.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ProjectServiceTest {
    private ProjectService service;

    @BeforeEach
    void setup() {
        service = new ProjectService();
    }

    @Test
    void testAddProject() {
        Project project = new Project("ServiceAdd", "Менеджер А", 40);
        service.addProject(project);
        List<Project> list = service.getAllProjects();
        boolean found = list.stream()
                .anyMatch(p -> p.getName().equals("ServiceAdd"));
        assertTrue(found);
    }

    @Test
    void testGetAllProjects() {
        List<Project> list = service.getAllProjects();
        assertNotNull(list);
    }

    @Test
    void testUpdateProject() {
        Project project = new Project("ServiceOld", "Менеджер Б", 20);
        service.addProject(project);
        List<Project> list = service.getAllProjects();
        Project last = list.get(list.size() - 1);
        last.setName("ServiceUpdated");
        service.updateProject(last);
        List<Project> updated = service.getAllProjects();
        boolean found = updated.stream()
                .anyMatch(p -> p.getName().equals("ServiceUpdated"));
        assertTrue(found);
    }

    @Test
    void testDeleteProject() {
        Project project = new Project("ServiceDelete", "Менеджер В", 15);
        service.addProject(project);
        List<Project> list = service.getAllProjects();
        Project last = list.get(list.size() - 1);
        service.deleteProject(last.getId());
        List<Project> after = service.getAllProjects();
        boolean exists = after.stream()
                .anyMatch(p -> p.getId() == last.getId());
        assertFalse(exists);
    }

    @Test
    void testFilterExact() {
        Project project = new Project("ServiceExact", "Менеджер Г", 50);
        service.addProject(project);
        List<Project> result = service.filterExact("name", "ServiceExact");
        assertFalse(result.isEmpty());
    }

    @Test
    void testFilterLike() {
        Project project = new Project("ServiceLike123", "Менеджер Д", 60);
        service.addProject(project);
        List<Project> result = service.filterLike("name", "ServiceLike");
        assertFalse(result.isEmpty());
    }

    @Test
    void testAddNullProject() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.addProject(null);
        });
    }

    @Test
    void testNegativeDuration() {
        Project project = new Project("BadDuration", "Менеджер Е", -5);
        assertThrows(IllegalArgumentException.class, () -> {
            service.addProject(project);
        });
    }
}