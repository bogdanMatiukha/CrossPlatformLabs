<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.User" %>
<%@ page import="service.UserService" %>
<%
    String idParam = request.getParameter("id");
    User user = (User) request.getAttribute("user");

    if (user == null && idParam != null && !idParam.isBlank()) {
        UserService service = new UserService();
        user = service.getUserById(Integer.parseInt(idParam));
    }

    boolean edit = user != null && user.getId() > 0;
%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title><%= edit ? "Редагування користувача" : "Додавання користувача" %></title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <h1><%= edit ? "Редагування даних користувача" : "Новий користувач" %></h1>

    <form method="post" action="users">
        <input type="hidden" name="action" value="<%= edit ? "update" : "insert" %>">

        <% if (edit) { %>
            <input type="hidden" name="id" value="<%= user.getId() %>">
        <% } %>

        <label>ПІБ:</label>
        <input type="text" name="fullName"
               value="<%= user == null ? "" : user.getFullName() %>" required>

        <label>Роль:</label>
        <input type="text" name="role"
               value="<%= user == null ? "" : user.getRole() %>" required>

        <label>Email:</label>
        <input type="email" name="email"
               value="<%= user == null ? "" : user.getEmail() %>" required>

        <button type="submit">Зберегти</button>
        <a class="button" href="index.jsp?tab=users"
           style="background: #ccc; text-align: center; text-decoration: none; color: black;">
            Назад
        </a>
    </form>
</body>
</html>