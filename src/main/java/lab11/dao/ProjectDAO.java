package lab11.dao;

import lab11.model.Project;

import java.util.List;

public interface ProjectDAO {
    void add(Project project);

    List<Project> getAll();

    void update(Project project);

    void delete(int id);

    List<Project> filterExact(String field, String value);

    List<Project> filterLike(String field, String value);
}