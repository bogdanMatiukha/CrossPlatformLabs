package lab10.dao;

import lab10.model.Project;
import java.util.List;

public interface ProjectDAO {
    void add(Project project);
    List<Project> getAll();
    void delete(int id);
    void update(Project project);
}
