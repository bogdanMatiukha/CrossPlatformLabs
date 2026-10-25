package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.PropertyItem;
import service.PropertyService;
import status.ItemState;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PropertyServlet extends HttpServlet {
    private PropertyService propertyService;
    private ObjectMapper mapper;

    @Override
    public void init() {
        propertyService = new PropertyService();
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
                case "delete" -> deleteProperty(request, response);
                default -> listProperties(request, response);
            }
        } catch (Exception e) {
            handleError(request, response, e);
        }
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/property-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        PropertyItem property = propertyService.getPropertyById(id);

        if (property == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?tab=properties");
            return;
        }

        request.setAttribute("property", property);
        request.getRequestDispatcher("/property-form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            if ("insert".equals(action)) {
                insertProperty(request, response);
            } else if ("update".equals(action)) {
                updateProperty(request, response);
            } else {
                writeJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Невідома операція");
            }
        } catch (Exception e) {
            handleError(request, response, e);
        }
    }

    private void listProperties(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<PropertyItem> list = propertyService.getAllProperties();
        if (wantsJson(request)) {
            writeJson(response, list);
        } else {
            response.sendRedirect(request.getContextPath() + "/index.jsp?tab=properties");
        }
    }

    private void insertProperty(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String invNum = request.getParameter("inventoryNumber");
        String name = request.getParameter("name");
        String category = request.getParameter("category");
        ItemState status = ItemState.valueOf(request.getParameter("statusItem"));

        PropertyItem item = new PropertyItem(0, invNum, name, category, status);
        propertyService.addProperty(item);
        sendAfterChange(request, response);
    }

    private void updateProperty(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String invNum = request.getParameter("inventoryNumber");
        String name = request.getParameter("name");
        String category = request.getParameter("category");
        ItemState status = ItemState.valueOf(request.getParameter("statusItem"));

        PropertyItem item = new PropertyItem(id, invNum, name, category, status);
        propertyService.updateProperty(item);
        sendAfterChange(request, response);
    }

    private void deleteProperty(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        propertyService.deleteProperty(id);
        sendAfterChange(request, response);
    }

    private boolean wantsJson(HttpServletRequest request) {
        return "json".equalsIgnoreCase(request.getParameter("format"));
    }

    private void sendAfterChange(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (wantsJson(request)) {
            writeJson(response, propertyService.getAllProperties());
        } else {
            response.sendRedirect(request.getContextPath() + "/index.jsp?tab=properties");
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

    private void handleError(HttpServletRequest request,
                             HttpServletResponse response,
                             Exception e) throws IOException {

        if (e instanceof IllegalStateException
                || e instanceof IllegalArgumentException) {

            if (wantsJson(request)) {
                writeJsonError(
                        response,
                        HttpServletResponse.SC_CONFLICT,
                        e.getMessage()
                );
            } else {
                response.sendRedirect(
                        request.getContextPath()
                                + "/index.jsp?tab=properties&error=property_unavailable"
                );
            }

            return;
        }

        writeJsonError(
                response,
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Помилка сервера: " + e.getMessage()
        );
    }
}