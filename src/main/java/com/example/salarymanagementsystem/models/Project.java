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
@Table(name = "Project")
public class Project {

    @Id
    @Column(name = "Project_number", nullable = false)
    private Integer projectNumber; // 项目编号

    @Column(name = "Project_name")
    private String projectName;

    @Column(name = "completed_or_not", length = 10)
    private String completedOrNot; // 是否计入工资（是否启用）

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Department_number", referencedColumnName = "Department_number")
    private Department department; // 院系编号

    @Column(name = "Month")
    private Integer month; // 计入哪一月工资

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Leader", referencedColumnName = "Teacher_number")
    private Teacher leader; // 项目负责人

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TeacherProjectAssignment> assignments;
}
