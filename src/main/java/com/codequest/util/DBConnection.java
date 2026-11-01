package com.codequest.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Module 5 - Database
 *
 * One reusable class that gives a JDBC connection to the
 * codequest_db database. Every servlet and JSP calls
 * DBConnection.getConnection() inside a try-with-resources block,
 * so the connection is always closed automatically.
 */
public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/codequest_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata";

    private static final String USER = "root";

    // IMPORTANT: change this to YOUR OWN MySQL root password
    // before running the project.
    private static final String PASSWORD = "root123";

    // Load the MySQL JDBC driver once, when the class is first used.
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC driver not found: " + e.getMessage());
        }
    }

    /**
     * Opens a new connection to the database.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
