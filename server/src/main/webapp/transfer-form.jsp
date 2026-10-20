<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.Transfer" %>
<%@ page import="model.IssueRequest" %>
<%@ page import="service.IssueRequestService" %>
<%@ page import="java.util.List" %>
<%
    Transfer record = (Transfer) request.getAttribute("record");
    boolean edit = record != null && record.getId() > 0;

    IssueRequestService reqService = new IssueRequestService();
    List<IssueRequest> requests = reqService.getAllRequests();
%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title><%= edit ? "Редагування передачі" : "Нова передача" %></title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <h1><%= edit ? "Редагування запису передачі" : "Новий запис передачі майна" %></h1>

    <form method="post" action="transfers">
        <input type="hidden" name="action" value="<%= edit ? "update" : "insert" %>">

        <% if (edit) { %>
            <input type="hidden" name="id" value="<%= record.getId() %>">
        <% } %>

        <label>Виберіть заявку (ID / Статус):</label>
        <select name="requestId" required>
            <% for (IssueRequest r : requests) {
                boolean selected = edit && record.getRequestId() == r.getId();
            %>
                <option value="<%= r.getId() %>" <%= selected ? "selected" : "" %>>
                    Заявка №<%= r.getId() %>
                    (Користувач ID: <%= r.getUserId() %>,
                    Статус: <%= r.getRequestStatus() %>)
                </option>
            <% } %>
        </select>

        <label>Дата передачі:</label>
        <input type="date" name="transferDate"
               value="<%= record != null && record.getTransferDate() != null
                       ? record.getTransferDate() : "" %>" required>

        <label>Дата повернення (опціонально):</label>
        <input type="date" name="returnDate"
               value="<%= record != null && record.getReturnDate() != null
                       ? record.getReturnDate() : "" %>">

        <label>Примітки:</label>
        <textarea name="notes" rows="3"
                  style="padding: 8px; border-radius: 4px; border: 1px solid #cbd5e1;"><%= record != null && record.getNotes() != null
                ? record.getNotes() : "" %></textarea>

        <button type="submit">Зберегти</button>
        <a class="button" href="<index.jsp?tab=transfers"
           style="background: #ccc; text-align: center; text-decoration: none; color: black;">
            Назад
        </a>
    </form>
</body>
</html>
