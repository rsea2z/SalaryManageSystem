package com.example.salarymanagementsystem.service.impl;

import com.example.salarymanagementsystem.dto.DepartmentDto;
import com.example.salarymanagementsystem.repository.DepartmentRepository;
import com.example.salarymanagementsystem.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public List<DepartmentDto> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(department -> new DepartmentDto(department.getDepartmentNumber(), department.getDepartmentName()))
                .collect(Collectors.toList());
    }
}
