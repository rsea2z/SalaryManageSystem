package com.example.salarymanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpsertDto {
    @NotNull(message = "Project ID cannot be null")
    private Integer projectId;

    @NotBlank(message = "Project name cannot be blank")
    private String projectName;

    @NotNull(message = "Project manager ID cannot be null")
    private Integer projectManager; // Teacher_number

    @NotBlank(message = "Department number cannot be blank")
    private String department; // Department_number (This should be the department's unique identifier, e.g., number)

    @NotEmpty(message = "Project members cannot be empty")
    private List<ProjectMemberDto> projectMembers;

    private String completed; // "是" or "否"
    
    @NotNull(message = "Month cannot be null") // Added NotNull if month is mandatory
    private Integer month; // Month for the project
}
