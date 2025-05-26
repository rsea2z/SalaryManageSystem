package com.example.salarymanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    private Integer projectNumber;
    private String projectName;
    private String completedOrNot;
    private String departmentName; // From Department entity
    private String leaderName; // From Teacher entity
    private Integer projectSalary; // This might be sum of assignments or a specific value
}
