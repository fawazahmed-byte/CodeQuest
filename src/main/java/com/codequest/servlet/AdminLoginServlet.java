package com.codequest.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.codequest.util.DBConnection;
import com.codequest.util.HtmlUtil;
import com.codequest.util.PasswordUtil;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Module 4 - Admin Login
 *
 * Declared in web.xml (not with @WebServlet) so that it can have an init-param.
 *
 * Concepts shown here:
 *  - ServletConfig  : reads the init-param "dashboardPage" in init()
 *  - HttpSession    : stores adminId and adminUsername
 *  - ServletContext : stores "lastAdminLogin", shared by the whole application
 */
public class AdminLoginServlet extends HttpServlet {

    private String dashboardPage;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);   // required so that getServletContext() still works

        dashboardPage = config.getInitParameter("dashboardPage");
        if (dashboardPage == null) {
            dashboardPage = "admin-dashboard.jsp";
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("admin-login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (HtmlUtil.isBlank(username) || HtmlUtil.isBlank(password)) {
            showError(request, response, "Please enter the admin username and password.");
            return;
        }
        username = username.trim();

        int adminId = 0;
        String adminUsername = null;

        String sql = "SELECT id, username FROM admins WHERE username = ? AND password = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hash(password));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    adminId = rs.getInt("id");
                    adminUsername = rs.getString("username");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError(request, response, "Database error: could not log in right now. Please try again later.");
            return;
        }

        if (adminUsername == null) {
            showError(request, response, "Invalid admin username or password.");
            return;
        }

        // Start a fresh session for the admin (removes any student login)
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession session = request.getSession(true);
        session.setAttribute("adminId", Integer.valueOf(adminId));
        session.setAttribute("adminUsername", adminUsername);

        // ServletContext attribute: visible to every page of the application
        String time = new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(new Date());
        getServletContext().setAttribute("lastAdminLogin", adminUsername + " at " + time);

        response.sendRedirect(dashboardPage);
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("admin-login.jsp").forward(request, response);
    }
}
