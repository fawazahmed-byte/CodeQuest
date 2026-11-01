package com.codequest.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.codequest.util.DBConnection;
import com.codequest.util.HtmlUtil;
import com.codequest.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Module 3 - Student Login
 *
 * Concepts shown here:
 *  - HttpSession : stores userId and username after a successful login
 *  - Cookie      : "rememberUser" keeps the username for 7 days
 *                  when the "Remember me" box is ticked
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final int SEVEN_DAYS = 7 * 24 * 60 * 60;   // in seconds

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String identifier = request.getParameter("identifier");   // username OR email
        String password = request.getParameter("password");
        boolean remember = request.getParameter("remember") != null;

        if (HtmlUtil.isBlank(identifier) || HtmlUtil.isBlank(password)) {
            showError(request, response, "Please enter your username/email and password.");
            return;
        }
        identifier = identifier.trim();

        int userId = 0;
        String username = null;

        String sql = "SELECT id, username FROM users WHERE (username = ? OR email = ?) AND password = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, identifier);
            ps.setString(2, identifier.toLowerCase());   // emails are stored in lower case
            ps.setString(3, PasswordUtil.hash(password));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("id");
                    username = rs.getString("username");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError(request, response, "Database error: could not log in right now. Please try again later.");
            return;
        }

        if (username == null) {
            showError(request, response, "Invalid username/email or password.");
            return;
        }

        // ---------- Session ----------
        // Throw away any old session (for example an admin session)
        // and start a fresh one for this student.
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession session = request.getSession(true);
        session.setAttribute("userId", Integer.valueOf(userId));
        session.setAttribute("username", username);

        // ---------- Cookie ----------
        Cookie cookie = new Cookie("rememberUser", username);
        cookie.setHttpOnly(true);
        cookie.setPath(cookiePath(request));
        if (remember) {
            cookie.setMaxAge(SEVEN_DAYS);
        } else {
            cookie.setMaxAge(0);   // 0 = delete the cookie
        }
        response.addCookie(cookie);

        response.sendRedirect("dashboard");
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    /** Cookie path = the application's context path (for example /CodeQuest). */
    private String cookiePath(HttpServletRequest request) {
        String path = request.getContextPath();
        return path.isEmpty() ? "/" : path;
    }
}
