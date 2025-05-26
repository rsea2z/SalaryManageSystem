package com.example.salarymanagementsystem.controller;

import com.example.salarymanagementsystem.dto.SalaryDto;
import com.example.salarymanagementsystem.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teacher/salary") // Base path from original /api/teacher/salary/{teacher_number}/{month}
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    @GetMapping("/{teacherNumber}/{month}")
    @PreAuthorize("isAuthenticated() and #teacherNumber.toString() == authentication.name or hasRole('ADMIN')")
    public ResponseEntity<SalaryDto> getTeacherSalary(
            @PathVariable Integer teacherNumber,
            @PathVariable Integer month) { // month = 0 for all months sum
        SalaryDto salaryDetails = salaryService.getTeacherSalary(teacherNumber, month);
        return ResponseEntity.ok(salaryDetails);
    }
}
