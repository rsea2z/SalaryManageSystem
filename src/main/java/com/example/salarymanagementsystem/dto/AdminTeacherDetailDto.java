package com.example.salarymanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminTeacherDetailDto {
    private Integer teacherNumber;
    private String teacherName;
    private Integer teacherAge;
    private String teacherGender;
    private String phone;
    private String email;
    private String departmentName;
    private String post;
    private String job;
    private List<ProjectAssignmentDetailDto> projects;
    private List<MonthlyBasicSalaryDto> monthlySalaries;
}
