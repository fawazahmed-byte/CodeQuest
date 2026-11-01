<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="true" %>
<%@ page import="com.codequest.util.HtmlUtil" %>
<%--
    Module 2 - Student Registration form
    Submits to RegisterServlet (/register). Errors are shown on this page.
--%>
<%
    String activePage = "register";

    String error = (String) request.getAttribute("error");
    // Keep what the user typed when the form is shown again after an error
    String username = request.getParameter("username");
    String email = request.getParameter("email");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CodeQuest - Register</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<%@ include file="/WEB-INF/navbar.jspf" %>

<div class="container narrow">
    <h1 class="page-title">Create Account</h1>
    <p class="subtitle">Register as a student to start solving missions.</p>

    <div class="card">
        <% if (error != null) { %>
            <div class="alert alert-error"><%= HtmlUtil.esc(error) %></div>
        <% } %>

        <form method="post" action="register">
            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" maxlength="50"
                       value="<%= HtmlUtil.esc(username) %>" required>
            </div>
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" maxlength="100"
                       value="<%= HtmlUtil.esc(email) %>" required>
            </div>
            <div class="form-group">
                <label for="password">Password (at least 6 characters)</label>
                <input type="password" id="password" name="password" required>
            </div>
            <div class="form-group">
                <label for="confirmPassword">Confirm Password</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required>
            </div>
            <button type="submit" class="btn btn-primary btn-block">Register</button>
        </form>

        <p class="form-footer">Already have an account? <a href="login.jsp">Log in here</a></p>
    </div>
</div>

<%@ include file="/WEB-INF/footer.jspf" %>

</body>
</html>
