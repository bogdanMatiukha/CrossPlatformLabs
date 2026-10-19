<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.PropertyItem" %>
<%@ page import="service.PropertyService" %>
<%@ page import="status.ItemState" %>
<%
    String idParam = request.getParameter("id");
    PropertyItem property = (PropertyItem) request.getAttribute("property");

    if (property == null && idParam != null && !idParam.isBlank()) {
        PropertyService service = new PropertyService();
        property = service.getPropertyById(Integer.parseInt(idParam));
    }

    boolean edit = property != null && property.getId() > 0;
%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title><%= edit ? "Редагування майна" : "Додавання майна" %></title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <h1><%= edit ? "Редагування майна" : "Нова одиниця майна" %></h1>

    <form method="post" action="properties">
        <input type="hidden" name="action" value="<%= edit ? "update" : "insert" %>">

        <% if (edit) { %>
            <input type="hidden" name="id" value="<%= property.getId() %>">
        <% } %>

        <label>Інвентарний номер:</label>
        <input type="text" name="inventoryNumber"
               value="<%= property == null ? "" : property.getInventoryNumber() %>" required>

        <label>Назва:</label>
        <input type="text" name="name"
               value="<%= property == null ? "" : property.getName() %>" required>

        <label>Категорія:</label>
        <input type="text" name="category"
               value="<%= property == null ? "" : property.getCategory() %>" required>

        <label>Статус майна:</label>
        <select name="statusItem" required>
            <% for (ItemState status : ItemState.values()) {
                boolean selected = property != null
                        ? property.getStatusItem() == status
                        : status == ItemState.AVAILABLE;

                String statusLabel = switch (status.name()) {
                    case "AVAILABLE" -> "В наявності";
                    case "ISSUED" -> "Видано";
                    case "REPAIR" -> "На ремонті";
                    case "UNAVAILABLE" -> "Недоступне";
                    default -> status.name();
                };
            %>
                <option value="<%= status.name() %>" <%= selected ? "selected" : "" %>>
                    <%= status.name() %> (<%= statusLabel %>)
                </option>
            <% } %>
        </select>

        <button type="submit">Зберегти</button>
        <a class="button" href="index.jsp?tab=properties"
           style="background: #ccc; text-align: center; text-decoration: none; color: black;">
            Назад
        </a>
    </form>
</body>
</html>