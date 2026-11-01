package com.codequest.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.codequest.util.DBConnection;
import com.codequest.util.LevelUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Module 8 - Student Dashboard (protected by StudentAuthFilter)
 *
 * Loads the student's XP, level, completed missions, global rank and
 * recent activity, then forwards to dashboard.jsp with RequestDispatcher.
 *
 * Also demonstrates:
 *  - ServletContext : reads the context-param "xpPerLevel"
 *  - Cookie         : "lastVisit" remembers when the student last opened the dashboard
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final int THIRTY_DAYS = 30 * 24 * 60 * 60;   // in seconds

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        int userId = (Integer) session.getAttribute("userId");   // filter guarantees it exists

        int xpPerLevel = LevelUtil.parseXpPerLevel(getServletContext().getInitParameter("xpPerLevel"));

        String username = null;
        int totalXp = 0;
        int level = 1;
        int completed = 0;
        int totalMissions = 0;
        int rank = 0;

        // Each row: { title, category, isCorrect ("true"/"false"), xpReward, completedAt }
        List<String[]> recentActivity = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");

        try (Connection con = DBConnection.getConnection()) {

            // 1. Basic user details
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT username, total_xp, player_level FROM users WHERE id = ?")) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        username = rs.getString("username");
                        totalXp = rs.getInt("total_xp");
                        level = rs.getInt("player_level");
                    }
                }
            }

            // User was deleted from the database -> log out
            if (username == null) {
                session.invalidate();
                response.sendRedirect("login.jsp?msg=Your+account+was+not+found.+Please+log+in+again.");
                return;
            }

            // 2. Missions completed (correct answers)
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM mission_submissions WHERE user_id = ? AND is_correct = TRUE")) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        completed = rs.getInt(1);
                    }
                }
            }

            // 3. Total missions available
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM missions");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalMissions = rs.getInt(1);
                }
            }

            // 4. Global rank = number of players with more XP + 1
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) + 1 FROM users WHERE total_xp > ?")) {
                ps.setInt(1, totalXp);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        rank = rs.getInt(1);
                    }
                }
            }

            // 5. Last 5 submissions
            String activitySql = "SELECT m.title, m.category, s.is_correct, m.xp_reward, s.completed_at "
                    + "FROM mission_submissions s JOIN missions m ON m.id = s.mission_id "
                    + "WHERE s.user_id = ? ORDER BY s.completed_at DESC, s.id DESC LIMIT 5";
            try (PreparedStatement ps = con.prepareStatement(activitySql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String[] row = new String[5];
                        row[0] = rs.getString("title");
                        row[1] = rs.getString("category");
                        row[2] = String.valueOf(rs.getBoolean("is_correct"));
                        row[3] = String.valueOf(rs.getInt("xp_reward"));
                        row[4] = dateFormat.format(rs.getTimestamp("completed_at"));
                        recentActivity.add(row);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error: could not load your dashboard.");
        }

        // ---------- Cookie demo: last visit ----------
        String lastVisit = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("lastVisit")) {
                    try {
                        long millis = Long.parseLong(c.getValue());
                        lastVisit = dateFormat.format(new Date(millis));
                    } catch (NumberFormatException e) {
                        // ignore a broken cookie value
                    }
                }
            }
        }
        Cookie visitCookie = new Cookie("lastVisit", String.valueOf(System.currentTimeMillis()));
        visitCookie.setMaxAge(THIRTY_DAYS);
        visitCookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
        visitCookie.setHttpOnly(true);
        response.addCookie(visitCookie);

        // ---------- Level progress ----------
        int nextLevel = level + 1;
        int xpNeeded = LevelUtil.nextLevelXp(level, xpPerLevel) - totalXp;
        int percent = LevelUtil.progressPercent(totalXp, xpPerLevel);

        // ---------- Send everything to the JSP ----------
        request.setAttribute("username", username);
        request.setAttribute("totalXp", totalXp);
        request.setAttribute("level", level);
        request.setAttribute("completed", completed);
        request.setAttribute("totalMissions", totalMissions);
        request.setAttribute("rank", rank);
        request.setAttribute("nextLevel", nextLevel);
        request.setAttribute("xpNeeded", xpNeeded);
        request.setAttribute("progressPercent", percent);
        request.setAttribute("recentActivity", recentActivity);
        request.setAttribute("lastVisit", lastVisit);

        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
    }
}
