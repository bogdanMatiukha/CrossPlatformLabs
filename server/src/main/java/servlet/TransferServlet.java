package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.TransferRecord;
import service.TransferService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TransferServlet extends HttpServlet {
    private TransferService transferService;
    private ObjectMapper mapper;

    @Override
    public void init() {
        transferService = new TransferService();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
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
                case "delete" -> deleteTransfer(request, response);
                default -> listTransfers(request, response);
            }
        } catch (Exception e) {
            handleError(request, response, e);
        }
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (wantsJson(request)) {
            writeJsonError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Неочікуваний формат для форми");
        } else {
            request.getRequestDispatcher("transfer-form.jsp").forward(request, response);
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        TransferRecord record = transferService.getTransferById(id);

        if (record == null) {
            if (wantsJson(request)) {
                writeJsonError(response, HttpServletResponse.SC_NOT_FOUND,
                        "Запис передачі не знайдено");
            } else {
                response.sendRedirect("index.jsp?tab=transfers");
            }
            return;
        }

        if (wantsJson(request)) {
            writeJson(response, record);
        } else {
            request.setAttribute("record", record);
            request.getRequestDispatcher("transfer-form.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            if ("insert".equals(action)) {
                insertTransfer(request, response);
            } else if ("update".equals(action)) {
                updateTransfer(request, response);
            } else {
                if (wantsJson(request)) {
                    writeJsonError(response, HttpServletResponse.SC_BAD_REQUEST,
                            "Невідома операція");
                } else {
                    response.sendRedirect("transfers");
                }
            }
        } catch (Exception e) {
            handleError(request, response, e);
        }
    }

    private void listTransfers(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<TransferRecord> transfers = transferService.getAllTransfers();

        if (wantsJson(request)) {
            writeJson(response, transfers);
        } else {
            response.sendRedirect("index.jsp?tab=transfers");
        }
    }

    private void insertTransfer(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int requestId = Integer.parseInt(request.getParameter("requestId"));
        LocalDate transferDate = LocalDate.parse(request.getParameter("transferDate"));

        String returnDateStr = request.getParameter("returnDate");
        LocalDate returnDate = returnDateStr != null && !returnDateStr.isBlank()
                ? LocalDate.parse(returnDateStr) : null;

        String notes = request.getParameter("notes");

        TransferRecord record = new TransferRecord(
                0, requestId, transferDate, returnDate, notes
        );

        transferService.addTransfer(record);
        sendAfterChange(request, response);
    }

    private void updateTransfer(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        int requestId = Integer.parseInt(request.getParameter("requestId"));
        LocalDate transferDate = LocalDate.parse(request.getParameter("transferDate"));

        String returnDateStr = request.getParameter("returnDate");
        LocalDate returnDate = returnDateStr != null && !returnDateStr.isBlank()
                ? LocalDate.parse(returnDateStr) : null;

        String notes = request.getParameter("notes");

        TransferRecord record = new TransferRecord(
                id, requestId, transferDate, returnDate, notes
        );

        transferService.updateTransfer(record);
        sendAfterChange(request, response);
    }

    private void deleteTransfer(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        transferService.deleteTransfer(id);
        sendAfterChange(request, response);
    }

    private boolean wantsJson(HttpServletRequest request) {
        return "json".equalsIgnoreCase(request.getParameter("format"));
    }

    private void sendAfterChange(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (wantsJson(request)) {
            writeJson(response, transferService.getAllTransfers());
        } else {
            response.sendRedirect("transfers");
        }
    }

    private void writeJson(HttpServletResponse response, Object data)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        mapper.writeValue(response.getWriter(), data);
    }

    private void writeJsonError(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        writeJson(response, Map.of("error", message));
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response,
                             Exception e) throws ServletException, IOException {
        if (wantsJson(request)) {
            writeJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Помилка сервера: " + e.getMessage());
        } else {
            throw new ServletException(e);
        }
    }
}
