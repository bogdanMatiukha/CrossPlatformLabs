<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.PropertyItem" %>
<%@ page import="service.PropertyService" %>
<%
    PropertyService propService = new PropertyService();
    List<PropertyItem> properties = propService.getAllProperties();
%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Облік майна</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <h1>Інвентарний перелік майна</h1>
    <p>
        <a class="button" href="properties?action=new">Додати майно</a>
    </p>
    <table>
        <tr>
            <th>ID</th>
            <th>Інвентарний номер</th>
            <th>Назва</th>
            <th>Категорія</th>
            <th>Статус</th>
            <th>Дії</th>
        </tr>
        <% if (properties != null) {
            for (PropertyItem p : properties) { %>
        <tr>
            <td><%= p.getId() %></td>
            <td><%= p.getInventoryNumber() %></td>
            <td><%= p.getName() %></td>
            <td><%= p.getCategory() %></td>
            <td><%= p.getStatusItem() %></td>
            <td>
                <a href="properties?action=edit&id=<%= p.getId() %>">Редагувати</a> |
                <a href="properties?action=delete&id=<%= p.getId() %>"
                   onclick="return confirm('Видалити майно?')">Видалити</a>
            </td>
        </tr>
        <%   }
        } %>
    </table>
</body>
</html>