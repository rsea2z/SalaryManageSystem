package com.example.salarymanagementsystem.models;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // Important for composite keys
public class TeacherProjectAssignmentId implements Serializable {
    private static final long serialVersionUID = 1L; // Good practice for Serializable classes

    private Integer teacher; // Changed from teacherNumber to teacher, type matches Teacher's PK type
    private Integer project; // Changed from projectNumber to project, type matches Project's PK type
}
