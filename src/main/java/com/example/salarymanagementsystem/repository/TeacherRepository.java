package com.example.salarymanagementsystem.repository;

import com.example.salarymanagementsystem.models.Department;
import com.example.salarymanagementsystem.models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    Optional<Teacher> findByTeacherNumber(Integer teacherNumber);
    Optional<Teacher> findByEmail(String email);
    List<Teacher> findByDepartment(Department department);
    List<Teacher> findByDepartmentAndTeacherNameContainingIgnoreCase(Department department, String teacherName);
    boolean existsByTeacherNumber(Integer teacherNumber);
    boolean existsByEmail(String email);
}
