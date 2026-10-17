package dao;

import model.Project;
import java.util.List;

public interface ProjectDAO {
    List<Project> getAllProjects();
    Project getProjectById(int id);
    void addProject(Project project);
    void updateProject(Project project);
    void deleteProject(int id);
}