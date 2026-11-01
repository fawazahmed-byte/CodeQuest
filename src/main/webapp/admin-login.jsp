<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="true" %>
<%@ page import="com.codequest.util.HtmlUtil" %>
<%--
    Module 4 - Admin Login form
    Submits to AdminLoginServlet (/adminLogin, declared in web.xml).
--%>
<%
    String activePage = "adminLogin";

    String error = (String) request.getAttribute("error");
    String success = request.getParameter("success");
    String msg = request.getParameter("msg");
    String username = request.getParameter("username");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CodeQuest - Admin Login</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<%@ include file="/WEB-INF/navbar.jspf" %>

<div class="container narrow">
    <h1 class="page-title">Admin Login</h1>
    <p class="subtitle">For administrators only.</p>

    <div class="card">
        <% if (error != null) { %>
            <div class="alert alert-error"><%= HtmlUtil.esc(error) %></div>
        <% } %>
        <% if (success != null) { %>
            <div class="alert alert-success"><%= HtmlUtil.esc(success) %></div>
        <% } %>
        <% if (msg != null) { %>
            <div class="alert alert-info"><%= HtmlUtil.esc(msg) %></div>
        <% } %>

        <form method="post" action="adminLogin">
            <div class="form-group">
                <label for="username">Admin Username</label>
                <input type="text" id="username" name="username"
                       value="<%= HtmlUtil.esc(username) %>" required>
            </div>
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required>
            </div>
            <button type="submit" class="btn btn-primary btn-block">Login as Admin</button>
        </form>

        <p class="form-footer">Are you a student? <a href="login.jsp">Student login</a></p>
    </div>
</div>

<%@ include file="/WEB-INF/footer.jspf" %>

</body>
</html>
