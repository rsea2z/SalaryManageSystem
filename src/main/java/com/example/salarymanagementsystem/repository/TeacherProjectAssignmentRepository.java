package com.example.salarymanagementsystem.repository;

import com.example.salarymanagementsystem.models.Project;
import com.example.salarymanagementsystem.models.Teacher;
import com.example.salarymanagementsystem.models.TeacherProjectAssignment;
import com.example.salarymanagementsystem.models.TeacherProjectAssignmentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherProjectAssignmentRepository extends JpaRepository<TeacherProjectAssignment, TeacherProjectAssignmentId> {
    List<TeacherProjectAssignment> findByTeacher(Teacher teacher);
    List<TeacherProjectAssignment> findByProject(Project project);
    List<TeacherProjectAssignment> findByTeacherAndProjectMonth(Teacher teacher, Integer month);
}
