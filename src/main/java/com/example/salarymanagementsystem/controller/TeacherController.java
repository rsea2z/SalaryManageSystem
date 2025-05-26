package com.example.salarymanagementsystem.controller;

import com.example.salarymanagementsystem.dto.*;
import com.example.salarymanagementsystem.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @GetMapping("/information/{teacherId}")
    @PreAuthorize("isAuthenticated() and #teacherId.toString() == authentication.name or hasRole('ADMIN')")
    public ResponseEntity<TeacherDto> getTeacherInformation(@PathVariable Integer teacherId) {
        TeacherDto teacherInfo = teacherService.getTeacherInformation(teacherId);
        return ResponseEntity.ok(teacherInfo);
    }

    @GetMapping("/admin_of_department/{teacherId}")
    @PreAuthorize("isAuthenticated() and #teacherId.toString() == authentication.name")
    public ResponseEntity<AdminDepartmentInfoDto> getAdminOfDepartment(@PathVariable Integer teacherId) {
        AdminDepartmentInfoDto adminInfo = teacherService.getAdminOfDepartment(teacherId);
        return ResponseEntity.ok(adminInfo);
    }
    
    @GetMapping("/salary/month/{teacherId}")
    @PreAuthorize("isAuthenticated() and #teacherId.toString() == authentication.name or hasRole('ADMIN')")
    public ResponseEntity<Set<Integer>> getSalaryMonths(@PathVariable Integer teacherId) {
        Set<Integer> months = teacherService.getSalaryMonthsForTeacher(teacherId);
        return ResponseEntity.ok(months);
    }

    // Admin accessible endpoints
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TeacherBasicDto>> getAllTeachersBasicInfo() {
        List<TeacherBasicDto> teachers = teacherService.getAllTeachersBasicInfo();
        return ResponseEntity.ok(teachers);
    }
}
