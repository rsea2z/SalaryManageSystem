package com.example.salarymanagementsystem.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SalaryId implements Serializable {

    private Integer teacherNumber; // Name and type must match @Id field in Salary entity

    private Integer month;         // Name and type must match @Id field in Salary entity
}
