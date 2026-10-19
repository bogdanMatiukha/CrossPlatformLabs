package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.User;
import service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class UserServlet extends HttpServlet {
    private UserService userService;
    private ObjectMapper mapper;

    @Override
    public void init() {
        userService = new UserService();
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
                case "new" -> showNewForm(request, response);
                case "edit" -> showEditForm(request, response);
                case "delete" -> deleteUser(request, response);
                default -> listUsers(request, response);
            }
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/User/user-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        User user = userService.getUserById(id);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?tab=users");
            return;
        }

        request.setAttribute("user", user);
        request.getRequestDispatcher("/User/user-form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            if ("insert".equals(action)) {
                insertUser(request, response);
            } else if ("update".equals(action)) {
                updateUser(request, response);
            } else {
                writeJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Невідома операція");
            }
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    private void listUsers(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<User> users = userService.getAllUsers();
        if (wantsJson(request)) {
            writeJson(response, users);
        } else {
            response.sendRedirect(request.getContextPath() + "/index.jsp?tab=users");
        }
    }

    private void insertUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fullName = request.getParameter("fullName");
        String role = request.getParameter("role");
        String email = request.getParameter("email");

        User user = new User(0, fullName, role, email);
        userService.addUser(user);
        sendAfterChange(request, response);
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String fullName = request.getParameter("fullName");
        String role = request.getParameter("role");
        String email = request.getParameter("email");

        User user = new User(id, fullName, role, email);
        userService.updateUser(user);
        sendAfterChange(request, response);
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        userService.deleteUser(id);
        sendAfterChange(request, response);
    }

    private boolean wantsJson(HttpServletRequest request) {
        return "json".equalsIgnoreCase(request.getParameter("format"));
    }

    private void sendAfterChange(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (wantsJson(request)) {
            writeJson(response, userService.getAllUsers());
        } else {
            response.sendRedirect(request.getContextPath() + "/index.jsp?tab=users");
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

    private void handleError(HttpServletResponse response, Exception e) throws IOException {
        writeJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Помилка сервера: " + e.getMessage());
    }
}