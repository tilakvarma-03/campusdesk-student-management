CREATE DATABASE IF NOT EXISTS student_db;
USE student_db;

CREATE TABLE IF NOT EXISTS students (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(50) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE
);

-- Optional demonstration record:
-- INSERT INTO students (id, name, department, email)
-- VALUES (101, 'Tilak', 'CSE', 'tilak@example.com');
