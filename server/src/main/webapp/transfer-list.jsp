<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.TransferRecord" %>
<%@ page import="service.TransferService" %>
<%
    TransferService transferService = new TransferService();
    List<TransferRecord> transfers = transferService.getAllTransfers();
%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Облік передачі майна</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <h1>Журнал записів передачі майна</h1>

    <p>
        <a class="button" href="transfers?action=new">
            Додати запис передачі
        </a>
    </p>

    <table>
        <tr>
            <th>ID</th>
            <th>ID заявки</th>
            <th>Дата передачі</th>
            <th>Дата повернення</th>
            <th>Примітки</th>
            <th>Дії</th>
        </tr>

        <% if (transfers != null) {
            for (TransferRecord t : transfers) { %>
        <tr>
            <td><%= t.getId() %></td>
            <td><%= t.getRequestId() %></td>
            <td><%= t.getTransferDate() %></td>
            <td><%= t.getReturnDate() != null
                    ? t.getReturnDate() : "Не повернено" %></td>
            <td><%= t.getNotes() != null ? t.getNotes() : "" %></td>
            <td>
                <a href="transfers?action=edit&id=<%= t.getId() %>">Редагувати</a> |
                <a href="transfers?action=delete&id=<%= t.getId() %>"
                   onclick="return confirm('Видалити запис?')">Видалити</a>
            </td>
        </tr>
        <%  }
        } %>
    </table>
</body>
</html>
