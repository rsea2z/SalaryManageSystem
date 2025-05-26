package com.example.salarymanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalaryDto {
    private Integer teacherNumber;
    private Integer month;
    private Integer salaryProject;
    private Integer salaryBasic;
    private Integer salaryPayable;
    private Integer salaryPaied;
}
