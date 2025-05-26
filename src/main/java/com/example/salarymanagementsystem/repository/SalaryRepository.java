package com.example.salarymanagementsystem.repository;

import com.example.salarymanagementsystem.models.Salary;
import com.example.salarymanagementsystem.models.SalaryId;
import com.example.salarymanagementsystem.models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, SalaryId> {
    List<Salary> findByTeacher(Teacher teacher);
    Optional<Salary> findByTeacherAndMonth(Teacher teacher, Integer month);

    @Query("SELECT DISTINCT s.month FROM Salary s WHERE s.teacher = :teacher")
    Set<Integer> findDistinctMonthsByTeacher(@Param("teacher") Teacher teacher);
}
