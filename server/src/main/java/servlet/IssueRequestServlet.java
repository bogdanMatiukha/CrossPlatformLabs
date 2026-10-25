package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.IssueRequest;
import status.RequestState;
import service.IssueRequestService;
import model.PropertyItem;
import model.Transfer;
import service.PropertyService;
import service.TransferService;
import status.ItemState;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class IssueRequestServlet extends HttpServlet {
    private IssueRequestService requestService;
    private ObjectMapper mapper;
    private PropertyService propertyService;
    private TransferService transferService;

    @Override
    public void init() {
        requestService = new IssueRequestService();
        propertyService = new PropertyService();
        transferService = new TransferService();
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
                case "delete" -> deleteRequest(request, response);
                default -> listRequests(request, response);
            }
        } catch (Exception e) {
            handleError(request, response, e);
        }
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (wantsJson(request)) {
            writeJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Неочікуваний формат для форми");
        } else {
            request.getRequestDispatcher("request-form.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            if ("insert".equals(action)) {
                insertRequest(request, response);
            } else if ("update".equals(action)) {
                updateRequest(request, response);
            } else {
                if (wantsJson(request)) {
                    writeJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Невідома операція");
                } else {
                    response.sendRedirect("requests");
                }
            }
        } catch (Exception e) {
            handleError(request, response, e);
        }
    }

    private void listRequests(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<IssueRequest> requests = requestService.getAllRequests();
        if (wantsJson(request)) {
            writeJson(response, requests);
        } else {
            request.setAttribute("requests", requests);
            response.sendRedirect("index.jsp?tab=requests");
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        IssueRequest req = requestService.getRequestById(id);
        if (req == null) {
            if (wantsJson(request)) {
                writeJsonError(response, HttpServletResponse.SC_NOT_FOUND, "Заявку не знайдено");
            } else {
                response.sendRedirect("index.jsp?tab=requests");
            }
            return;
        }
        if (wantsJson(request)) {
            writeJson(response, req);
        } else {
            request.setAttribute("issueRequest", req);
            request.getRequestDispatcher("request-form.jsp").forward(request, response);
        }
    }

    private void insertRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int userId = Integer.parseInt(request.getParameter("userId"));
        int propertyId = Integer.parseInt(request.getParameter("propertyId"));

        String selectedStatus = request.getParameter("requestStatus");

        int f1 = Integer.parseInt(request.getParameter("urgencyF1"));
        int f2 = Integer.parseInt(request.getParameter("criticalityF2"));
        int f3 = Integer.parseInt(request.getParameter("deficitF3"));

        IssueRequest issueRequest = new IssueRequest(
                userId,
                propertyId,
                RequestState.CREATED,
                f1,
                f2,
                f3
        );

        requestService.addRequest(issueRequest);

        if (selectedStatus != null
                && !RequestState.CREATED.name().equalsIgnoreCase(selectedStatus)) {

            if (wantsJson(request)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                mapper.writeValue(response.getWriter(), Map.of(
                        "message", "Заявки спочатку створюються зі статусом CREATED",
                        "request", issueRequest
                ));
                return;
            }

            response.sendRedirect(
                    "index.jsp?tab=requests&message=request_created"
            );
            return;
        }

        sendAfterChange(request, response);
    }

    private void updateRequest(HttpServletRequest request,
                               HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        int userId = Integer.parseInt(request.getParameter("userId"));
        int propertyId = Integer.parseInt(request.getParameter("propertyId"));

        RequestState status = RequestState.valueOf(
                request.getParameter("requestStatus")
        );

        int f1 = Integer.parseInt(request.getParameter("urgencyF1"));
        int f2 = Integer.parseInt(request.getParameter("criticalityF2"));
        int f3 = Integer.parseInt(request.getParameter("deficitF3"));

        IssueRequest oldRequest = requestService.getRequestById(id);

        if (oldRequest == null) {
            if (wantsJson(request)) {
                writeJsonError(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        "Заявку не знайдено"
                );
            } else {
                response.sendRedirect("requests");
            }
            return;
        }

        RequestState oldStatus = oldRequest.getRequestStatus();


        if (oldStatus != status) {
            boolean allowed = switch (oldStatus) {
                case CREATED ->
                        status == RequestState.APPROVED
                                || status == RequestState.CANCELLED;

                case APPROVED ->
                        status == RequestState.PUBLISHED
                                || status == RequestState.CANCELLED;

                case PUBLISHED ->
                        status == RequestState.RETURNED;

                case RETURNED, CANCELLED -> false;
            };

            if (!allowed) {
                String message = "Не можна змінити статус з "
                        + oldStatus + " на " + status;

                if (wantsJson(request)) {
                    writeJsonError(
                            response,
                            HttpServletResponse.SC_CONFLICT,
                            message
                    );
                } else {
                    response.sendRedirect(
                            "index.jsp?tab=requests&error=invalid_status"
                    );
                }

                return;
            }
        }


        // Check whether the property is already issued
        if (status == RequestState.PUBLISHED
                && oldStatus != RequestState.PUBLISHED) {

            PropertyItem item = propertyService.getPropertyById(propertyId);

            boolean alreadyPublished =
                    requestService.isPropertyPublishedInAnotherRequest(propertyId, id);

            if (item == null || alreadyPublished
                    || item.getStatusItem() == ItemState.UNAVAILABLE) {

                if (wantsJson(request)) {
                    writeJsonError(
                            response,
                            HttpServletResponse.SC_CONFLICT,
                            "Товар наразі недоступний"
                    );
                } else {
                    response.sendRedirect(
                            "index.jsp?tab=requests&error=property_unavailable"
                    );
                }

                return;
            }
        }

        IssueRequest issueRequest = new IssueRequest(
                id,
                userId,
                propertyId,
                status,
                f1,
                f2,
                f3,
                IssueRequest.calculateIntegralIndex(f1, f2, f3)
        );

        requestService.updateRequest(issueRequest);

        PropertyItem item = propertyService.getPropertyById(propertyId);

        List<Transfer> transfers = transferService.getAllTransfers();

        if (status == RequestState.APPROVED
                && oldStatus != RequestState.APPROVED) {

            boolean transferExists = transfers.stream()
                    .anyMatch(transfer ->
                            transfer.getRequestId() == id
                                    && transfer.getTransferDate() == null
                                    && transfer.getReturnDate() == null
                    );

            if (!transferExists) {
                Transfer transfer = new Transfer(
                        0,
                        id,
                        (LocalDate) null,
                        null,
                        ""
                );

                transferService.addTransfer(transfer);
            }
        }

        if (status == RequestState.PUBLISHED
                && oldStatus != RequestState.PUBLISHED) {

            Transfer transfer = transfers.stream()
                    .filter(t ->
                            t.getRequestId() == id
                                    && t.getReturnDate() == null
                    )
                    .findFirst()
                    .orElse(null);

            if (transfer == null) {
                transfer = new Transfer(
                        0,
                        id,
                        LocalDate.now(),
                        null,
                        ""
                );

                transferService.addTransfer(transfer);
            } else if (transfer.getTransferDate() == null) {
                transfer.setTransferDate(LocalDate.now());
                transferService.updateTransfer(transfer);
            }

            if (item != null) {
                item.setStatusItem(ItemState.UNAVAILABLE);
                propertyService.updateProperty(item);
            }
        }

        if (status == RequestState.RETURNED
                && oldStatus != RequestState.RETURNED) {

            transfers.stream()
                    .filter(t ->
                            t.getRequestId() == id
                                    && t.getTransferDate() != null
                                    && t.getReturnDate() == null
                    )
                    .findFirst()
                    .ifPresent(transfer -> {
                        transfer.setReturnDate(LocalDate.now());
                        transferService.updateTransfer(transfer);
                    });

            if (item != null) {
                item.setStatusItem(ItemState.AVAILABLE);
                propertyService.updateProperty(item);
            }
        }

        sendAfterChange(request, response);
    }

    private void deleteRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        requestService.deleteRequest(id);
        sendAfterChange(request, response);
    }

    private boolean wantsJson(HttpServletRequest request) {
        return "json".equalsIgnoreCase(request.getParameter("format"));
    }

    private void sendAfterChange(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (wantsJson(request)) {
            writeJson(response, requestService.getAllRequests());
        } else {
            response.sendRedirect("requests");
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
            writeJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Помилка сервера: " + e.getMessage());
        } else {
            throw new ServletException(e);
        }
    }
}