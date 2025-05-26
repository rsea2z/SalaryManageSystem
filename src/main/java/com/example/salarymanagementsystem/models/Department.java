package com.example.salarymanagementsystem.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Department")
public class Department {

    @Id
    @Column(name = "Department_number", nullable = false)
    private String departmentNumber; // 院系编号

    @Column(name = "Department_name", nullable = false)
    private String departmentName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Administrator_id", referencedColumnName = "Teacher_number")
    private Teacher administrator; // 财务负责人

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Teacher> teachers;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Project> projects;
}
