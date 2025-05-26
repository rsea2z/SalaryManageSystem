package com.example.salarymanagementsystem.service;

import com.example.salarymanagementsystem.dto.SalaryDto;

public interface SalaryService {
    SalaryDto getTeacherSalary(Integer teacherId, Integer month);
}
