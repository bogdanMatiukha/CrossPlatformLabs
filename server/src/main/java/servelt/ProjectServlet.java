package servelt;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Project;
import service.ProjectService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ProjectServlet extends HttpServlet {
    private ProjectService projectService;
    private ObjectMapper mapper;

    @Override
    public void init() {
        projectService = new ProjectService();
        mapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null || action.isBlank()) {
            action = "list";
        }

        try {
            switch (action) {
                case "edit" -> showEditForm(request, response);
                case "delete" -> deleteProject(request, response);
                default -> listProjects(request, response);
            }
        } catch (Exception e) {
            handleError(request, response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            if ("insert".equals(action)) {
                insertProject(request, response);
            } else if ("update".equals(action)) {
                updateProject(request, response);
            } else {
                if (wantsJson(request)) {
                    writeJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Невідома операція");
                } else {
                    response.sendRedirect("projects");
                }
            }
        } catch (Exception e) {
            handleError(request, response, e);
        }
    }

    private void listProjects(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Project> projects = projectService.getAllProjects();
        if (wantsJson(request)) {
            writeJson(response, projects);
        } else {
            request.setAttribute("projects", projects);
            request.getRequestDispatcher("project-list.jsp").forward(request, response);
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Project project = projectService.getProjectById(id);
        if (project == null) {
            if (wantsJson(request)) {
                writeJsonError(response, HttpServletResponse.SC_NOT_FOUND, "Проєкт не знайдено");
            } else {
                response.sendRedirect("projects");
            }
            return;
        }
        if (wantsJson(request)) {
            writeJson(response, project);
        } else {
            request.setAttribute("project", project);
            request.getRequestDispatcher("project-form.jsp").forward(request, response);
        }
    }

    private void insertProject(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String manager = request.getParameter("manager");
        String durationText = request.getParameter("duration");

        String error = validate(name, manager, durationText);
        if (error != null) {
            sendValidationError(request, response, error);
            return;
        }

        Project project = new Project(name.trim(), manager.trim(), Integer.parseInt(durationText));
        projectService.addProject(project);
        sendAfterChange(request, response);
    }

    private void updateProject(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String manager = request.getParameter("manager");
        String durationText = request.getParameter("duration");

        String error = validate(name, manager, durationText);
        if (error != null) {
            if (!wantsJson(request)) {
                request.setAttribute("project", new Project(id, name, manager, 0));
            }
            sendValidationError(request, response, error);
            return;
        }

        Project project = new Project(id, name.trim(), manager.trim(), Integer.parseInt(durationText));
        projectService.updateProject(project);
        sendAfterChange(request, response);
    }

    private void deleteProject(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        projectService.deleteProject(id);
        sendAfterChange(request, response);
    }

    private String validate(String name, String manager, String durationText) {
        if (name == null || name.trim().isEmpty()) {
            return "Назва проєкту не може бути порожньою";
        }
        if (manager == null || manager.trim().isEmpty()) {
            return "Керівник не може бути порожнім";
        }
        try {
            int duration = Integer.parseInt(durationText);
            if (duration <= 0) return "Тривалість має бути більшою за нуль";
        } catch (Exception e) {
            return "Тривалість повинна бути цілим числом";
        }
        return null;
    }

    private boolean wantsJson(HttpServletRequest request) {
        return "json".equalsIgnoreCase(request.getParameter("format"));
    }

    private void sendAfterChange(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (wantsJson(request)) {
            writeJson(response, projectService.getAllProjects());
        } else {
            response.sendRedirect("projects");
        }
    }

    private void sendValidationError(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException {
        if (wantsJson(request)) {
            writeJsonError(response, HttpServletResponse.SC_BAD_REQUEST, error);
        } else {
            request.setAttribute("error", error);
            request.getRequestDispatcher("project-form.jsp").forward(request, response);
        }
    }

    private void writeJson(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        mapper.writeValue(response.getWriter(), data);
    }

    private void writeJsonError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        writeJson(response, Map.of("error", message));
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws ServletException, IOException {
        if (wantsJson(request)) {
            if (e instanceof NumberFormatException) {
                writeJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Некоректне числове значення");
            } else {
                writeJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Помилка сервера");
            }
        } else {
            throw new ServletException(e);
        }
    }
}