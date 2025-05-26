package com.example.salarymanagementsystem.controller;

import com.example.salarymanagementsystem.dto.ProjectDetailDto;
import com.example.salarymanagementsystem.dto.ProjectDto;
import com.example.salarymanagementsystem.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;
    
    // Endpoint for a teacher to view their projects (all or by month)
    // /api/teacher/projects/{teacher_number}
    // /api/teacher/projects/{teacher_number}/{month}
    // These are combined into one endpoint with an optional month parameter
    @GetMapping("/teacher/{teacherNumber}")
    @PreAuthorize("isAuthenticated() and #teacherNumber.toString().equals(authentication.name.toString())")
    public ResponseEntity<List<ProjectDto>> getTeacherProjects(
            @PathVariable Integer teacherNumber,
            @RequestParam(required = false) Integer month) { // month=0 or null for all
        List<ProjectDto> projects = projectService.getTeacherProjects(teacherNumber, month == null ? 0 : month);
        return ResponseEntity.ok(projects);
    }

    // New endpoint to get project details for a teacher
    @GetMapping("/{projectId}/{teacherId}") 
    @PreAuthorize("(isAuthenticated() and #teacherId.toString().equals(authentication.name.toString())) or hasRole('ADMIN')") // MODIFIED: Added OR condition for ADMIN role
    public ResponseEntity<ProjectDetailDto> getProjectDetailsForTeacher(
            @PathVariable Integer projectId, 
            @PathVariable Integer teacherId) { 
        ProjectDetailDto projectDetails = projectService.getProjectDetailsForTeacher(projectId, teacherId);
        return ResponseEntity.ok(projectDetails);
    }
    
}
