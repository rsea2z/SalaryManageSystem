package com.example.salarymanagementsystem.service.impl;

import com.example.salarymanagementsystem.dto.SalaryDto;
import com.example.salarymanagementsystem.exception.ResourceNotFoundException; // Import
import com.example.salarymanagementsystem.models.Salary;
import com.example.salarymanagementsystem.models.Teacher;
import com.example.salarymanagementsystem.repository.SalaryRepository;
import com.example.salarymanagementsystem.repository.TeacherRepository;
import com.example.salarymanagementsystem.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SalaryServiceImpl implements SalaryService {

    @Autowired
    private SalaryRepository salaryRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Override
    @Transactional(readOnly = true)
    public SalaryDto getTeacherSalary(Integer teacherId, Integer month) {
        Teacher teacher = teacherRepository.findByTeacherNumber(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", teacherId)); // Updated

        if (month != null && month != 0) {
            Salary salary = salaryRepository.findByTeacherAndMonth(teacher, month)
                    .orElse(new Salary(teacher.getTeacherNumber(), month, teacher, 0, 0, 0, 0)); // Return zeroed if not found for specific month
            return new SalaryDto(
                    teacherId,
                    month,
                    salary.getSalaryProject(),
                    salary.getSalaryBasic(),
                    salary.getSalaryPayable(),
                    salary.getSalaryPaied()
            );
        } else { // All months sum
            List<Salary> salaries = salaryRepository.findByTeacher(teacher);
            int totalProject = salaries.stream().mapToInt(s -> s.getSalaryProject() != null ? s.getSalaryProject() : 0).sum();
            int totalBasic = salaries.stream().mapToInt(s -> s.getSalaryBasic() != null ? s.getSalaryBasic() : 0).sum();
            int totalPayable = salaries.stream().mapToInt(s -> s.getSalaryPayable() != null ? s.getSalaryPayable() : 0).sum();
            int totalPaied = salaries.stream().mapToInt(s -> s.getSalaryPaied() != null ? s.getSalaryPaied() : 0).sum();
            return new SalaryDto(teacherId, 0, totalProject, totalBasic, totalPayable, totalPaied);
        }
    }
}
