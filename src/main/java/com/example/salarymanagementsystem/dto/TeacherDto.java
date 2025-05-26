package com.example.salarymanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDto {
    private Integer teacherNumber;
    private String teacherName;
    private Integer teacherAge;
    private String departmentNumber;
    private String departmentName;
    private String post; // 职称
    private String teacherGender;
    private String job; // 职务
    private String phone;
    private String email;
    private boolean isAdmin;
}
