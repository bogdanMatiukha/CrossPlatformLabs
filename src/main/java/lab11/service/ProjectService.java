package lab11.service;

import lab11.dao.ProjectDAO;
import lab11.dao.ProjectDAOImpl;
import lab11.model.Project;

import java.util.List;

public class ProjectService {
    private final ProjectDAO dao = new ProjectDAOImpl();

    public void addProject(Project project) {
        dao.add(project);
    }

    public List<Project> getAllProjects() {
        return dao.getAll();
    }

    public void updateProject(Project project) {
        dao.update(project);
    }

    public void deleteProject(int id) {
        dao.delete(id);
    }

    public List<Project> filterExact(String field, String value) {
        return dao.filterExact(field, value);
    }

    public List<Project> filterLike(String field, String value) {
        return dao.filterLike(field, value);
    }
}