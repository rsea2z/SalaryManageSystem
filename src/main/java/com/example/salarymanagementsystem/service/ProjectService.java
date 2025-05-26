package com.example.salarymanagementsystem.service;

import com.example.salarymanagementsystem.dto.ApiResponse;
import com.example.salarymanagementsystem.dto.ProjectDetailDto;
import com.example.salarymanagementsystem.dto.ProjectDto;
import com.example.salarymanagementsystem.dto.ProjectUpsertDto;

import java.util.List;
import java.util.Set;

public interface ProjectService {
    List<ProjectDto> getTeacherProjects(Integer teacherId, Integer month);
    ProjectDetailDto getProjectDetailsForTeacher(Integer projectId, Integer teacherId);
    List<ProjectDto> getDepartmentProjects(String departmentNumber, Integer month, String projectName);
    ProjectDetailDto getProjectDetailsForAdmin(Integer projectId, String adminDepartmentNumber);
    Set<Integer> getProjectMonthsForDepartment(String departmentNumber);
    ProjectUpsertDto createOrUpdateProject(ProjectUpsertDto projectUpsertDto, String adminDepartmentNumber);
    ApiResponse deleteProject(Integer projectId, String adminDepartmentNumber);
    ApiResponse importProjectsFromCsv(byte[] csvData, String adminDepartmentNumber);
}
