package com.example.salarymanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherProjectAssignmentUpsertDto {
    private Integer projectId;
    // projectName is not strictly needed for update if projectId is present, but good for context
    private String projectName;
    private String task;
    private Integer bonus; // Project_salary in TeacherProjectAssignment
    private String completed; // completed_or_not from Project
    private Integer extraBonus; // Project_salary_addition
    private String extraBonusReason; // Comment
}
