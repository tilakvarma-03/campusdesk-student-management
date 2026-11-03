package com.college.studentmanagement.controller;

import com.college.studentmanagement.model.Student;
import com.college.studentmanagement.repository.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository repository;

    public StudentController(StudentRepository repository) {
        this.repository = repository;
    }

    // READ: list students, with optional search and department filters
    @GetMapping
    public List<Student> getStudents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department) {

        String term = search == null ? "" : search.trim().toLowerCase();
        String dept = department == null ? "" : department.trim();

        return repository.findAll().stream()
                .filter(student -> term.isEmpty()
                        || String.valueOf(student.getId()).contains(term)
                        || student.getName().toLowerCase().contains(term)
                        || student.getEmail().toLowerCase().contains(term)
                        || student.getDepartment().toLowerCase().contains(term))
                .filter(student -> dept.isEmpty()
                        || dept.equalsIgnoreCase(student.getDepartment()))
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .toList();
    }

    // READ: get one student
    @GetMapping("/{id}")
    public Student getStudent(@PathVariable Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Student not found"));
    }

    // CREATE
    @PostMapping
    public ResponseEntity<?> addStudent(@Valid @RequestBody Student student) {
        if (repository.existsById(student.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "A student with this ID already exists."));
        }
        if (repository.existsByEmailIgnoreCase(student.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "This email address is already registered."));
        }
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(student));
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Student ID or email already exists."));
        }
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(
            @PathVariable Integer id, @Valid @RequestBody Student updated) {

        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Student not found."));
        }
        if (!id.equals(updated.getId())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "The ID in the form must match the student being edited."));
        }
        if (repository.existsByEmailIgnoreCaseAndIdNot(updated.getEmail(), id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "This email address is already used by another student."));
        }

        return ResponseEntity.ok(repository.save(updated));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Dashboard statistics
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        List<Student> students = repository.findAll();
        Map<String, Long> departments = new LinkedHashMap<>();
        students.forEach(student ->
                departments.merge(student.getDepartment(), 1L, Long::sum));

        return Map.of(
                "totalStudents", students.size(),
                "departmentCount", departments.size(),
                "departments", departments
        );
    }
}
