package com.example.salarymanagementsystem.service.impl;

import com.example.salarymanagementsystem.service.FileDownloadService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Service
public class FileDownloadServiceImpl implements FileDownloadService {

    private static final Logger logger = LoggerFactory.getLogger(FileDownloadServiceImpl.class);

    @Override
    public void downloadProjectTemplate(HttpServletResponse response) {
        try {
            // 设置响应头
            response.setContentType("text/csv; charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''project_template.csv");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            
            // 添加UTF-8 BOM标记，确保Excel正确显示中文
            response.getOutputStream().write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            
            // 使用OutputStreamWriter确保正确的编码
            PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8), 
                true
            );
            
            // CSV标题行
            StringBuilder header = new StringBuilder();
            header.append("项目编号,项目名称,项目负责人工号,主导院系编号,计入月份,项目总奖金"); // Added 项目总奖金
            
            // 添加项目成员列（支持最多10个成员）
            for (int i = 1; i <= 10; i++) {
                header.append(",项目成员工号").append(i)
                      .append(",任务").append(i)
                      .append(",奖金比例").append(i); // Changed from 奖金 to 奖金比例
            }
            header.append(",是否完成");
            
            writer.println(header.toString());
            
            // 示例数据行1 (参考 SQL 中 Project 2024001 和相关分配)
            // 总奖金: 9000 + 6000 + 5000 + 5500 = 25500
            StringBuilder example1 = new StringBuilder();
            // 项目编号,项目名称,项目负责人工号,主导院系编号,计入月份,项目总奖金
            example1.append("2024001,智慧校园平台升级,101,DPT001,202407,25500");
            // 项目成员工号1,任务1,奖金比例1 (负责人) 9000 / 25500 = 0.3529 (approx)
            example1.append(",101,项目总负责与架构设计,0.3529");
            // 项目成员工号2,任务2,奖金比例2 6000 / 25500 = 0.2353 (approx)
            example1.append(",102,核心模块开发,0.2353");
            // 项目成员工号3,任务3,奖金比例3 5000 / 25500 = 0.1961 (approx)
            example1.append(",103,前端与测试,0.1961");
            // 项目成员工号4,任务4,奖金比例4 5500 / 25500 = 0.2157 (approx)
            example1.append(",104,数据库设计,0.2157");
            // 其余成员位置留空
            for (int i = 5; i <= 10; i++) {
                example1.append(",,,");
            }
            example1.append(",是");
            
            writer.println(example1.toString());
            
            // 示例数据行2 (参考 SQL 中 Project 2024002 和相关分配)
            // 总奖金: 8300 + 3000 + 4000 = 15300
            StringBuilder example2 = new StringBuilder();
            // 项目编号,项目名称,项目负责人工号,主导院系编号,计入月份,项目总奖金
            example2.append("2024002,企业级AI应用研发,102,DPT001,202407,15300");
            // 项目成员工号1,任务1,奖金比例1 (负责人) 8300 / 15300 = 0.5425 (approx)
            example2.append(",102,AI模型研发与集成,0.5425");
            // 项目成员工号2,任务2,奖金比例2 3000 / 15300 = 0.1961 (approx)
            example2.append(",101,技术指导,0.1961");
            // 项目成员工号3,任务3,奖金比例3 4000 / 15300 = 0.2614 (approx)
            example2.append(",105,数据标注与预处理,0.2614");
            // 其余成员位置留空
            for (int i = 4; i <= 10; i++) {
                example2.append(",,,");
            }
            example2.append(",是");
            
            writer.println(example2.toString());
            
            writer.flush();
            writer.close();
            
            logger.info("项目模板文件下载成功");
            
        } catch (IOException e) {
            logger.error("下载项目模板文件时发生错误", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void downloadTeacherTemplate(HttpServletResponse response) {
        try {
            // 设置响应头
            response.setContentType("text/csv; charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''teacher_template.csv");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");

            // 添加UTF-8 BOM标记，确保Excel正确显示中文
            response.getOutputStream().write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

            // 使用OutputStreamWriter确保正确的编码
            PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8),
                true
            );

            // CSV标题行
            StringBuilder header = new StringBuilder();
            header.append("工号,姓名,性别,出生日期,职称,职务,手机号,邮箱,院系编号,基本工资,密码");
            writer.println(header.toString());

            StringBuilder example1 = new StringBuilder();
            example1.append("101,张三丰,男,1979-01-01,教授,院长,13800138001,zhangsanfeng@example.com,DPT001,12000,password123");
            writer.println(example1.toString());

            StringBuilder example2 = new StringBuilder();
            example2.append("102,李若曦,女,1986-05-15,副教授,系主任,13900139002,liruoxi@example.com,DPT001,9000,securepass456");
            writer.println(example2.toString());
            
            StringBuilder example3 = new StringBuilder();
            example3.append("103,王五,男,1992-10-20,讲师,普通教师,13700137003,wangwu@example.com,DPT001,7000,testpass789");
            writer.println(example3.toString());

            writer.flush();
            writer.close();

            logger.info("教师模板文件下载成功");

        } catch (IOException e) {
            logger.error("下载教师模板文件时发生错误", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
