<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="true" %>
<%@ page import="com.codequest.util.HtmlUtil" %>
<%@ page import="com.codequest.util.DBConnection" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%--
    Module 11 - Leaderboard (public page, guests can view it)
    Reads the latest XP directly from the database on every request.
--%>
<%
    String activePage = "leaderboard";

    // Never cache this page, so it always shows the latest XP
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

    // The logged-in student (if any) is highlighted
    Integer myId = (Integer) session.getAttribute("userId");

    // Each row: { id, username, level, completed, totalXp }
    List<String[]> players = new ArrayList<>();
    String error = null;

    String sql = "SELECT u.id, u.username, u.player_level, u.total_xp, "
            + "COUNT(s.id) AS completed "
            + "FROM users u "
            + "LEFT JOIN mission_submissions s ON s.user_id = u.id AND s.is_correct = TRUE "
            + "GROUP BY u.id, u.username, u.player_level, u.total_xp "
            + "ORDER BY u.total_xp DESC, completed DESC, u.username ASC";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            String[] row = new String[5];
            row[0] = String.valueOf(rs.getInt("id"));
            row[1] = rs.getString("username");
            row[2] = String.valueOf(rs.getInt("player_level"));
            row[3] = String.valueOf(rs.getInt("completed"));
            row[4] = String.valueOf(rs.getInt("total_xp"));
            players.add(row);
        }
    } catch (SQLException e) {
        error = "Database error: could not load the leaderboard.";
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CodeQuest - Leaderboard</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<%@ include file="/WEB-INF/navbar.jspf" %>

<div class="container">
    <h1 class="page-title">Leaderboard</h1>
    <p class="subtitle">Players ranked by total XP.</p>

    <% if (error != null) { %>
        <div class="alert alert-error"><%= HtmlUtil.esc(error) %></div>
    <% } %>

    <div class="card">
        <% if (players.isEmpty()) { %>
            <p class="empty">No players yet. Register and be the first on the leaderboard!</p>
        <% } else { %>
            <div class="table-wrap">
                <table>
                    <tr>
                        <th>Rank</th>
                        <th>Player</th>
                        <th>Level</th>
                        <th>Missions Completed</th>
                        <th>Total XP</th>
                    </tr>
                    <%
                        int rank = 0;
                        for (String[] p : players) {
                            rank++;

                            // CSS classes for the top 3 rows and for the student's own row
                            String rowClass = "";
                            String medal = "";
                            if (rank == 1) {
                                rowClass = "rank-1";
                                medal = "<span class=\"medal medal-gold\">GOLD</span>";
                            } else if (rank == 2) {
                                rowClass = "rank-2";
                                medal = "<span class=\"medal medal-silver\">SILVER</span>";
                            } else if (rank == 3) {
                                rowClass = "rank-3";
                                medal = "<span class=\"medal medal-bronze\">BRONZE</span>";
                            }
                            boolean isMe = myId != null && String.valueOf(myId).equals(p[0]);
                            if (isMe) {
                                rowClass = rowClass + " me";
                            }
                    %>
                        <tr class="<%= rowClass.trim() %>">
                            <td class="rank"><%= rank %><%= medal %></td>
                            <td><%= HtmlUtil.esc(p[1]) %><%= isMe ? " (You)" : "" %></td>
                            <td><%= HtmlUtil.esc(p[2]) %></td>
                            <td><%= HtmlUtil.esc(p[3]) %></td>
                            <td><strong><%= HtmlUtil.esc(p[4]) %></strong></td>
                        </tr>
                    <% } %>
                </table>
            </div>
        <% } %>
    </div>
</div>

<%@ include file="/WEB-INF/footer.jspf" %>

</body>
</html>
