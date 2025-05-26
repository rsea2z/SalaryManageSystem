package com.example.salarymanagementsystem.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Teacher_project_assignment")
@IdClass(TeacherProjectAssignmentId.class)
public class TeacherProjectAssignment {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_number", referencedColumnName = "teacher_number")
    private Teacher teacher;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_number", referencedColumnName = "project_number")
    private Project project;

    @Column(name = "Project_task", nullable = false)
    private String projectTask; // 项目分工描述

    @Column(name = "Project_salary")
    private Integer projectSalary; // 项目工资

    @Column(name = "Project_salary_addition")
    private Integer projectSalaryAddition; // 项目工资加扣

    @Column(name = "Comment")
    private String comment; // 备注
}
