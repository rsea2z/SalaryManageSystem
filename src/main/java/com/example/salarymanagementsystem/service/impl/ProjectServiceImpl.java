package com.example.salarymanagementsystem.service.impl;

import com.example.salarymanagementsystem.dto.*;
import com.example.salarymanagementsystem.exception.ResourceNotFoundException; // Import
import com.example.salarymanagementsystem.models.*;
import com.example.salarymanagementsystem.repository.*;
import com.example.salarymanagementsystem.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private TeacherProjectAssignmentRepository tpaRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> getTeacherProjects(Integer teacherId, Integer month) {
        Teacher teacher = teacherRepository.findByTeacherNumber(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", teacherId)); // Updated

        List<TeacherProjectAssignment> assignments;
        if (month != null && month != 0) {
            assignments = tpaRepository.findByTeacherAndProjectMonth(teacher, month);
        } else {
            assignments = tpaRepository.findByTeacher(teacher);
        }

        return assignments.stream().map(tpa -> {
            Project p = tpa.getProject();
            return new ProjectDto(
                    p.getProjectNumber(),
                    p.getProjectName(),
                    p.getCompletedOrNot(),
                    p.getDepartment() != null ? p.getDepartment().getDepartmentName() : null,
                    p.getLeader() != null ? p.getLeader().getTeacherName() : null,
                    tpa.getProjectSalary()
            );
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDetailDto getProjectDetailsForTeacher(Integer projectId, Integer teacherId) { // MODIFIED return type
        Project project = projectRepository.findByProjectNumber(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        Teacher currentTeacher = teacherRepository.findByTeacherNumber(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", teacherId));

        TeacherProjectAssignmentId tpaId = new TeacherProjectAssignmentId(currentTeacher.getTeacherNumber(), project.getProjectNumber());
        TeacherProjectAssignment currentTeacherAssignment = tpaRepository.findById(tpaId)
                .orElseThrow(() -> new ResourceNotFoundException("TeacherProjectAssignment", "teacherId/projectId", teacherId + "/" + projectId)); // Updated

        List<TeacherProjectAssignmentDto> collaborators = tpaRepository.findByProject(project).stream()
                .map(tpa -> new TeacherProjectAssignmentDto(
                        tpa.getTeacher().getTeacherNumber(),
                        tpa.getTeacher().getTeacherName(),
                        tpa.getTeacher().getDepartment() != null ? tpa.getTeacher().getDepartment().getDepartmentName() : null,
                        tpa.getProjectTask(),
                        tpa.getProjectSalary(),
                        tpa.getProjectSalaryAddition(),
                        tpa.getComment(),
                        project.getLeader() != null && project.getLeader().getTeacherNumber().equals(tpa.getTeacher().getTeacherNumber())
                )).collect(Collectors.toList());

        return new ProjectDetailDto(
                project.getProjectNumber(),
                project.getProjectName(),
                project.getDepartment() != null ? project.getDepartment().getDepartmentName() : null,
                project.getLeader() != null ? project.getLeader().getTeacherNumber() : null,
                project.getLeader() != null ? project.getLeader().getTeacherName() : null,
                project.getMonth(),
                project.getCompletedOrNot(),
                collaborators,
                currentTeacherAssignment.getProjectSalary(),
                currentTeacherAssignment.getProjectSalaryAddition(),
                currentTeacherAssignment.getComment(),
                currentTeacherAssignment.getProjectTask()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> getDepartmentProjects(String departmentNumber, Integer month, String projectName) {
        Department department = departmentRepository.findByDepartmentNumber(departmentNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "number", departmentNumber)); // Updated
        List<Project> projects;

        boolean searchByName = projectName != null && !projectName.isEmpty();

        if (month != null && month != 0) {
            if (searchByName) {
                projects = projectRepository.findByDepartmentAndMonthAndProjectNameContainingIgnoreCase(department, month, projectName);
            } else {
                projects = projectRepository.findByDepartmentAndMonth(department, month);
            }
        } else {
            if (searchByName) {
                projects = projectRepository.findByDepartmentAndProjectNameContainingIgnoreCase(department, projectName);
            } else {
                projects = projectRepository.findByDepartment(department);
            }
        }

        return projects.stream().map(p -> {
            int totalSalary = tpaRepository.findByProject(p).stream()
                                .mapToInt(tpa -> tpa.getProjectSalary() != null ? tpa.getProjectSalary() : 0)
                                .sum();
            return new ProjectDto(
                    p.getProjectNumber(),
                    p.getProjectName(),
                    p.getCompletedOrNot(),
                    p.getDepartment().getDepartmentName(),
                    p.getLeader() != null ? p.getLeader().getTeacherName() : null,
                    totalSalary // This is sum of assignments, not a field in Project entity
            );
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDetailDto getProjectDetailsForAdmin(Integer projectId, String adminDepartmentNumber) { // MODIFIED return type
        Project project = projectRepository.findByProjectNumber(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId)); // Updated

        if (project.getDepartment() == null || !project.getDepartment().getDepartmentNumber().equals(adminDepartmentNumber)) {
            throw new SecurityException("Admin not authorized for project in department: " +
                (project.getDepartment() != null ? project.getDepartment().getDepartmentName() : "N/A"));
        }

        List<TeacherProjectAssignmentDto> assignments = tpaRepository.findByProject(project).stream()
                .map(tpa -> new TeacherProjectAssignmentDto(
                        tpa.getTeacher().getTeacherNumber(),
                        tpa.getTeacher().getTeacherName(),
                        tpa.getTeacher().getDepartment() != null ? tpa.getTeacher().getDepartment().getDepartmentName() : null,
                        tpa.getProjectTask(),
                        tpa.getProjectSalary(),
                        tpa.getProjectSalaryAddition(),
                        tpa.getComment(),
                        project.getLeader() != null && project.getLeader().getTeacherNumber().equals(tpa.getTeacher().getTeacherNumber())
                )).collect(Collectors.toList());

        return new ProjectDetailDto(
                project.getProjectNumber(),
                project.getProjectName(),
                project.getDepartment() != null ? project.getDepartment().getDepartmentName() : null, // departmentName
                project.getLeader() != null ? project.getLeader().getTeacherNumber() : null,
                project.getLeader() != null ? project.getLeader().getTeacherName() : null,
                project.getMonth(),
                project.getCompletedOrNot(),
                assignments,
                null, null, null, null // Teacher specific fields not relevant for admin general view
        );
    }

    @Override
    public Set<Integer> getProjectMonthsForDepartment(String departmentNumber) {
        Department department = departmentRepository.findByDepartmentNumber(departmentNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "number", departmentNumber)); // Updated
        return projectRepository.findDistinctMonthsByDepartment(department);
    }

    @Override
    @Transactional
    public ProjectUpsertDto createOrUpdateProject(ProjectUpsertDto dto, String adminDepartmentNumber) {
        Department projectDepartment = departmentRepository.findByDepartmentNumber(dto.getDepartment())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "number", dto.getDepartment())); // Updated

        // Authorization: Admin must belong to the department they are creating/updating project for
        if (!projectDepartment.getDepartmentNumber().equals(adminDepartmentNumber)) {
            throw new SecurityException("Admin not authorized to manage projects for department: " + projectDepartment.getDepartmentName());
        }

        Teacher leader = teacherRepository.findByTeacherNumber(dto.getProjectManager())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", dto.getProjectManager())); // Updated

        Project project = projectRepository.findByProjectNumber(dto.getProjectId()).orElse(new Project());
        project.setProjectNumber(dto.getProjectId());
        project.setProjectName(dto.getProjectName());
        project.setLeader(leader);
        project.setDepartment(projectDepartment);
        project.setCompletedOrNot(dto.getCompleted());
        project.setMonth(dto.getMonth()); // Assuming month is part of DTO

        Project savedProject = projectRepository.save(project);

        // Clear existing assignments for this project and re-add
        tpaRepository.deleteAll(tpaRepository.findByProject(savedProject));

        if (dto.getProjectMembers() != null) {
            for (ProjectMemberDto memberDto : dto.getProjectMembers()) {
                Teacher memberTeacher = teacherRepository.findByTeacherNumber(memberDto.getTeacherId())
                        .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", memberDto.getTeacherId()));
                TeacherProjectAssignment tpa = new TeacherProjectAssignment();
                tpa.setProject(savedProject);
                tpa.setTeacher(memberTeacher);
                tpa.setProjectTask(memberDto.getTask());
                tpa.setProjectSalary(memberDto.getBonus());
                // ProjectSalaryAddition and Comment might need to be set if applicable from DTO
                // Consider adding these fields to ProjectMemberDto if they need to be set during creation/update
                // tpa.setProjectSalaryAddition(memberDto.getExtraBonus()); // MODIFIED: Commented out due to missing method in ProjectMemberDto
                // tpa.setComment(memberDto.getExtraBonusReason());   // MODIFIED: Commented out due to missing method in ProjectMemberDto
                tpaRepository.save(tpa);
            }
        }
        // Consider returning a DTO mapped from savedProject and its members
        // For now, returning the input DTO as per original logic, but ideally:
        // return mapProjectToProjectUpsertDto(savedProject);
        return dto;
    }

    @Override
    @Transactional
    public ApiResponse deleteProject(Integer projectId, String adminDepartmentNumber) {
        Project project = projectRepository.findByProjectNumber(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId)); // Updated

        if (project.getDepartment() == null || !project.getDepartment().getDepartmentNumber().equals(adminDepartmentNumber)) {
            throw new SecurityException("Admin not authorized to delete project from department: " +
                (project.getDepartment() != null ? project.getDepartment().getDepartmentName() : "N/A"));
        }

        tpaRepository.deleteAll(tpaRepository.findByProject(project));
        projectRepository.delete(project);
        return new ApiResponse(true, "Project deleted successfully.");
    }

    @Override
    @Transactional
    public ApiResponse importProjectsFromCsv(byte[] csvData, String adminDepartmentNumber) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(csvData), StandardCharsets.UTF_8)); // Specify UTF-8
            String line;
            int processedCount = 0;
            int skippedCount = 0;
            List<String> errors = new ArrayList<>();
            
            // 跳过CSV文件的表头行
            reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                try {
                    String[] data = line.split(",");
                    if (data.length < 5) { // 确保有足够的字段
                        errors.add("行数据不完整: " + line);
                        skippedCount++;
                        continue;
                    }
                    
                    // 解析CSV数据
                    Integer projectId = Integer.parseInt(data[0].trim());
                    String projectName = data[1].trim();
                    Integer leaderId = Integer.parseInt(data[2].trim());
                    String departmentNumber = data[3].trim();
                    Integer month = Integer.parseInt(data[4].trim());
                    String completed = data.length > 5 ? data[5].trim() : "否";
                    
                    // 检查管理员权限
                    if (!departmentNumber.equals(adminDepartmentNumber)) {
                        errors.add("无权导入其他部门项目: " + projectName);
                        skippedCount++;
                        continue;
                    }
                    
                    // 检查部门是否存在
                    Department department = departmentRepository.findByDepartmentNumber(departmentNumber)
                            .orElse(null);
                    if (department == null) {
                        errors.add("部门不存在: " + departmentNumber);
                        skippedCount++;
                        continue;
                    }
                    
                    // 检查项目负责人是否存在
                    Teacher leader = teacherRepository.findByTeacherNumber(leaderId)
                            .orElse(null);
                    if (leader == null) {
                        errors.add("项目负责人不存在: " + leaderId);
                        skippedCount++;
                        continue;
                    }
                    
                    // 创建或更新项目
                    Project project = projectRepository.findByProjectNumber(projectId).orElse(new Project());
                    project.setProjectNumber(projectId);
                    project.setProjectName(projectName);
                    project.setLeader(leader);
                    project.setDepartment(department);
                    project.setCompletedOrNot(completed);
                    project.setMonth(month);
                    
                    projectRepository.save(project);
                    processedCount++;
                    
                } catch (NumberFormatException e) {
                    errors.add("数据格式错误: " + line + " - " + e.getMessage());
                    skippedCount++;
                } catch (Exception e) {
                    errors.add("处理行时发生错误: " + line + " - " + e.getMessage());
                    skippedCount++;
                }
            }
            
            String message = String.format("导入完成。成功处理 %d 个项目，跳过 %d 个。", processedCount, skippedCount);
            if (!errors.isEmpty()) {
                message += " 错误: " + String.join("; ", errors);
            }
            
            return new ApiResponse(true, message);
            
        } catch (IOException e) {
            return new ApiResponse(false, "CSV文件读取失败: " + e.getMessage());
        } catch (Exception e) {
            return new ApiResponse(false, "导入过程中发生错误: " + e.getMessage());
        }
    }
}
