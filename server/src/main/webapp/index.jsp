<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String tab = request.getParameter("tab");
    if (tab == null || tab.isBlank()) {
        tab = "requests";
    }
%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Система обліку університетського майна</title>
    <link rel="stylesheet" href="style.css">
    <style>
        .navbar {
            background: #1e293b;
            padding: 12px 20px;
            border-radius: 6px;
            margin-bottom: 20px;
        }
        .navbar a {
            color: #fff;
            text-decoration: none;
            margin-right: 15px;
            padding: 8px 14px;
            border-radius: 4px;
            font-weight: 500;
            display: inline-block;
        }
        .navbar a:hover, .navbar a.active {
            background: #3b82f6;
        }
    </style>
</head>
<body>
    <h1>Система видачі та повернення університетського майна</h1>

    <div class="navbar">
        <a href="index.jsp?tab=requests" class="<%= "requests".equals(tab) ? "active" : "" %>">Заявки</a>
        <a href="index.jsp?tab=users" class="<%= "users".equals(tab) ? "active" : "" %>">Користувачі</a>
        <a href="index.jsp?tab=properties" class="<%= "properties".equals(tab) ? "active" : "" %>">Майно</a>
        <a href="index.jsp?tab=transfers" class="<%= "transfers".equals(tab) ? "active" : "" %>">Передачі</a>
    </div>

    <div class="content">
        <%
            if ("users".equals(tab)) {
        %>
                <jsp:include page="user-list.jsp" />
        <%  } else if ("properties".equals(tab)) { %>
                <jsp:include page="property-list.jsp" />
        <%  } else if ("transfers".equals(tab)) { %>
                <jsp:include page="transfer-list.jsp" />
        <%  } else { %>
                <jsp:include page="request-list.jsp" />
        <%  } %>
    </div>
</body>
</html>