package main.dao;

import main.model.Project;

import java.util.List;

public interface ProjectDAO {
    void add(Project project);

    List<Project> getAll();

    void update(Project project);

    void delete(int id);

    List<Project> filterExact(String field, String value);

    List<Project> filterLike(String field, String value);
}