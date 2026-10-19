<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.IssueRequest" %>
<%@ page import="service.IssueRequestService" %>
<%
    IssueRequestService reqService = new IssueRequestService();
    List<IssueRequest> requests = reqService.getAllRequests();
%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Облік користувачів</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <h1>Список заявок</h1>
    <p>
        <a class="button" href="requests?action=new">Створити нову заявку</a>
    </p>
    <table>
        <tr>
            <th>ID</th>
            <th>ID Користувача</th>
            <th>ID Майна</th>
            <th>Статус (ЛЦ)</th>
            <th>F1 (Терміновість)</th>
            <th>F2 (Критичність)</th>
            <th>F3 (Дефіцит)</th>
            <th>Інтегральний індекс</th>
            <th>Дії</th>
        </tr>
        <% if (requests != null) {
            for (IssueRequest r : requests) { %>
        <tr>
            <td><%= r.getId() %></td>
            <td><%= r.getUserId() %></td>
            <td><%= r.getPropertyId() %></td>
            <td><strong><%= r.getRequestStatus() %></strong></td>
            <td><%= r.getUrgencyF1() %></td>
            <td><%= r.getCriticalityF2() %></td>
            <td><%= r.getDeficitF3() %></td>
            <td><%= String.format("%.2f", r.getIntegralIndex()) %></td>
            <td>
                <a href="requests?action=edit&id=<%= r.getId() %>">Редагувати</a>|
                <a href="requests?action=delete&id=<%= r.getId() %>" onclick="return confirm('Видалити заявку?')">Видалити</a>
            </td>
        </tr>
        <%   }
        } %>
    </table>
</body>
</html>