<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="true" %>
<%@ page import="com.codequest.util.HtmlUtil" %>
<%@ page import="com.codequest.model.Mission" %>
<%@ page import="java.util.List" %>
<%--
    Module 9 - Missions (protected by StudentAuthFilter)
    The list is prepared by MissionServlet and forwarded here.
    The correct answer is never part of this page.
--%>
<%!
    // Builds a filter link such as "missions?category=Java&amp;difficulty=Easy"
    private String filterUrl(String category, String difficulty) {
        String url = "missions";
        if (category != null && difficulty != null) {
            url = url + "?category=" + category + "&amp;difficulty=" + difficulty;
        } else if (category != null) {
            url = url + "?category=" + category;
        } else if (difficulty != null) {
            url = url + "?difficulty=" + difficulty;
        }
        return url;
    }

    // Returns ' class="active"' when the filter value is the current one
    private String activeFilter(String current, String value) {
        if (current == null) {
            return value == null ? " class=\"active\"" : "";
        }
        return current.equals(value) ? " class=\"active\"" : "";
    }
%>
<%
    @SuppressWarnings("unchecked")
    List<Mission> missions = (List<Mission>) request.getAttribute("missions");

    // Opened directly (not through the servlet) -> load the data first
    if (missions == null) {
        response.sendRedirect("missions");
        return;
    }

    String activePage = "missions";
    String error = (String) request.getAttribute("error");

    // Current filters (already checked by MissionServlet, null = all)
    String category = (String) request.getAttribute("category");
    String difficulty = (String) request.getAttribute("difficulty");
    boolean noFilter = (category == null && difficulty == null);

    String[] categories = { "Java", "SQL", "DSA" };
    String[] difficulties = { "Easy", "Medium", "Hard" };
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CodeQuest - Missions</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<%@ include file="/WEB-INF/navbar.jspf" %>

<div class="container">
    <h1 class="page-title">Programming Missions</h1>
    <p class="subtitle">Select an answer and submit. Only one attempt is allowed per mission.</p>

    <% if (error != null) { %>
        <div class="alert alert-error"><%= HtmlUtil.esc(error) %></div>
    <% } %>

    <!-- Filters: a category link keeps the current difficulty and the other way round -->
    <div class="filters">
        <a href="missions"<%= noFilter ? " class=\"active\"" : "" %>>All</a>
        <% for (String c : categories) { %>
            <a href="<%= filterUrl(c, difficulty) %>"<%= activeFilter(category, c) %>><%= c %></a>
        <% } %>
        <span class="sep">|</span>
        <% for (String d : difficulties) { %>
            <a href="<%= filterUrl(category, d) %>"<%= activeFilter(difficulty, d) %>><%= d %></a>
        <% } %>
    </div>

    <% if (missions.isEmpty()) { %>
        <div class="card">
            <p class="empty">No missions available yet.</p>
        </div>
    <% } %>

    <% for (Mission m : missions) { %>
        <div class="card">
            <div class="mission-head">
                <h3><%= HtmlUtil.esc(m.getTitle()) %></h3>
                <div>
                    <span class="badge badge-cat"><%= HtmlUtil.esc(m.getCategory()) %></span>
                    <span class="badge badge-<%= HtmlUtil.esc(m.getDifficulty().toLowerCase()) %>"><%= HtmlUtil.esc(m.getDifficulty()) %></span>
                    <span class="badge badge-xp">+<%= m.getXpReward() %> XP</span>
                </div>
            </div>
            <p class="mission-desc"><%= HtmlUtil.esc(m.getDescription()) %></p>

            <% if (m.getAttemptedCorrect() == null) { %>
                <!-- Not attempted yet: show the answer form -->
                <form method="post" action="submitAnswer">
                    <input type="hidden" name="missionId" value="<%= m.getId() %>">
                    <div class="options">
                        <% for (String option : m.getOptions()) { %>
                            <label class="option">
                                <input type="radio" name="answer" value="<%= HtmlUtil.esc(option) %>" required>
                                <%= HtmlUtil.esc(option) %>
                            </label>
                        <% } %>
                    </div>
                    <button type="submit" class="btn btn-primary">Submit Answer</button>
                </form>
            <% } else if (m.getAttemptedCorrect()) { %>
                <p class="status-done">Completed - you earned <%= m.getXpReward() %> XP</p>
            <% } else { %>
                <p class="status-wrong">Attempted - wrong answer</p>
            <% } %>
        </div>
    <% } %>
</div>

<%@ include file="/WEB-INF/footer.jspf" %>

</body>
</html>
