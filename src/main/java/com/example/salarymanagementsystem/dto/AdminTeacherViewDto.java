package com.example.salarymanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminTeacherViewDto {
    private Integer teacherNumber;
    private String teacherName;
    private String teacherGender;
    private String teacherPosition; // Post
    private String teacherDuty; // Job
    private Integer teacherSalary; // Basic salary for the month or total
    private Integer projectSalary; // Project salary for the month or total
}
