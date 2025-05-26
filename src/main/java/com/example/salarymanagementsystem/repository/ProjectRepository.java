package com.example.salarymanagementsystem.repository;

import com.example.salarymanagementsystem.models.Department;
import com.example.salarymanagementsystem.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    Optional<Project> findByProjectNumber(Integer projectNumber);
    List<Project> findByDepartment(Department department);
    List<Project> findByDepartmentAndMonth(Department department, Integer month);
    List<Project> findByDepartmentAndProjectNameContainingIgnoreCase(Department department, String projectName);
    List<Project> findByDepartmentAndMonthAndProjectNameContainingIgnoreCase(Department department, Integer month, String projectName);

    @Query("SELECT DISTINCT p.month FROM Project p WHERE p.department = :department")
    Set<Integer> findDistinctMonthsByDepartment(@Param("department") Department department);
}
