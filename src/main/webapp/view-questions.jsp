<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="true" %>
<%@ page import="com.codequest.util.HtmlUtil" %>
<%@ page import="com.codequest.model.Mission" %>
<%@ page import="java.util.List" %>
<%--
    Module 7 - View Questions (protected by AdminAuthFilter)
    The list is prepared by ViewQuestionsServlet and forwarded here.
--%>
<%
    @SuppressWarnings("unchecked")
    List<Mission> questions = (List<Mission>) request.getAttribute("questions");

    // Opened directly (not through the servlet) -> load the data first
    if (questions == null) {
        response.sendRedirect("viewQuestions");
        return;
    }

    String activePage = "viewQuestions";
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CodeQuest - View Questions</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<%@ include file="/WEB-INF/navbar.jspf" %>

<div class="container">
    <h1 class="page-title">All Questions</h1>
    <p class="subtitle"><%= questions.size() %> missions in the database.</p>

    <% if (error != null) { %>
        <div class="alert alert-error"><%= HtmlUtil.esc(error) %></div>
    <% } %>

    <div class="card">
        <% if (questions.isEmpty()) { %>
            <p class="empty">No questions yet. Use Add Question to create the first one.</p>
        <% } else { %>
            <div class="table-wrap">
                <table>
                    <tr>
                        <th>#</th>
                        <th>Title</th>
                        <th>Category</th>
                        <th>Difficulty</th>
                        <th>XP</th>
                    </tr>
                    <% for (Mission m : questions) { %>
                        <tr>
                            <td><%= m.getId() %></td>
                            <td><%= HtmlUtil.esc(m.getTitle()) %></td>
                            <td><span class="badge badge-cat"><%= HtmlUtil.esc(m.getCategory()) %></span></td>
                            <td><span class="badge badge-<%= HtmlUtil.esc(m.getDifficulty().toLowerCase()) %>"><%= HtmlUtil.esc(m.getDifficulty()) %></span></td>
                            <td><%= m.getXpReward() %></td>
                        </tr>
                    <% } %>
                </table>
            </div>
        <% } %>
    </div>

    <a href="add-question.jsp" class="btn btn-primary">Add Question</a>
</div>

<%@ include file="/WEB-INF/footer.jspf" %>

</body>
</html>
