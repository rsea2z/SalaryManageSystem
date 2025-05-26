package com.example.salarymanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectAssignmentDetailDto {
    private Integer projectNumber;
    private String projectName;
    private String completedOrNot;
    private Integer projectSalary;
    private Integer projectSalaryAddition;
    private String projectTask;
    private String comment;
}
