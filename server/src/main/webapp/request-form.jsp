<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.IssueRequest" %>
<%@ page import="model.User" %>
<%@ page import="model.PropertyItem" %>
<%@ page import="service.IssueRequestService" %>
<%@ page import="service.UserService" %>
<%@ page import="service.PropertyService" %>
<%@ page import="java.util.List" %>
<%
    String idParam = request.getParameter("id");
    IssueRequest req = null;
    if (idParam != null && !idParam.isBlank()) {
        IssueRequestService service = new IssueRequestService();
        req = service.getRequestById(Integer.parseInt(idParam));
    }
    boolean edit = req != null && req.getId() > 0;

    UserService userService = new UserService();
    List<User> users = userService.getAllUsers();

    PropertyService propService = new PropertyService();
    List<PropertyItem> properties = propService.getAllProperties();
%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title><%= edit ? "Редагування заявки" : "Створення заявки" %></title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <h1><%= edit ? "Редагування заявки на видачу" : "Нова заявка на видачу майна" %></h1>
    <form method="post" action="requests">
        <input type="hidden" name="action" value="<%= edit ? "update" : "insert" %>">
        <% if (edit) { %>
            <input type="hidden" name="id" value="<%= req.getId() %>">
        <% } %>

        <label>Користувач:</label>
        <select name="userId" required>
            <% for (User u : users) {
                boolean selected = edit && req.getUserId() == u.getId();
            %>
                <option value="<%= u.getId() %>" <%= selected ? "selected" : "" %>>
                    <%= u.getFullName() %> (<%= u.getRole() %>)
                </option>
            <% } %>
        </select>

        <label>Майно:</label>
        <select name="propertyId" required>
            <% for (PropertyItem p : properties) {
                boolean selected = edit && req.getPropertyId() == p.getId();
            %>
                <option value="<%= p.getId() %>" <%= selected ? "selected" : "" %>>
                    [<%= p.getInventoryNumber() %>] <%= p.getName() %> (<%= p.getCategory() %>)
                </option>
            <% } %>
        </select>

        <label>Статус (Життєвий цикл):</label>
        <select name="requestStatus">
            <option value="CREATED" <%= (req != null && "CREATED".equals(req.getRequestStatus())) ? "selected" : "" %>>CREATED (Створено)</option>
            <option value="APPROVED" <%= (req != null && "APPROVED".equals(req.getRequestStatus())) ? "selected" : "" %>>APPROVED (Схвалено)</option>
            <option value="PUBLISHED" <%= (req != null && "PUBLISHED".equals(req.getRequestStatus())) ? "selected" : "" %>>PUBLISHED (Видано)</option>
            <option value="RETURNED" <%= (req != null && "RETURNED".equals(req.getRequestStatus())) ? "selected" : "" %>>RETURNED (Повернуто)</option>
        </select>

        <label>F1 — Терміновість робіт (0–100):</label>
        <input type="number" name="urgencyF1" value="<%= req == null ? "0" : req.getUrgencyF1() %>" min="0" max="100" required>

        <label>F2 — Критичність майна (0–100):</label>
        <input type="number" name="criticalityF2" value="<%= req == null ? "0" : req.getCriticalityF2() %>" min="0" max="100" required>

        <label>F3 — Дефіцит доступних одиниць (0–100):</label>
        <input type="number" name="deficitF3" value="<%= req == null ? "0" : req.getDeficitF3() %>" min="0" max="100" required>

        <button type="submit">Зберегти</button>
        <a class="button" href="index.jsp?tab=requests" style="background: #ccc; text-align: center; text-decoration: none; color: black;">Назад</a>
    </form>
</body>
</html>