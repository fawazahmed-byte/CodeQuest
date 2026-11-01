-- =========================================================
-- CodeQuest - Programming Learning Game
-- Database script (Module 5)
-- Run this whole file in MySQL Workbench or the MySQL CLI:
--   mysql -u root -p < schema.sql
-- =========================================================

DROP DATABASE IF EXISTS codequest_db;
CREATE DATABASE codequest_db;
USE codequest_db;

-- ---------------------------------------------------------
-- Table 1: users  (students, their XP and level)
-- ---------------------------------------------------------
CREATE TABLE users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,          -- SHA-256 hash of the password
    total_xp      INT NOT NULL DEFAULT 0,
    player_level  INT NOT NULL DEFAULT 1,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- Table 2: admins  (administrator credentials)
-- ---------------------------------------------------------
CREATE TABLE admins (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50)  NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL                -- SHA-256 hash of the password
);

-- ---------------------------------------------------------
-- Table 3: missions  (programming questions created by admin)
-- ---------------------------------------------------------
CREATE TABLE missions (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(100) NOT NULL,
    description     TEXT NOT NULL,
    category        VARCHAR(30)  NOT NULL,         -- Java, SQL, DSA
    difficulty      VARCHAR(20)  NOT NULL,         -- Easy, Medium, Hard
    option_a        VARCHAR(255) NOT NULL,
    option_b        VARCHAR(255) NOT NULL,
    option_c        VARCHAR(255) NOT NULL,
    option_d        VARCHAR(255) NOT NULL,
    correct_answer  VARCHAR(255) NOT NULL,         -- stores the TEXT of the correct option
    xp_reward       INT NOT NULL DEFAULT 50,
    created_by      INT,
    CONSTRAINT fk_missions_admin FOREIGN KEY (created_by)
        REFERENCES admins(id) ON DELETE SET NULL
);

-- ---------------------------------------------------------
-- Table 4: mission_submissions  (student answers)
-- One attempt per mission -> UNIQUE (user_id, mission_id)
-- ---------------------------------------------------------
CREATE TABLE mission_submissions (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT NOT NULL,
    mission_id    INT NOT NULL,
    answer        VARCHAR(255) NOT NULL,
    is_correct    BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sub_user    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
    CONSTRAINT fk_sub_mission FOREIGN KEY (mission_id) REFERENCES missions(id) ON DELETE CASCADE,
    CONSTRAINT uq_one_attempt UNIQUE (user_id, mission_id)
);

-- ---------------------------------------------------------
-- Default admin account
--   username: admin
--   password: admin123   (stored as SHA-256 hash)
-- ---------------------------------------------------------
INSERT INTO admins (username, password)
VALUES ('admin', SHA2('admin123', 256));

-- ---------------------------------------------------------
-- Sample missions (so the app is not empty on first run).
-- The admin can add more from the Add Question page.
-- ---------------------------------------------------------
INSERT INTO missions (title, description, category, difficulty, option_a, option_b, option_c, option_d, correct_answer, xp_reward, created_by) VALUES
('Java Entry Point',
 'Which method signature is the entry point of a standalone Java application?',
 'Java', 'Easy',
 'public void main(String args)', 'public static void main(String[] args)', 'static int main()', 'public main(String[] args)',
 'public static void main(String[] args)', 50, 1),

('String Comparison',
 'Which method should be used to compare the contents of two String objects in Java?',
 'Java', 'Easy',
 '==', 'equals()', 'compare()', 'same()',
 'equals()', 50, 1),

('HashMap Keys',
 'How many null keys can a java.util.HashMap contain?',
 'Java', 'Medium',
 'None', 'Exactly one', 'Unlimited', 'Two',
 'Exactly one', 75, 1),

('Select All Rows',
 'Which SQL statement returns every column and every row from a table named students?',
 'SQL', 'Easy',
 'GET * FROM students', 'SELECT ALL students', 'SELECT * FROM students', 'SHOW students',
 'SELECT * FROM students', 50, 1),

('Filtering Groups',
 'Which SQL clause is used to filter the results of a GROUP BY?',
 'SQL', 'Medium',
 'WHERE', 'HAVING', 'ORDER BY', 'LIMIT',
 'HAVING', 75, 1),

('Binary Search Complexity',
 'What is the time complexity of binary search on a sorted array of n elements?',
 'DSA', 'Easy',
 'O(n)', 'O(log n)', 'O(n log n)', 'O(1)',
 'O(log n)', 50, 1),

('Stack Principle',
 'Which principle does a Stack data structure follow?',
 'DSA', 'Easy',
 'FIFO', 'LIFO', 'Random access', 'Priority based',
 'LIFO', 50, 1),

('Worst Case Quick Sort',
 'What is the worst-case time complexity of Quick Sort?',
 'DSA', 'Hard',
 'O(n log n)', 'O(n)', 'O(n^2)', 'O(log n)',
 'O(n^2)', 100, 1);
