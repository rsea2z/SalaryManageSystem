package com.example.salarymanagementsystem.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Teacher")
public class Teacher {

    @Id
    @Column(name = "Teacher_number", nullable = false)
    private Integer teacherNumber; // 教师编号, 作为登录用户名

    @Column(name = "Teacher_name", nullable = false)
    private String teacherName;

    @Column(name = "Password", nullable = false)
    private String password; // 加密后的密码

    @Column(name = "Teacher_age")
    private Integer teacherAge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Department_number", referencedColumnName = "Department_number")
    private Department department;

    @Column(name = "Post")
    private String post; // 职称

    @Column(name = "Teacher_gender")
    private String teacherGender;

    @Column(name = "Job")
    private String job; // 职务

    @Column(name = "Phone")
    private String phone;

    @Column(name = "Email")
    private String email;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Salary> salaries;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TeacherProjectAssignment> projectAssignments;

    @OneToMany(mappedBy = "leader", cascade = CascadeType.ALL)
    private Set<Project> ledProjects;

    @OneToOne(mappedBy = "administrator", cascade = CascadeType.ALL)
    private Department administeredDepartment;
}
