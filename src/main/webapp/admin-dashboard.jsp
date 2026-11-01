<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="true" %>
<%@ page import="com.codequest.util.HtmlUtil" %>
<%@ page import="com.codequest.util.DBConnection" %>
<%@ page import="java.sql.*" %>
<%--
    Module 4 - Admin Dashboard (protected by AdminAuthFilter)
    Shows simple counts from the database and ServletContext information.
--%>
<%
    String activePage = "adminDashboard";

    int totalQuestions = 0;
    int totalStudents = 0;
    int totalSubmissions = 0;
    String error = null;

    try (Connection con = DBConnection.getConnection()) {
        try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM missions");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) totalQuestions = rs.getInt(1);
        }
        try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM users");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) totalStudents = rs.getInt(1);
        }
        try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM mission_submissions");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) totalSubmissions = rs.getInt(1);
        }
    } catch (SQLException e) {
        error = "Database error: could not load the statistics.";
    }

    // ServletContext values (implicit object "application")
    String appName = application.getInitParameter("appName");
    Object lastAdminLogin = application.getAttribute("lastAdminLogin");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CodeQuest - Admin Dashboard</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<%@ include file="/WEB-INF/navbar.jspf" %>

<div class="container">
    <h1 class="page-title">Admin Dashboard</h1>
    <p class="subtitle">Manage the programming missions of <%= HtmlUtil.esc(appName) %>.</p>

    <% if (error != null) { %>
        <div class="alert alert-error"><%= HtmlUtil.esc(error) %></div>
    <% } %>

    <div class="grid">
        <div class="card stat">
            <div class="value"><%= totalQuestions %></div>
            <div class="label">Total Questions</div>
        </div>
        <div class="card stat">
            <div class="value"><%= totalStudents %></div>
            <div class="label">Total Students</div>
        </div>
        <div class="card stat">
            <div class="value"><%= totalSubmissions %></div>
            <div class="label">Total Submissions</div>
        </div>
    </div>

    <div class="card">
        <h3>Quick Actions</h3>
        <p class="subtitle">
            ServletContext info - Application: <%= HtmlUtil.esc(appName) %>
            | Last admin login: <%= lastAdminLogin != null ? HtmlUtil.esc(lastAdminLogin) : "-" %>
        </p>
        <a href="add-question.jsp" class="btn btn-primary">Add Question</a>
        <a href="viewQuestions" class="btn btn-outline">View Questions</a>
        <a href="adminLogout" class="btn btn-outline">Logout</a>
    </div>
</div>

<%@ include file="/WEB-INF/footer.jspf" %>

</body>
</html>
