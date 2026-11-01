<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="true" %>
<%@ page import="com.codequest.util.HtmlUtil" %>
<%@ page import="java.util.List" %>
<%--
    Module 8 - Student Dashboard (protected by StudentAuthFilter)
    All values are prepared by DashboardServlet and forwarded here.
--%>
<%
    // Opened directly (not through the servlet) -> load the data first
    if (request.getAttribute("totalXp") == null) {
        response.sendRedirect("dashboard");
        return;
    }

    String activePage = "dashboard";

    String error = (String) request.getAttribute("error");
    String username = (String) request.getAttribute("username");
    int totalXp = (Integer) request.getAttribute("totalXp");
    int level = (Integer) request.getAttribute("level");
    int completed = (Integer) request.getAttribute("completed");
    int totalMissions = (Integer) request.getAttribute("totalMissions");
    int rank = (Integer) request.getAttribute("rank");
    int nextLevel = (Integer) request.getAttribute("nextLevel");
    int xpNeeded = (Integer) request.getAttribute("xpNeeded");
    int percent = (Integer) request.getAttribute("progressPercent");
    String lastVisit = (String) request.getAttribute("lastVisit");

    // Each row: { title, category, isCorrect, xpReward, completedAt }
    @SuppressWarnings("unchecked")
    List<String[]> recentActivity = (List<String[]>) request.getAttribute("recentActivity");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CodeQuest - Dashboard</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<%@ include file="/WEB-INF/navbar.jspf" %>

<div class="container">
    <h1 class="page-title">Welcome, <%= HtmlUtil.esc(username) %></h1>
    <p class="subtitle">Your learning progress.</p>

    <% if (error != null) { %>
        <div class="alert alert-error"><%= HtmlUtil.esc(error) %></div>
    <% } %>
    <% if (lastVisit != null) { %>
        <div class="alert alert-info">Your last visit: <%= HtmlUtil.esc(lastVisit) %></div>
    <% } %>

    <div class="grid">
        <div class="card stat">
            <div class="value"><%= totalXp %></div>
            <div class="label">Total XP</div>
        </div>
        <div class="card stat">
            <div class="value"><%= level %></div>
            <div class="label">Level</div>
        </div>
        <div class="card stat">
            <div class="value"><%= completed %> / <%= totalMissions %></div>
            <div class="label">Missions Completed</div>
        </div>
        <div class="card stat">
            <div class="value">#<%= rank %></div>
            <div class="label">Global Rank</div>
        </div>
    </div>

    <div class="card">
        <h3>Progress to Level <%= nextLevel %></h3>
        <div class="progress">
            <div class="bar" style="width: <%= percent %>%;"></div>
        </div>
        <div class="progress-meta">
            <span><%= totalXp %> XP</span>
            <span><%= xpNeeded %> XP needed for Level <%= nextLevel %></span>
        </div>
    </div>

    <div class="card">
        <h3>Recent Activity</h3>
        <% if (recentActivity == null || recentActivity.isEmpty()) { %>
            <p class="empty">You have not attempted any missions yet.</p>
        <% } else { %>
            <div class="table-wrap">
                <table>
                    <tr>
                        <th>Mission</th>
                        <th>Category</th>
                        <th>Result</th>
                        <th>XP</th>
                        <th>Date</th>
                    </tr>
                    <% for (String[] row : recentActivity) {
                           boolean isCorrect = "true".equals(row[2]); %>
                        <tr>
                            <td><%= HtmlUtil.esc(row[0]) %></td>
                            <td><span class="badge badge-cat"><%= HtmlUtil.esc(row[1]) %></span></td>
                            <% if (isCorrect) { %>
                                <td class="status-done">Correct</td>
                                <td>+<%= HtmlUtil.esc(row[3]) %></td>
                            <% } else { %>
                                <td class="status-wrong">Wrong</td>
                                <td>0</td>
                            <% } %>
                            <td><%= HtmlUtil.esc(row[4]) %></td>
                        </tr>
                    <% } %>
                </table>
            </div>
        <% } %>
    </div>

    <a href="missions" class="btn btn-primary">Go to Missions</a>
</div>

<%@ include file="/WEB-INF/footer.jspf" %>

</body>
</html>
