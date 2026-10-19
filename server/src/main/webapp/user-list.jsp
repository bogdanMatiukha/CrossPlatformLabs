<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.User" %>
<%@ page import="service.UserService" %>
<%
    UserService userService = new UserService();
    List<User> users = userService.getAllUsers();
%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Облік користувачів</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <h1>Список користувачів</h1>
    <p>
        <a class="button" href="users?action=new">Додати користувача</a>
    </p>
    <table>
        <tr>
            <th>ID</th>
            <th>ПІБ</th>
            <th>Роль</th>
            <th>Email</th>
            <th>Дії</th>
        </tr>
        <% if (users != null) {
            for (User u : users) { %>
        <tr>
            <td><%= u.getId() %></td>
            <td><%= u.getFullName() %></td>
            <td><%= u.getRole() %></td>
            <td><%= u.getEmail() %></td>
            <td>
                <a href="users?action=edit&id=<%= u.getId() %>">Редагувати</a> |
                <a href="users?action=delete&id=<%= u.getId() %>"
                   onclick="return confirm('Видалити користувача?')">Видалити</a>
            </td>
        </tr>
        <%   }
        } %>
    </table>
</body>
</html>