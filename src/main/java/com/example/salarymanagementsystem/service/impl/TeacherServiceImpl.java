package com.example.salarymanagementsystem.service.impl;

import com.example.salarymanagementsystem.dto.*;
import com.example.salarymanagementsystem.exception.AdminOperationException;
import com.example.salarymanagementsystem.exception.ResourceNotFoundException; // Import
import com.example.salarymanagementsystem.models.*;
import com.example.salarymanagementsystem.repository.*;
import com.example.salarymanagementsystem.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional // 统一加在类上
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private SalaryRepository salaryRepository;

    @Autowired
    private TeacherProjectAssignmentRepository tpaRepository;

    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    // 抽取权限校验
    private void checkAdminDepartment(Department department, String adminDepartmentNumber) {
        if (!department.getDepartmentNumber().equals(adminDepartmentNumber)) {
            throw new AdminOperationException("无权操作该部门教师");
        }
    }

    @Override
    public TeacherDto getTeacherInformation(Integer teacherId) {
        Teacher teacher = teacherRepository.findByTeacherNumber(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", teacherId)); // Updated
        Department department = teacher.getDepartment();
        boolean isAdmin = departmentRepository.findByAdministrator(teacher).isPresent();

        return new TeacherDto(
                teacher.getTeacherNumber(),
                teacher.getTeacherName(),
                teacher.getTeacherAge(),
                department != null ? department.getDepartmentNumber() : null,
                department != null ? department.getDepartmentName() : null,
                teacher.getPost(),
                teacher.getTeacherGender(),
                teacher.getJob(),
                teacher.getPhone(),
                teacher.getEmail(),
                isAdmin
        );
    }

    @Override
    public AdminDepartmentInfoDto getAdminOfDepartment(Integer teacherId) {
        Teacher teacher = teacherRepository.findByTeacherNumber(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", teacherId)); // Updated
        Optional<Department> departmentOptional = departmentRepository.findByAdministrator(teacher);
        if (departmentOptional.isPresent()) {
            return new AdminDepartmentInfoDto(departmentOptional.get().getDepartmentNumber(), true);
        }
        return new AdminDepartmentInfoDto(null, false);
    }

    @Override
    public List<TeacherBasicDto> getAllTeachersBasicInfo() {
        return teacherRepository.findAll().stream()
                .map(teacher -> new TeacherBasicDto(teacher.getTeacherNumber(), teacher.getTeacherName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminTeacherViewDto> getDepartmentTeachers(String departmentNumber, Integer month, String teacherName) {
        Department department = departmentRepository.findByDepartmentNumber(departmentNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "number", departmentNumber)); // Updated

        List<Teacher> teachers;
        if (teacherName != null && !teacherName.isEmpty()) {
            teachers = teacherRepository.findByDepartmentAndTeacherNameContainingIgnoreCase(department, teacherName);
        } else {
            teachers = teacherRepository.findByDepartment(department);
        }

        List<AdminTeacherViewDto> result = new ArrayList<>();
        for (Teacher teacher : teachers) {
            int basicSalary = 0;
            int projectSalaryTotal = 0;

            if (month != null && month != 0) {
                Optional<Salary> salaryOpt = salaryRepository.findByTeacherAndMonth(teacher, month);
                if (salaryOpt.isPresent()) {
                    basicSalary = salaryOpt.get().getSalaryBasic() != null ? salaryOpt.get().getSalaryBasic() : 0;
                }
                List<TeacherProjectAssignment> assignments = tpaRepository.findByTeacherAndProjectMonth(teacher, month);
                projectSalaryTotal = assignments.stream()
                        .mapToInt(tpa -> tpa.getProjectSalary() != null ? tpa.getProjectSalary() : 0)
                        .sum();
            } else { // All months
                List<Salary> salaries = salaryRepository.findByTeacher(teacher);
                basicSalary = salaries.stream()
                        .mapToInt(s -> s.getSalaryBasic() != null ? s.getSalaryBasic() : 0)
                        .sum();
                List<TeacherProjectAssignment> assignments = tpaRepository.findByTeacher(teacher);
                projectSalaryTotal = assignments.stream()
                        .mapToInt(tpa -> tpa.getProjectSalary() != null ? tpa.getProjectSalary() : 0)
                        .sum();
            }
            result.add(new AdminTeacherViewDto(
                    teacher.getTeacherNumber(),
                    teacher.getTeacherName(),
                    teacher.getTeacherGender(),
                    teacher.getPost(),
                    teacher.getJob(),
                    basicSalary,
                    projectSalaryTotal
            ));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminTeacherDetailDto getTeacherDetailForAdmin(Integer teacherId) {
        Teacher teacher = teacherRepository.findByTeacherNumber(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", teacherId)); // Updated

        List<ProjectAssignmentDetailDto> projectAssignments = tpaRepository.findByTeacher(teacher).stream()
                .map(tpa -> new ProjectAssignmentDetailDto(
                        tpa.getProject().getProjectNumber(),
                        tpa.getProject().getProjectName(),
                        tpa.getProject().getCompletedOrNot(),
                        tpa.getProjectSalary(),
                        tpa.getProjectSalaryAddition(),
                        tpa.getProjectTask(),
                        tpa.getComment()
                )).collect(Collectors.toList());

        List<MonthlyBasicSalaryDto> monthlySalaries = salaryRepository.findByTeacher(teacher).stream()
                .map(salary -> new MonthlyBasicSalaryDto(
                        salary.getMonth(),
                        salary.getSalaryBasic()
                )).collect(Collectors.toList());

        return new AdminTeacherDetailDto(
                teacher.getTeacherNumber(),
                teacher.getTeacherName(),
                teacher.getTeacherAge(),
                teacher.getTeacherGender(),
                teacher.getPhone(),
                teacher.getEmail(),
                teacher.getDepartment() != null ? teacher.getDepartment().getDepartmentName() : null,
                teacher.getPost(),
                teacher.getJob(),
                projectAssignments,
                monthlySalaries
        );
    }
    
    @Override
    @Transactional
    public TeacherUpsertDto createOrUpdateTeacher(TeacherUpsertDto dto, String adminDepartmentNumber) {
        Department department = departmentRepository.findByDepartmentNumber(dto.getEmployeeDepartment())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "number", dto.getEmployeeDepartment()));
        checkAdminDepartment(department, adminDepartmentNumber);

        Teacher teacher = teacherRepository.findByTeacherNumber(dto.getEmployeeId()).orElse(new Teacher());
        teacher.setTeacherNumber(dto.getEmployeeId());
        teacher.setTeacherName(dto.getEmployeeName());
        teacher.setTeacherAge(dto.getEmployeeAge());
        teacher.setTeacherGender(dto.getEmployeeGender());
        teacher.setDepartment(department);
        teacher.setPost(dto.getEmployeePosition());
        teacher.setJob(dto.getEmployeeDuty());
        teacher.setPhone(dto.getEmployeePhone());
        teacher.setEmail(dto.getEmployeeEmail());

        // 密码逻辑优化
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            teacher.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else if (teacher.getPassword() == null || teacher.getPassword().isEmpty()) {
            throw new AdminOperationException("必须设置初始密码");
        }

        Teacher savedTeacher = teacherRepository.save(teacher);

        // Handle basic salaries
        if (dto.getEmployeeBasic() != null) {
            salaryRepository.deleteAll(salaryRepository.findByTeacher(savedTeacher));
            dto.getEmployeeBasic().forEach(basicDto -> {
                Salary salary = salaryRepository.findByTeacherAndMonth(savedTeacher, basicDto.getMonth())
                                .orElse(new Salary(savedTeacher.getTeacherNumber(), basicDto.getMonth(), savedTeacher, 0, null, null, null));
                salary.setSalaryBasic(basicDto.getSalaryBasic());
                salaryRepository.save(salary);
            });
        }

        // Handle project assignments
        if (dto.getEmployeeProjects() != null) {
            tpaRepository.deleteAll(tpaRepository.findByTeacher(savedTeacher));
            dto.getEmployeeProjects().forEach(projAssignDto -> {
                Project project = projectRepository.findByProjectNumber(projAssignDto.getProjectId())
                        .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projAssignDto.getProjectId()));
                TeacherProjectAssignment tpa = new TeacherProjectAssignment();
                tpa.setTeacher(savedTeacher);
                tpa.setProject(project);
                tpa.setProjectTask(projAssignDto.getTask());
                tpa.setProjectSalary(projAssignDto.getBonus());
                tpa.setProjectSalaryAddition(projAssignDto.getExtraBonus());
                tpa.setComment(projAssignDto.getExtraBonusReason());
                tpaRepository.save(tpa);
            });
        }
        // 返回最新TeacherUpsertDto
        return TeacherUpsertDto.fromEntity(savedTeacher);
    }

    @Override
    public ApiResponse deleteTeacher(Integer teacherId, String adminDepartmentNumber) {
        Teacher teacher = teacherRepository.findByTeacherNumber(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", teacherId));
        checkAdminDepartment(teacher.getDepartment(), adminDepartmentNumber);

        Optional<Department> administeredDept = departmentRepository.findByAdministrator(teacher);
        if (administeredDept.isPresent()) {
            throw new AdminOperationException("不能删除部门管理员");
        }

        tpaRepository.deleteAll(tpaRepository.findByTeacher(teacher));
        salaryRepository.deleteAll(salaryRepository.findByTeacher(teacher));
        teacherRepository.delete(teacher);
        return new ApiResponse(true, "Teacher deleted successfully.");
    }

    @Override
    public Set<Integer> getSalaryMonthsForTeacher(Integer teacherId) {
        Teacher teacher = teacherRepository.findByTeacherNumber(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", teacherId));
        Set<Integer> months = salaryRepository.findDistinctMonthsByTeacher(teacher);
        return months != null ? months : Collections.emptySet();
    }
}
