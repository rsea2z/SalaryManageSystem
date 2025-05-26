package com.example.salarymanagementsystem.controller;

import com.example.salarymanagementsystem.dto.*;
import com.example.salarymanagementsystem.service.DepartmentService;
import com.example.salarymanagementsystem.service.ProjectService;
import com.example.salarymanagementsystem.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/department")
@PreAuthorize("hasRole('ADMIN')") // All endpoints in this controller require ADMIN role
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TeacherService teacherService;
    
    private String getAdminDepartmentNumber(Authentication authentication) {
        // Assuming JwtAuthResponse or similar is stored in authentication details or principal
        // For simplicity, let's assume the admin's department is part of their authorities or a custom principal
        // This needs to be robustly implemented based on how admin department is stored upon login.
        // A placeholder logic:
        // If using CustomUserDetails, you might add department info there.
        // Or, query it based on the authenticated admin's ID.
        // For now, this is a simplified placeholder.
        // A better way would be to get it from the JWT claims if stored there, or from UserDetails.
        AdminDepartmentInfoDto adminInfo = teacherService.getAdminOfDepartment(Integer.parseInt(authentication.getName()));
        if (!adminInfo.isAdmin() || adminInfo.getDepartmentNumber() == null) {
            throw new SecurityException("Admin department not found or user is not an admin.");
        }
        return adminInfo.getDepartmentNumber();
    }


    @GetMapping("/projects/all")
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        List<DepartmentDto> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/projects/{departmentNumber}/{month}")
    public ResponseEntity<List<ProjectDto>> getDepartmentProjectsByMonth(
            @PathVariable String departmentNumber,
            @PathVariable Integer month,
            Authentication authentication) {
        // Optional: Add check to ensure adminDepartmentNumber matches departmentNumber if needed
        List<ProjectDto> projects = projectService.getDepartmentProjects(departmentNumber, month, null);
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/projects/{departmentNumber}/{month}/{projectName}")
    public ResponseEntity<List<ProjectDto>> searchDepartmentProjectsByMonthAndName(
            @PathVariable String departmentNumber,
            @PathVariable Integer month,
            @PathVariable String projectName,
            Authentication authentication) {
        List<ProjectDto> projects = projectService.getDepartmentProjects(departmentNumber, month, projectName);
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/projects/month/{departmentNumber}")
    public ResponseEntity<Set<Integer>> getDepartmentProjectMonths(@PathVariable String departmentNumber, Authentication authentication) {
        // Optional: Add check to ensure adminDepartmentNumber matches departmentNumber
        Set<Integer> months = projectService.getProjectMonthsForDepartment(departmentNumber);
        return ResponseEntity.ok(months);
    }

    @PostMapping("/project")
    public ResponseEntity<ProjectUpsertDto> createOrUpdateProject(
            @Valid @RequestBody ProjectUpsertDto projectUpsertDto,
            Authentication authentication) {
        String adminDepartmentNumber = getAdminDepartmentNumber(authentication);
        ProjectUpsertDto result = projectService.createOrUpdateProject(projectUpsertDto, adminDepartmentNumber);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @DeleteMapping("/project/{projectNumber}")
    public ResponseEntity<ApiResponse> deleteProject(
            @PathVariable Integer projectNumber,
            Authentication authentication) {
        String adminDepartmentNumber = getAdminDepartmentNumber(authentication);
        ApiResponse response = projectService.deleteProject(projectNumber, adminDepartmentNumber);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/project/{projectNumber}")
    public ResponseEntity<ProjectDetailDto> getProjectDetailsForAdmin(
            @PathVariable Integer projectNumber,
            Authentication authentication) {
        String adminDepartmentNumber = getAdminDepartmentNumber(authentication);
        ProjectDetailDto projectDetails = projectService.getProjectDetailsForAdmin(projectNumber, adminDepartmentNumber);
        return ResponseEntity.ok(projectDetails);
    }

    @GetMapping("/teachers/{departmentNumber}/{month}")
    public ResponseEntity<List<AdminTeacherViewDto>> getDepartmentTeachers(
            @PathVariable String departmentNumber,
            @PathVariable Integer month,
            Authentication authentication) {
        // Optional: Add check to ensure adminDepartmentNumber matches departmentNumber
        List<AdminTeacherViewDto> teachers = teacherService.getDepartmentTeachers(departmentNumber, month, null);
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/teachers/{departmentNumber}/{month}/{teacherName}")
    public ResponseEntity<List<AdminTeacherViewDto>> searchDepartmentTeachers(
            @PathVariable String departmentNumber,
            @PathVariable Integer month,
            @PathVariable String teacherName,
            Authentication authentication) {
        List<AdminTeacherViewDto> teachers = teacherService.getDepartmentTeachers(departmentNumber, month, teacherName);
        return ResponseEntity.ok(teachers);
    }

    @PostMapping("/teacher")
    public ResponseEntity<TeacherUpsertDto> createOrUpdateTeacher(
            @Valid @RequestBody TeacherUpsertDto teacherUpsertDto,
            Authentication authentication) {
        String adminDepartmentNumber = getAdminDepartmentNumber(authentication);
        TeacherUpsertDto result = teacherService.createOrUpdateTeacher(teacherUpsertDto, adminDepartmentNumber);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @DeleteMapping("/teacher/{teacherId}")
    public ResponseEntity<ApiResponse> deleteTeacher(
            @PathVariable Integer teacherId,
            Authentication authentication) {
        String adminDepartmentNumber = getAdminDepartmentNumber(authentication);
        ApiResponse response = teacherService.deleteTeacher(teacherId, adminDepartmentNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teacher/detail/{teacherId}")
    public ResponseEntity<AdminTeacherDetailDto> getTeacherDetailForAdmin(
            @PathVariable Integer teacherId,
            Authentication authentication) {
        // String adminDepartmentNumber = getAdminDepartmentNumber(authentication); // Not strictly needed if admin can see any teacher detail
        AdminTeacherDetailDto teacherDetail = teacherService.getTeacherDetailForAdmin(teacherId);
        return ResponseEntity.ok(teacherDetail);
    }
}
