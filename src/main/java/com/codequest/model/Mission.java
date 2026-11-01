package com.codequest.model;

/**
 * Module 7 / 9 - View Questions and Missions
 *
 * Simple bean that holds one mission (question) row.
 *
 * NOTE: This class deliberately does NOT have a correctAnswer field.
 * Mission objects are sent to missions.jsp, and the correct answer must
 * never reach the student's browser (otherwise a student could read it
 * from the page source). The answer is only checked on the server
 * inside SubmitAnswerServlet.
 */
public class Mission {

    private int id;
    private String title;
    private String description;
    private String category;
    private String difficulty;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private int xpReward;

    // Only used on the student missions page:
    // null = not attempted, TRUE = answered correctly, FALSE = answered wrong
    private Boolean attemptedCorrect;
    private String studentAnswer;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }

    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }

    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }

    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }

    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }

    public Boolean getAttemptedCorrect() { return attemptedCorrect; }
    public void setAttemptedCorrect(Boolean attemptedCorrect) { this.attemptedCorrect = attemptedCorrect; }

    public String getStudentAnswer() { return studentAnswer; }
    public void setStudentAnswer(String studentAnswer) { this.studentAnswer = studentAnswer; }

    /** The four options as an array, so the JSP can loop over them. */
    public String[] getOptions() {
        return new String[] { optionA, optionB, optionC, optionD };
    }
}
