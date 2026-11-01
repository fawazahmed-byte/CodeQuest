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
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Module 2 - Student Registration
 *
 * Validates the registration form, checks that the username and email
 * are not already used, then saves the new student with a hashed password.
 * Errors are shown on register.jsp itself (no separate error page).
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final String USERNAME_PATTERN = "[A-Za-z0-9_]{3,50}";
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    // Opening /register directly just shows the form
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("register.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // ---------- Validation (first error wins) ----------
        if (HtmlUtil.isBlank(username) || HtmlUtil.isBlank(email)
                || HtmlUtil.isBlank(password) || HtmlUtil.isBlank(confirmPassword)) {
            showError(request, response, "All fields are required.");
            return;
        }

        username = username.trim();
        email = email.trim().toLowerCase();

        if (!username.matches(USERNAME_PATTERN)) {
            showError(request, response,
                    "Username must be 3-50 characters and contain only letters, numbers and underscore.");
            return;
        }
        if (email.length() > 100 || !email.matches(EMAIL_PATTERN)) {
            showError(request, response, "Please enter a valid email address.");
            return;
        }
        if (password.length() < 6) {
            showError(request, response, "Password must be at least 6 characters long.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showError(request, response, "Password and Confirm Password do not match.");
            return;
        }

        // ---------- Database work ----------
        try (Connection con = DBConnection.getConnection()) {

            // 1. Check that username and email are not already used
            String checkSql = "SELECT username, email FROM users WHERE username = ? OR email = ?";
            try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                ps.setString(1, username);
                ps.setString(2, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (username.equalsIgnoreCase(rs.getString("username"))) {
                            showError(request, response, "Username '" + username + "' is already taken.");
                        } else {
                            showError(request, response, "This email is already registered.");
                        }
                        return;
                    }
                }
            }

            // 2. Insert the new student (starts with 0 XP at Level 1)
            String insertSql = "INSERT INTO users (username, email, password, total_xp, player_level) "
                    + "VALUES (?, ?, ?, 0, 1)";
            try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setString(1, username);
                ps.setString(2, email);
                ps.setString(3, PasswordUtil.hash(password));
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showError(request, response, "Database error: could not register right now. Please try again later.");
            return;
        }

        // Post-Redirect-Get: go to the login page with a success message
        response.sendRedirect("login.jsp?success=Registration+successful!+Please+log+in.");
    }

    /** Puts the error message in the request and shows the form again. */
    private void showError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }
}
