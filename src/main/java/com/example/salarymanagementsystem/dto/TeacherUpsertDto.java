package com.example.salarymanagementsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class TeacherUpsertDto {
    @NotNull(message = "Employee ID cannot be null")
    private Integer employeeId; // Teacher_number

    @NotBlank(message = "Employee name cannot be blank")
    private String employeeName;

    private Integer employeeAge;
    private String employeeGender;

    @NotBlank(message = "Department number cannot be blank")
    private String employeeDepartment; // Department_number (This should be the department's unique identifier, e.g., number)

    private String employeePhone;

    @Email(message = "Email should be valid")
    private String employeeEmail;

    private String employeePosition; // Post
    private String employeeDuty; // Job

    private String password; // For new teachers or password changes

    private List<MonthlyBasicSalaryDto> employeeBasic;
    private List<TeacherProjectAssignmentUpsertDto> employeeProjects;

    public static TeacherUpsertDto fromEntity(com.example.salarymanagementsystem.models.Teacher teacher) {
        TeacherUpsertDto dto = new TeacherUpsertDto();
        dto.setEmployeeId(teacher.getTeacherNumber());
        dto.setEmployeeName(teacher.getTeacherName());
        dto.setEmployeeAge(teacher.getTeacherAge());
        dto.setEmployeeGender(teacher.getTeacherGender());
        dto.setEmployeeDepartment(teacher.getDepartment() != null ? teacher.getDepartment().getDepartmentNumber() : null);
        dto.setEmployeePhone(teacher.getPhone());
        dto.setEmployeeEmail(teacher.getEmail());
        dto.setEmployeePosition(teacher.getPost());
        dto.setEmployeeDuty(teacher.getJob());
        // 密码、basic、projects不返回
        return dto;
    }
}
