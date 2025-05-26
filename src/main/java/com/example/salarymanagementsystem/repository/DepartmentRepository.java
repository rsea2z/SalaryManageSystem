package com.example.salarymanagementsystem.repository;

import com.example.salarymanagementsystem.models.Department;
import com.example.salarymanagementsystem.models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {
    Optional<Department> findByDepartmentNumber(String departmentNumber);
    Optional<Department> findByAdministrator(Teacher administrator);
}
