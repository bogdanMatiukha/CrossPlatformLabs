package lab10.service;

import lab10.dao.ProjectDAO;
import lab10.dao.ProjectDAOImpl;
import lab10.model.Project;

import java.util.List;

public class ProjectService {
    private final ProjectDAO dao;

    public ProjectService() {
        this.dao = new ProjectDAOImpl();
    }

    public void addProject(Project project) {
        dao.add(project);
    }

    public List<Project> getAllProjects() {
        return dao.getAll();
    }

    public void deleteProject(int id) {
        dao.delete(id);
    }

    public void updateProject(Project project) {
        dao.update(project);
    }
}