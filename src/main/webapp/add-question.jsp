<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="true" %>
<%@ page import="com.codequest.util.HtmlUtil" %>
<%--
    Module 6 - Add Question form (protected by AdminAuthFilter)
    Submits to AddQuestionServlet (/addQuestion, declared in web.xml).
    After an error, every field is filled again from the request parameters.
--%>
<%!
    // Returns ' selected' when the option value matches the chosen value
    private String sel(String chosen, String value) {
        return value.equals(chosen) ? " selected" : "";
    }
%>
<%
    String activePage = "addQuestion";

    String error = (String) request.getAttribute("error");
    String success = request.getParameter("success");

    String title = request.getParameter("title");
    String description = request.getParameter("description");
    String category = request.getParameter("category");
    String difficulty = request.getParameter("difficulty");
    String optionA = request.getParameter("optionA");
    String optionB = request.getParameter("optionB");
    String optionC = request.getParameter("optionC");
    String optionD = request.getParameter("optionD");
    String correctOption = request.getParameter("correctOption");
    String xpReward = request.getParameter("xpReward");

    // Default XP comes from the ServletContext (set by AddQuestionServlet.init())
    if (xpReward == null) {
        Object defaultXp = application.getAttribute("defaultXp");
        xpReward = (defaultXp != null) ? defaultXp.toString() : "50";
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CodeQuest - Add Question</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<%@ include file="/WEB-INF/navbar.jspf" %>

<div class="container">
    <h1 class="page-title">Add Question</h1>
    <p class="subtitle">Create a new multiple-choice mission for students.</p>

    <div class="card">
        <% if (error != null) { %>
            <div class="alert alert-error"><%= HtmlUtil.esc(error) %></div>
        <% } %>
        <% if (success != null) { %>
            <div class="alert alert-success"><%= HtmlUtil.esc(success) %></div>
        <% } %>

        <form method="post" action="addQuestion">
            <div class="form-group">
                <label for="title">Title</label>
                <input type="text" id="title" name="title" maxlength="100"
                       value="<%= HtmlUtil.esc(title) %>" required>
            </div>
            <div class="form-group">
                <label for="description">Question</label>
                <textarea id="description" name="description" required><%= HtmlUtil.esc(description) %></textarea>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="category">Category</label>
                    <select id="category" name="category" required>
                        <option value="Java"<%= sel(category, "Java") %>>Java</option>
                        <option value="SQL"<%= sel(category, "SQL") %>>SQL</option>
                        <option value="DSA"<%= sel(category, "DSA") %>>DSA</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="difficulty">Difficulty</label>
                    <select id="difficulty" name="difficulty" required>
                        <option value="Easy"<%= sel(difficulty, "Easy") %>>Easy</option>
                        <option value="Medium"<%= sel(difficulty, "Medium") %>>Medium</option>
                        <option value="Hard"<%= sel(difficulty, "Hard") %>>Hard</option>
                    </select>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="optionA">Option A</label>
                    <input type="text" id="optionA" name="optionA" maxlength="255"
                           value="<%= HtmlUtil.esc(optionA) %>" required>
                </div>
                <div class="form-group">
                    <label for="optionB">Option B</label>
                    <input type="text" id="optionB" name="optionB" maxlength="255"
                           value="<%= HtmlUtil.esc(optionB) %>" required>
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label for="optionC">Option C</label>
                    <input type="text" id="optionC" name="optionC" maxlength="255"
                           value="<%= HtmlUtil.esc(optionC) %>" required>
                </div>
                <div class="form-group">
                    <label for="optionD">Option D</label>
                    <input type="text" id="optionD" name="optionD" maxlength="255"
                           value="<%= HtmlUtil.esc(optionD) %>" required>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="correctOption">Correct Answer</label>
                    <select id="correctOption" name="correctOption" required>
                        <option value="A"<%= sel(correctOption, "A") %>>Option A</option>
                        <option value="B"<%= sel(correctOption, "B") %>>Option B</option>
                        <option value="C"<%= sel(correctOption, "C") %>>Option C</option>
                        <option value="D"<%= sel(correctOption, "D") %>>Option D</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="xpReward">XP Reward</label>
                    <input type="number" id="xpReward" name="xpReward" min="1"
                           value="<%= HtmlUtil.esc(xpReward) %>" required>
                </div>
            </div>

            <button type="submit" class="btn btn-primary">Add Question</button>
            <a href="viewQuestions" class="btn btn-outline">View Questions</a>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/footer.jspf" %>

</body>
</html>
