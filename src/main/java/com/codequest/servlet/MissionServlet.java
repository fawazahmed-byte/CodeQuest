package com.codequest.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.codequest.model.Mission;
import com.codequest.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Module 9 - Missions (protected by StudentAuthFilter)
 *
 * Loads all missions (optionally filtered by category / difficulty)
 * together with this student's previous attempt, and forwards the list
 * to missions.jsp with a RequestDispatcher.
 *
 * The correct_answer column is NEVER selected here, so it can never
 * reach the student's browser.
 */
@WebServlet("/missions")
public class MissionServlet extends HttpServlet {

    private static final List<String> CATEGORIES = Arrays.asList("Java", "SQL", "DSA");
    private static final List<String> DIFFICULTIES = Arrays.asList("Easy", "Medium", "Hard");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int userId = (Integer) request.getSession().getAttribute("userId");

        // Only accept known filter values; anything else is ignored (null = no filter)
        String category = request.getParameter("category");
        if (!CATEGORIES.contains(category)) {
            category = null;
        }
        String difficulty = request.getParameter("difficulty");
        if (!DIFFICULTIES.contains(difficulty)) {
            difficulty = null;
        }

        List<Mission> missions = new ArrayList<>();

        String sql = "SELECT m.id, m.title, m.description, m.category, m.difficulty, "
                + "m.option_a, m.option_b, m.option_c, m.option_d, m.xp_reward, "
                + "s.is_correct, s.answer "
                + "FROM missions m "
                + "LEFT JOIN mission_submissions s ON s.mission_id = m.id AND s.user_id = ? "
                + "WHERE (? IS NULL OR m.category = ?) AND (? IS NULL OR m.difficulty = ?) "
                + "ORDER BY FIELD(m.category,'Java','SQL','DSA'), "
                + "FIELD(m.difficulty,'Easy','Medium','Hard'), m.id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, category);
            ps.setString(3, category);
            ps.setString(4, difficulty);
            ps.setString(5, difficulty);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Mission m = new Mission();
                    m.setId(rs.getInt("id"));
                    m.setTitle(rs.getString("title"));
                    m.setDescription(rs.getString("description"));
                    m.setCategory(rs.getString("category"));
                    m.setDifficulty(rs.getString("difficulty"));
                    m.setOptionA(rs.getString("option_a"));
                    m.setOptionB(rs.getString("option_b"));
                    m.setOptionC(rs.getString("option_c"));
                    m.setOptionD(rs.getString("option_d"));
                    m.setXpReward(rs.getInt("xp_reward"));

                    // s.answer is NULL when the student has not attempted this mission
                    String answer = rs.getString("answer");
                    if (answer != null) {
                        m.setStudentAnswer(answer);
                        m.setAttemptedCorrect(rs.getBoolean("is_correct"));
                    }
                    missions.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error: could not load missions.");
        }

        // Error message sent back by SubmitAnswerServlet (shown in red)
        if (request.getAttribute("error") == null && request.getParameter("error") != null) {
            request.setAttribute("error", request.getParameter("error"));
        }

        request.setAttribute("missions", missions);
        request.setAttribute("category", category);
        request.setAttribute("difficulty", difficulty);
        request.getRequestDispatcher("missions.jsp").forward(request, response);
    }
}
