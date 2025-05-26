package com.example.salarymanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDetailDto extends ProjectDto {
    private Integer leaderId; // Teacher_number of the leader
    private String leaderName;
    private Integer month;
    private List<TeacherProjectAssignmentDto> collaborators; // 添加协作者字段
    // For teacher's view of project details
    private Integer myProjectSalary;
    private Integer myProjectSalaryAddition;
    private String myComment;
    private String myProjectTask;

    public ProjectDetailDto(Integer projectNumber,
                           String projectName,
                           String departmentName,
                           Integer leaderId,
                           String leaderName,
                           Integer month,
                           String completedOrNot,
                           List<TeacherProjectAssignmentDto> collaborators,
                           Integer myProjectSalary,
                           Integer myProjectSalaryAddition,
                           String myComment,
                           String myProjectTask) {
        super(projectNumber, projectName, completedOrNot, departmentName, leaderName, null);
        this.leaderId = leaderId;
        this.leaderName = leaderName;
        this.month = month;
        this.collaborators = collaborators;
        this.myProjectSalary = myProjectSalary;
        this.myProjectSalaryAddition = myProjectSalaryAddition;
        this.myComment = myComment;
        this.myProjectTask = myProjectTask;
    }
}
