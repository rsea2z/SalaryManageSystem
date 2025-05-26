package com.example.salarymanagementsystem.service.impl;

import com.example.salarymanagementsystem.dto.JwtAuthResponse;
import com.example.salarymanagementsystem.dto.LoginRequest;
import com.example.salarymanagementsystem.exception.ResourceNotFoundException; // Import
import com.example.salarymanagementsystem.models.Department;
import com.example.salarymanagementsystem.models.Teacher;
import com.example.salarymanagementsystem.repository.DepartmentRepository;
import com.example.salarymanagementsystem.repository.TeacherRepository;
import com.example.salarymanagementsystem.security.JwtTokenProvider;
import com.example.salarymanagementsystem.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;

    // @Autowired
    // private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public JwtAuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUserid(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);
        
        Integer userId = Integer.parseInt(loginRequest.getUserid());
        Teacher teacher = teacherRepository.findByTeacherNumber(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId)); // Updated

        boolean isAdmin = false;
        String adminDepartmentNumber = null;
        
        Department administeredDept = departmentRepository.findByAdministrator(teacher).orElse(null);
        if (administeredDept != null) {
            isAdmin = true;
            adminDepartmentNumber = administeredDept.getDepartmentNumber();
        }

        return new JwtAuthResponse(token, "Bearer", userId, teacher.getTeacherName(), isAdmin, adminDepartmentNumber);
    }
}
