package com.example.salarymanagementsystem.service;

import com.example.salarymanagementsystem.dto.*;

import java.util.List;
import java.util.Set;

public interface TeacherService {
    TeacherDto getTeacherInformation(Integer teacherId);
    AdminDepartmentInfoDto getAdminOfDepartment(Integer teacherId);
    List<TeacherBasicDto> getAllTeachersBasicInfo();
    List<AdminTeacherViewDto> getDepartmentTeachers(String departmentNumber, Integer month, String teacherName);
    AdminTeacherDetailDto getTeacherDetailForAdmin(Integer teacherId);
    TeacherUpsertDto createOrUpdateTeacher(TeacherUpsertDto teacherUpsertDto, String adminDepartmentNumber);
    ApiResponse deleteTeacher(Integer teacherId, String adminDepartmentNumber);
    Set<Integer> getSalaryMonthsForTeacher(Integer teacherId);
}
