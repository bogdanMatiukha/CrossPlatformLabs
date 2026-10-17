package main.service;

import main.dao.ProjectDAO;
import main.dao.ProjectDAOImpl;
import main.model.Project;
import java.util.List;

public class ProjectService {
    private final ProjectDAO dao = new ProjectDAOImpl();

    public void addProject(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null");
        }
        if (project.getName() == null || project.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }
        if (project.getDuration() <= 0) {
            throw new IllegalArgumentException("Duration must be greater than zero");
        }
        dao.add(project);
    }

    public List<Project> getAllProjects() {
        return dao.getAll();
    }

    public void updateProject(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null");
        }
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