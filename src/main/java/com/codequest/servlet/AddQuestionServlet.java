package com.codequest.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import com.codequest.util.DBConnection;
import com.codequest.util.HtmlUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Module 6 - Add Question (admin only, protected by AdminAuthFilter)
 *
 * Declared in web.xml with init-params "defaultXp" and "maxXp".
 *
 * Concepts shown here:
 *  - ServletConfig  : getServletConfig().getInitParameter(...) in init()
 *  - ServletContext : "defaultXp" is stored so add-question.jsp can use it
 */
public class AddQuestionServlet extends HttpServlet {

    private static final List<String> CATEGORIES = Arrays.asList("Java", "SQL", "DSA");
    private static final List<String> DIFFICULTIES = Arrays.asList("Easy", "Medium", "Hard");

    private int defaultXp = 50;
    private int maxXp = 500;

    @Override
    public void init() throws ServletException {
        try {
            defaultXp = Integer.parseInt(getServletConfig().getInitParameter("defaultXp"));
            maxXp = Integer.parseInt(getServletConfig().getInitParameter("maxXp"));
        } catch (NumberFormatException e) {
            // keep the default values above if web.xml has a wrong value
        }

        // Share the default XP with the JSP page through the ServletContext
        getServletContext().setAttribute("defaultXp", Integer.valueOf(defaultXp));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("add-question.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String title = trim(request.getParameter("title"));
        String description = trim(request.getParameter("description"));
        String category = trim(request.getParameter("category"));
        String difficulty = trim(request.getParameter("difficulty"));
        String optionA = trim(request.getParameter("optionA"));
        String optionB = trim(request.getParameter("optionB"));
        String optionC = trim(request.getParameter("optionC"));
        String optionD = trim(request.getParameter("optionD"));
        String correctOption = trim(request.getParameter("correctOption"));
        String xpText = trim(request.getParameter("xpReward"));

        // ---------- Validation ----------
        if (HtmlUtil.isBlank(title) || HtmlUtil.isBlank(description)
                || HtmlUtil.isBlank(category) || HtmlUtil.isBlank(difficulty)
                || HtmlUtil.isBlank(optionA) || HtmlUtil.isBlank(optionB)
                || HtmlUtil.isBlank(optionC) || HtmlUtil.isBlank(optionD)
                || HtmlUtil.isBlank(correctOption) || HtmlUtil.isBlank(xpText)) {
            showError(request, response, "All fields are required.");
            return;
        }
        if (title.length() > 100) {
            showError(request, response, "Title must be at most 100 characters.");
            return;
        }
        if (optionA.length() > 255 || optionB.length() > 255
                || optionC.length() > 255 || optionD.length() > 255) {
            showError(request, response, "Each option must be at most 255 characters.");
            return;
        }
        if (!CATEGORIES.contains(category)) {
            showError(request, response, "Please choose a valid category (Java, SQL or DSA).");
            return;
        }
        if (!DIFFICULTIES.contains(difficulty)) {
            showError(request, response, "Please choose a valid difficulty (Easy, Medium or Hard).");
            return;
        }
        if (!Arrays.asList("A", "B", "C", "D").contains(correctOption)) {
            showError(request, response, "Please choose the correct option (A, B, C or D).");
            return;
        }

        // The four options must all be different (ignoring upper/lower case)
        String[] options = { optionA, optionB, optionC, optionD };
        for (int i = 0; i < options.length; i++) {
            for (int j = i + 1; j < options.length; j++) {
                if (options[i].equalsIgnoreCase(options[j])) {
                    showError(request, response, "All four options must be different.");
                    return;
                }
            }
        }

        int xpReward;
        try {
            xpReward = Integer.parseInt(xpText);
        } catch (NumberFormatException e) {
            showError(request, response, "XP reward must be a number.");
            return;
        }
        if (xpReward < 1 || xpReward > maxXp) {
            showError(request, response, "XP reward must be between 1 and " + maxXp + ".");
            return;
        }

        // Store the TEXT of the correct option, not the letter
        String correctAnswer;
        if (correctOption.equals("A")) {
            correctAnswer = optionA;
        } else if (correctOption.equals("B")) {
            correctAnswer = optionB;
        } else if (correctOption.equals("C")) {
            correctAnswer = optionC;
        } else {
            correctAnswer = optionD;
        }

        Integer adminId = (Integer) request.getSession().getAttribute("adminId");

        // ---------- Insert ----------
        String sql = "INSERT INTO missions (title, description, category, difficulty, "
                + "option_a, option_b, option_c, option_d, correct_answer, xp_reward, created_by) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, category);
            ps.setString(4, difficulty);
            ps.setString(5, optionA);
            ps.setString(6, optionB);
            ps.setString(7, optionC);
            ps.setString(8, optionD);
            ps.setString(9, correctAnswer);
            ps.setInt(10, xpReward);
            ps.setInt(11, adminId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            showError(request, response, "Database error: could not save the question. Please try again later.");
            return;
        }

        response.sendRedirect("add-question.jsp?success=Question+added+successfully.");
    }

    /** Re-shows the form. All fields are re-filled from request parameters in the JSP. */
    private void showError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("add-question.jsp").forward(request, response);
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
