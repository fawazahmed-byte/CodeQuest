<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="true" %>
<%@ page import="com.codequest.util.HtmlUtil" %>
<%--
    Module 3 - Student Login form
    Submits to LoginServlet (/login).
    Reads the "rememberUser" cookie to pre-fill the username.
--%>
<%
    String activePage = "login";

    String error = (String) request.getAttribute("error");
    String success = request.getParameter("success");
    String msg = request.getParameter("msg");

    // ---------- Cookie: remember me ----------
    String rememberedUser = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie c : cookies) {
            if (c.getName().equals("rememberUser")) {
                rememberedUser = c.getValue();
            }
        }
    }

    // After a failed login, show what the user typed; otherwise the remembered name
    String identifier = request.getParameter("identifier");
    if (identifier == null) {
        identifier = rememberedUser;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CodeQuest - Student Login</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<%@ include file="/WEB-INF/navbar.jspf" %>

<div class="container narrow">
    <h1 class="page-title">Student Login</h1>
    <p class="subtitle">Log in to continue your missions.</p>

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

        <form method="post" action="login">
            <div class="form-group">
                <label for="identifier">Username or Email</label>
                <input type="text" id="identifier" name="identifier"
                       value="<%= HtmlUtil.esc(identifier) %>" required>
            </div>
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required>
            </div>
            <label class="checkbox">
                <input type="checkbox" name="remember" <%= rememberedUser != null ? "checked" : "" %>>
                Remember me for 7 days
            </label>
            <button type="submit" class="btn btn-primary btn-block">Login</button>
        </form>

        <p class="form-footer">New here? <a href="register.jsp">Create an account</a></p>
    </div>
</div>

<%@ include file="/WEB-INF/footer.jspf" %>

</body>
</html>
