<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.Project" %>
<%
    Project project = (Project) request.getAttribute("project");
    String error = (String) request.getAttribute("error");
    boolean edit = project != null && project.getId() > 0;
%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title><%= edit ? "Редагування проєкту" : "Додавання проєкту" %></title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <h1><%= edit ? "Редагування проєкту" : "Новий проєкт" %></h1>
    <% if (error != null) { %>
        <p class="error"><%= error %></p>
    <% } %>
    <form method="post" action="projects">
        <input type="hidden" name="action" value="<%= edit ? "update" : "insert" %>">
        <% if (edit) { %>
            <input type="hidden" name="id" value="<%= project.getId() %>">
        <% } %>

        <label>Назва проєкту:</label>
        <input name="name" value="<%= project == null ? "" : project.getName() %>">

        <label>Керівник:</label>
        <input name="manager" value="<%= project == null ? "" : project.getManager() %>">

        <label>Тривалість (днів):</label>
        <input name="duration" value="<%= project == null ? "" : project.getDuration() %>">

        <button type="submit">Зберегти</button>
        <a class="button" href="projects" style="background: #ccc; text-align: center; text-decoration: none; color: black;">Назад</a>
    </form>
</body>
</html>