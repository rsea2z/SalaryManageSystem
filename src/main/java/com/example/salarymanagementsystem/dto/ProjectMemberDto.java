package com.example.salarymanagementsystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberDto {
    @NotNull(message = "Teacher ID cannot be null")
    private Integer teacherId;

    private String task;

    private Integer bonus; // Corresponds to Project_salary in TeacherProjectAssignment
}
