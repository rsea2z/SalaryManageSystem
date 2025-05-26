package com.example.salarymanagementsystem.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Salary") // 建议表名使用下划线或全小写，例如 "salaries"
@IdClass(SalaryId.class)
public class Salary {

    @Id
    @Column(name = "Teacher_number") // 确保这个列名与数据库中的列名一致
    private Integer teacherNumber;    // 此字段名应与 SalaryId 类中的字段名匹配

    @Id
    @Column(name = "Month", nullable = false)
    private Integer month;            // 此字段名应与 SalaryId 类中的字段名匹配

    // 实际的 Teacher 关联，通过 teacherNumber (外键) 映射
    // insertable = false, updatable = false 是因为 Teacher_number 列已经由 teacherNumber 字段管理
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Teacher_number", referencedColumnName = "Teacher_number", insertable = false, updatable = false)
    private Teacher teacher;

    @Column(name = "Salary_project", nullable = false)
    private Integer salaryProject; // 项目工资总和

    @Column(name = "Salary_basic")
    private Integer salaryBasic; // 基本工资

    @Column(name = "Salary_payable")
    private Integer salaryPayable; // 应付工资总和

    @Column(name = "Salary_paid") // 原字段名 "Slalary_paied" 有拼写错误，建议修正为 "Salary_paid"
    private Integer salaryPaied; // 实付工资总和
}