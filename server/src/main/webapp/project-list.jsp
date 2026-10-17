<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Project" %>
<%
    List<Project> projects = (List<Project>) request.getAttribute("projects");
%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Облік проєктів</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <h1>Система обліку проєктів</h1>
    <p><a class="button" href="project-form.jsp">Додати новий проєкт</a></p>
    <table>
        <tr>
            <th>ID</th>
            <th>Назва проєкту</th>
            <th>Керівник</th>
            <th>Тривалість (днів)</th>
            <th>Дії</th>
        </tr>
        <% if (projects != null) {
            for (Project p : projects) { %>
        <tr>
            <td><%= p.getId() %></td>
            <td><%= p.getName() %></td>
            <td><%= p.getManager() %></td>
            <td><%= p.getDuration() %></td>
            <td>
                <a href="projects?action=edit&id=<%= p.getId() %>">Редагувати</a> |
                <a href="projects?action=delete&id=<%= p.getId() %>" onclick="return confirm('Видалити проєкт?')">Видалити</a>
            </td>
        </tr>
        <%   }
        } %>
    </table>
</body>
</html>