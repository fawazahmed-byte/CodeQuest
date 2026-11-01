# CodeQuest - Programming Learning Game

CodeQuest is a web-based game for learning programming, built as a college **Java Full Stack** project.
Students answer multiple-choice programming questions ("missions") in **Java, SQL and DSA**, earn **XP** for
correct answers, level up and compete on a **leaderboard**. An **admin** adds and manages the questions.
All data is stored in **MySQL** and loaded dynamically.

## Features

- Student registration with validation (username, email, password rules, duplicate check)
- Student login with username **or** email, "Remember me" cookie, logout
- Separate admin login, admin dashboard with live statistics
- Admin can add multiple-choice questions and view all questions
- Missions page with category (Java / SQL / DSA) and difficulty (Easy / Medium / Hard) filters
- One attempt per mission; correct answers give XP; every 100 XP = next level
- Result page with "Level Up" message
- Student dashboard: XP, level, completed missions, global rank, progress bar, recent activity
- Public leaderboard with gold / silver / bronze highlighting
- Passwords stored as SHA-256 hashes; all SQL uses `PreparedStatement`; all output is HTML-escaped

## Tech Stack

| Layer      | Technology                                  |
|------------|---------------------------------------------|
| Frontend   | HTML, CSS, JSP (scriptlets only, no JSTL/EL)|
| Backend    | Java Servlets (Jakarta Servlet 6.0)         |
| Database   | MySQL 8 with JDBC (MySQL Connector/J)       |
| Server     | Apache Tomcat 10.1                          |
| Build      | Maven (WAR), JDK 17                         |

## Database (`codequest_db`)

| Table                 | Purpose                                                        |
|-----------------------|----------------------------------------------------------------|
| `users`               | Students with username, email, password hash, total XP, level  |
| `admins`              | Administrator credentials (password hash)                      |
| `missions`            | Questions: title, description, category, difficulty, 4 options, correct answer, XP |
| `mission_submissions` | Student answers; `UNIQUE (user_id, mission_id)` = one attempt per mission |

**Default admin login:** username `admin`, password `admin123`

## Folder Structure

```
CodeQuest/
├── pom.xml
├── README.md
├── database/
│   └── schema.sql
└── src/main/
    ├── java/com/codequest/
    │   ├── util/      DBConnection, PasswordUtil, HtmlUtil, LevelUtil
    │   ├── model/     Mission
    │   ├── filter/    StudentAuthFilter, AdminAuthFilter
    │   └── servlet/   Register, Login, Logout, AdminLogin, AdminLogout,
    │                  AddQuestion, ViewQuestions, Dashboard, Mission, SubmitAnswer
    └── webapp/
        ├── index.html, register.jsp, login.jsp, admin-login.jsp,
        ├── admin-dashboard.jsp, add-question.jsp, view-questions.jsp,
        ├── dashboard.jsp, missions.jsp, mission-result.jsp, leaderboard.jsp
        ├── css/style.css
        └── WEB-INF/   web.xml, navbar.jspf, footer.jspf
```

## Module -> Files

| Module                     | Files |
|----------------------------|-------|
| 1. Home                    | `index.html` |
| 2. Registration            | `register.jsp`, `RegisterServlet.java`, `PasswordUtil.java` |
| 3. Student Login / Logout  | `login.jsp`, `LoginServlet.java`, `LogoutServlet.java`, `StudentAuthFilter.java` |
| 4. Admin Login / Dashboard | `admin-login.jsp`, `admin-dashboard.jsp`, `AdminLoginServlet.java`, `AdminLogoutServlet.java`, `AdminAuthFilter.java` |
| 5. Database                | `database/schema.sql`, `DBConnection.java` |
| 6. Add Question            | `add-question.jsp`, `AddQuestionServlet.java` |
| 7. View Questions          | `view-questions.jsp`, `ViewQuestionsServlet.java`, `Mission.java` |
| 8. Student Dashboard       | `dashboard.jsp`, `DashboardServlet.java`, `LevelUtil.java` |
| 9. Missions                | `missions.jsp`, `MissionServlet.java` |
| 10. Submit Answer          | `mission-result.jsp`, `SubmitAnswerServlet.java` |
| 11. Leaderboard            | `leaderboard.jsp` |
| 12. Integration            | `web.xml`, `navbar.jspf`, `footer.jspf`, `style.css`, `HtmlUtil.java` |

## How to Run

1. Install **JDK 17**, **Apache Tomcat 10.1**, **MySQL 8** + MySQL Workbench (and Maven, or an IDE with Maven support).
2. Open `database/schema.sql` in MySQL Workbench and run the whole script. It creates `codequest_db`,
   the 4 tables, the default admin and some sample missions.
3. Open `src/main/java/com/codequest/util/DBConnection.java` and change `PASSWORD` to your own MySQL password.
4. Build and deploy:
   - **Eclipse:** File -> Import -> Maven -> Existing Maven Projects -> select the `CodeQuest` folder.
     Then right-click the project -> Run As -> Run on Server -> Apache Tomcat v10.1.
   - **VS Code / command line:** run `mvn clean package`, copy `target/CodeQuest.war` into Tomcat's
     `webapps` folder, then start Tomcat (`bin/startup.bat` on Windows, `bin/startup.sh` on Linux/Mac).
5. Open **http://localhost:8080/CodeQuest/** in a browser.

## Where Each Concept Is Used

| Concept              | Where |
|----------------------|-------|
| HttpSession          | `LoginServlet`, `AdminLoginServlet` (set attributes), `StudentAuthFilter`, `AdminAuthFilter` (check them) |
| Session invalidation | `LogoutServlet`, `AdminLogoutServlet` |
| Cookies              | `LoginServlet` (`rememberUser`, read in `login.jsp`), `DashboardServlet` (`lastVisit`) |
| ServletConfig        | `AdminLoginServlet` (`dashboardPage`), `AddQuestionServlet` (`defaultXp`, `maxXp`) |
| ServletContext       | `web.xml` context-params (`appName`, `xpPerLevel`), `lastAdminLogin` attribute, `defaultXp` attribute |
| RequestDispatcher    | `ViewQuestionsServlet`, `DashboardServlet`, `MissionServlet`, `SubmitAnswerServlet` (forward to JSP) |
| Transactions         | `SubmitAnswerServlet` (commit / rollback around saving the answer and adding XP) |
| PreparedStatement    | Every database query in every servlet and JSP |
| Filters              | `StudentAuthFilter`, `AdminAuthFilter` (mapped in `web.xml`) |
