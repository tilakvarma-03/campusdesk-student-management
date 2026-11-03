package com.college.studentmanagement.repository;

import com.college.studentmanagement.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Integer id);
    boolean existsByEmailIgnoreCase(String email);
}
