<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="true" %>
<%@ page import="com.codequest.util.HtmlUtil" %>
<%--
    Module 10 - Mission Result (protected by StudentAuthFilter)
    Values are prepared by SubmitAnswerServlet and forwarded here.
--%>
<%
    Boolean correct = (Boolean) request.getAttribute("correct");

    // Opened directly (not after submitting an answer) -> back to missions
    if (correct == null) {
        response.sendRedirect("missions");
        return;
    }

    String activePage = "missions";

    String missionTitle = (String) request.getAttribute("missionTitle");
    String userAnswer = (String) request.getAttribute("userAnswer");
    String correctAnswer = (String) request.getAttribute("correctAnswer");
    int xpEarned = (Integer) request.getAttribute("xpEarned");
    int totalXp = (Integer) request.getAttribute("totalXp");
    int level = (Integer) request.getAttribute("level");
    boolean leveledUp = (Boolean) request.getAttribute("leveledUp");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CodeQuest - Mission Result</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<%@ include file="/WEB-INF/navbar.jspf" %>

<div class="container">

    <% if (correct) { %>
        <div class="card result correct">
            <h2>Correct!</h2>
            <p><%= HtmlUtil.esc(missionTitle) %></p>
            <p>You earned +<%= xpEarned %> XP</p>
            <% if (leveledUp) { %>
                <div class="levelup">Level Up! You are now Level <%= level %></div>
            <% } %>
            <p>Total XP: <%= totalXp %></p>
        </div>
    <% } else { %>
        <div class="card result wrong">
            <h2>Wrong Answer</h2>
            <p><%= HtmlUtil.esc(missionTitle) %></p>
            <p>Your answer: <%= HtmlUtil.esc(userAnswer) %></p>
            <p>Correct answer: <%= HtmlUtil.esc(correctAnswer) %></p>
            <p>No XP awarded for this mission.</p>
        </div>
    <% } %>

    <a href="missions" class="btn btn-primary">Next Mission</a>
    <a href="dashboard" class="btn btn-outline">Dashboard</a>
</div>

<%@ include file="/WEB-INF/footer.jspf" %>

</body>
</html>
