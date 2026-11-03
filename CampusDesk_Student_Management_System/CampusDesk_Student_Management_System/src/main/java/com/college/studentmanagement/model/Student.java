package com.college.studentmanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @NotNull(message = "Student ID is required")
    private Integer id;

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Name must be 100 characters or fewer")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Department is required")
    @Size(max = 50, message = "Department must be 50 characters or fewer")
    @Column(nullable = false, length = 50)
    private String department;

    @NotBlank(message = "Email address is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 120, message = "Email must be 120 characters or fewer")
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    public Student() {}

    public Student(Integer id, String name, String department, String email) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.email = email;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
