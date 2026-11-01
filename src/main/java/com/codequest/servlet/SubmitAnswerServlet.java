package com.codequest.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import com.codequest.util.DBConnection;
import com.codequest.util.HtmlUtil;
import com.codequest.util.LevelUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Module 10 - Submit Answer (protected by StudentAuthFilter)
 *
 * Checks the student's answer and saves it using a JDBC TRANSACTION:
 * saving the submission and adding XP must both succeed, or both are
 * undone (rollback). Only one attempt is allowed per mission.
 * The result is shown on mission-result.jsp using a RequestDispatcher.
 */
@WebServlet("/submitAnswer")
public class SubmitAnswerServlet extends HttpServlet {

    private static final String ALREADY_ATTEMPTED =
            "missions?error=You+have+already+attempted+this+mission.";

    // Answers can only be submitted with the form (POST)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("missions");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        int userId = (Integer) request.getSession().getAttribute("userId");
        String answer = request.getParameter("answer");

        if (HtmlUtil.isBlank(answer)) {
            response.sendRedirect("missions?error=Please+select+an+answer+before+submitting.");
            return;
        }

        int missionId;
        try {
            missionId = Integer.parseInt(request.getParameter("missionId"));
        } catch (NumberFormatException e) {
            response.sendRedirect("missions?error=Invalid+mission.");
            return;
        }

        // Values for the result page
        String missionTitle;
        String correctAnswer;
        boolean correct;
        int xpEarned = 0;
        int totalXp = 0;
        int level = 1;
        boolean leveledUp = false;

        int xpPerLevel = LevelUtil.parseXpPerLevel(getServletContext().getInitParameter("xpPerLevel"));

        try (Connection con = DBConnection.getConnection()) {
            try {
                // ---------- Start the transaction ----------
                con.setAutoCommit(false);

                // Step 1: load the mission
                String[] options = new String[4];
                int xpReward;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT title, option_a, option_b, option_c, option_d, correct_answer, xp_reward "
                        + "FROM missions WHERE id = ?")) {
                    ps.setInt(1, missionId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            response.sendRedirect("missions?error=This+mission+does+not+exist.");
                            return;
                        }
                        missionTitle = rs.getString("title");
                        options[0] = rs.getString("option_a");
                        options[1] = rs.getString("option_b");
                        options[2] = rs.getString("option_c");
                        options[3] = rs.getString("option_d");
                        correctAnswer = rs.getString("correct_answer");
                        xpReward = rs.getInt("xp_reward");
                    }
                }

                // Step 2: the answer must be one of the four options
                boolean validOption = false;
                for (String option : options) {
                    if (option.equals(answer)) {
                        validOption = true;
                    }
                }
                if (!validOption) {
                    con.rollback();
                    response.sendRedirect("missions?error=Invalid+answer.+Please+choose+one+of+the+options.");
                    return;
                }

                // Step 3: one attempt per mission (correct OR wrong)
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT id FROM mission_submissions WHERE user_id = ? AND mission_id = ? FOR UPDATE")) {
                    ps.setInt(1, userId);
                    ps.setInt(2, missionId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            con.rollback();
                            response.sendRedirect(ALREADY_ATTEMPTED);
                            return;
                        }
                    }
                }

                // Step 4: save the submission
                correct = correctAnswer.equals(answer);
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO mission_submissions (user_id, mission_id, answer, is_correct) "
                        + "VALUES (?, ?, ?, ?)")) {
                    ps.setInt(1, userId);
                    ps.setInt(2, missionId);
                    ps.setString(3, answer);
                    ps.setBoolean(4, correct);
                    ps.executeUpdate();
                }

                // Step 5: if correct, add XP and recalculate the level
                if (correct) {
                    int oldLevel = 1;
                    try (PreparedStatement ps = con.prepareStatement(
                            "SELECT total_xp, player_level FROM users WHERE id = ? FOR UPDATE")) {
                        ps.setInt(1, userId);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                totalXp = rs.getInt("total_xp");
                                oldLevel = rs.getInt("player_level");
                            }
                        }
                    }

                    xpEarned = xpReward;
                    totalXp = totalXp + xpEarned;
                    level = LevelUtil.levelFor(totalXp, xpPerLevel);
                    leveledUp = level > oldLevel;

                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE users SET total_xp = ?, player_level = ? WHERE id = ?")) {
                        ps.setInt(1, totalXp);
                        ps.setInt(2, level);
                        ps.setInt(3, userId);
                        ps.executeUpdate();
                    }
                }

                // Step 6: everything worked -> save permanently
                con.commit();

            } catch (SQLException e) {
                con.rollback();   // undo every change made in this transaction
                throw e;          // handled by the outer catch blocks below
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            // UNIQUE (user_id, mission_id) stopped a second attempt
            response.sendRedirect(ALREADY_ATTEMPTED);
            return;
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("missions?error=Database+error:+could+not+save+your+answer.+Please+try+again.");
            return;
        }

        // ---------- Show the result ----------
        request.setAttribute("correct", Boolean.valueOf(correct));
        request.setAttribute("missionTitle", missionTitle);
        request.setAttribute("userAnswer", answer);
        request.setAttribute("correctAnswer", correctAnswer);
        request.setAttribute("xpEarned", xpEarned);
        request.setAttribute("totalXp", totalXp);
        request.setAttribute("level", level);
        request.setAttribute("leveledUp", Boolean.valueOf(leveledUp));
        request.getRequestDispatcher("mission-result.jsp").forward(request, response);
    }
}
