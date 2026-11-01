package com.codequest.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.codequest.model.Mission;
import com.codequest.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Module 7 - View Questions (admin only, protected by AdminAuthFilter)
 *
 * Loads all missions from the database and forwards them to
 * view-questions.jsp using a RequestDispatcher.
 */
@WebServlet("/viewQuestions")
public class ViewQuestionsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Mission> questions = new ArrayList<>();

        String sql = "SELECT id, title, category, difficulty, xp_reward FROM missions ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Mission m = new Mission();
                m.setId(rs.getInt("id"));
                m.setTitle(rs.getString("title"));
                m.setCategory(rs.getString("category"));
                m.setDifficulty(rs.getString("difficulty"));
                m.setXpReward(rs.getInt("xp_reward"));
                questions.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error: could not load the questions.");
        }

        request.setAttribute("questions", questions);
        request.getRequestDispatcher("view-questions.jsp").forward(request, response);
    }
}
