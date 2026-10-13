package lab10.dao;

import lab10.model.Project;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAOImpl implements ProjectDAO {
    private final List<Project> projects = new ArrayList<>();

    @Override
    public void add(Project project) {
        projects.add(project);
    }

    @Override
    public List<Project> getAll() {
        return new ArrayList<>(projects);
    }

    @Override
    public void delete(int id) {
        projects.removeIf(project -> project.getId() == id);
    }

    @Override
    public void update(Project project) {
        for (Project existing : projects) {
            if (existing.getId() == project.getId()) {
                existing.setName(project.getName());
                existing.setManager(project.getManager());
                existing.setDuration(project.getDuration());
                break;
            }
        }
    }
}