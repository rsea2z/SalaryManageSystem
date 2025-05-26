package com.example.salarymanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherProjectAssignmentDto {
    private Integer teacherNumber;
    private String teacherName;
    private String departmentName; // Teacher's department
    private String projectTask;
    private Integer projectSalary;
    private Integer projectSalaryAddition;
    private String comment;
    private boolean isLeader; // Specific to project detail view
}
