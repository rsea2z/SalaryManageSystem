package com.example.salarymanagementsystem.utility;

import com.example.salarymanagementsystem.models.Teacher;
import com.example.salarymanagementsystem.repository.TeacherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class DatabasePasswordInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabasePasswordInitializer.class);
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String PASSWORD_PREFIX = "hashed_";

    public DatabasePasswordInitializer(TeacherRepository teacherRepository, PasswordEncoder passwordEncoder) {
        // 添加构造函数日志
        System.out.println("DatabasePasswordInitializer CONSTRUCTOR CALLED - Bean is being created.");
        logger.info("DatabasePasswordInitializer CONSTRUCTOR - Bean is being created via logger.");
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("CommandLineRunner RUN method: 启动密码初始化检查...");

        List<Teacher> teachers = teacherRepository.findAll();
        List<Teacher> teachersToUpdate = new ArrayList<>(); // ADDED: List to hold modified teachers
        int updatedCount = 0;

        //logger.info("将要检查 {} 位教师的密码，寻找以 '{}' 开头的未哈希密码。", teachers.size(), PASSWORD_PREFIX);

        for (Teacher teacher : teachers) {
            String currentPassword = teacher.getPassword();
            // logger.debug("教师编号 {}: 当前数据库密码是 '{}'", teacher.getTeacherNumber(), currentPassword);

            if (currentPassword != null && currentPassword.startsWith(PASSWORD_PREFIX)) {
                //logger.info("教师编号 {}: 发现未哈希密码 '{}'，以 '{}' 开头。", teacher.getTeacherNumber(), currentPassword, PASSWORD_PREFIX);
                String rawPasswordToHash = currentPassword.substring(PASSWORD_PREFIX.length());
                //logger.info("教师编号 {}: 提取的待哈希原始密码是 '{}'", teacher.getTeacherNumber(), rawPasswordToHash);

                if (rawPasswordToHash.isEmpty()) {
                    //logger.warn("教师编号 {}: 密码以 '{}' 开头，但没有实际的密码部分: '{}'。跳过处理。",
                    //            teacher.getTeacherNumber(), PASSWORD_PREFIX, currentPassword);
                    continue;
                }

                //logger.info("教师编号 {}: 准备对 '{}' 进行哈希处理。",
                //            teacher.getTeacherNumber(), rawPasswordToHash);

                String newHashedPassword = passwordEncoder.encode(rawPasswordToHash);
                teacher.setPassword(newHashedPassword);
                teachersToUpdate.add(teacher); // ADDED: Add modified teacher to the list
                updatedCount++;
                //logger.info("教师编号 {}: 密码已更新为哈希值。", teacher.getTeacherNumber());
            } else {
                //logger.info("教师编号 {}: 密码 '{}' 不以 '{}' 开头，或密码为null。假定已哈希或无需处理，跳过。", teacher.getTeacherNumber(), currentPassword, PASSWORD_PREFIX);
            }
        }
        
        if (updatedCount > 0) {
            teacherRepository.saveAll(teachersToUpdate); // MODIFIED: Save only the updated teachers
            logger.info("密码初始化检查完成，共更新了 {} 个教师的密码。", updatedCount);
        } else {
            logger.info("密码初始化检查完成，没有找到以 '{}' 开头的未哈希密码。", PASSWORD_PREFIX);
        }
    }
}