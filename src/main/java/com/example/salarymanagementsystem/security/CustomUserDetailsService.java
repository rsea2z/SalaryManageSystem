package com.example.salarymanagementsystem.security;

import com.example.salarymanagementsystem.models.Teacher;
import com.example.salarymanagementsystem.repository.DepartmentRepository;
import com.example.salarymanagementsystem.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Assuming username is Teacher_number for login
        Teacher teacher = teacherRepository.findByTeacherNumber(Integer.parseInt(usernameOrEmail))
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username or email : " + usernameOrEmail));
        
        Set<GrantedAuthority> authorities = new HashSet<>();
        // Add roles/authorities if you have them, e.g., based on teacher.getJob() or a separate Role entity
        // For now, a default "ROLE_USER" or based on isAdmin
        boolean isAdmin = departmentRepository.findByAdministrator(teacher).isPresent();
        
        if (isAdmin) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_TEACHER"));
        }


        return new org.springframework.security.core.userdetails.User(teacher.getTeacherNumber().toString(),
                teacher.getPassword(),
                authorities);
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(Integer id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id : " + id)
        );
        
        Set<GrantedAuthority> authorities = new HashSet<>();
        boolean isAdmin = departmentRepository.findByAdministrator(teacher).isPresent();

         if (isAdmin) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_TEACHER"));
        }

        return new org.springframework.security.core.userdetails.User(teacher.getTeacherNumber().toString(),
                teacher.getPassword(),
                authorities);
    }
}
