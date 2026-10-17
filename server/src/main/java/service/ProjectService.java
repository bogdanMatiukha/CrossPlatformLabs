package service;

import dao.ProjectDAO;
import dao.ProjectDAOImpl;
import model.Project;
import java.util.List;

public class ProjectService {
    private final ProjectDAO dao = new ProjectDAOImpl();

    public List<Project> getAllProjects() { return dao.getAllProjects(); }
    public Project getProjectById(int id) { return dao.getProjectById(id); }
    public void addProject(Project project) { dao.addProject(project); }
    public void updateProject(Project project) { dao.updateProject(project); }
    public void deleteProject(int id) { dao.deleteProject(id); }
}