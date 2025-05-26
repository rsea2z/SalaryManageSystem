/*
New Sample Data SQL
Database: salary_management_system_db
*/

SET FOREIGN_KEY_CHECKS=0;

-- Drop tables in reverse order of dependency
DROP TABLE IF EXISTS `Teacher_project_assignment`;
DROP TABLE IF EXISTS `Salary`;
DROP TABLE IF EXISTS `Project`;
DROP TABLE IF EXISTS `Teacher`;
DROP TABLE IF EXISTS `Department`;

SET FOREIGN_KEY_CHECKS=1;

-- Table structure for Department
CREATE TABLE `Department` (
  `Department_number` varchar(255) NOT NULL COMMENT '院系编号',
  `Department_name` varchar(255) NOT NULL COMMENT '院系名称',
  `Administrator_id` int DEFAULT NULL COMMENT '财务负责人ID (外键, 指向Teacher.Teacher_number)',
  PRIMARY KEY (`Department_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='院系表';

-- Table structure for Teacher
CREATE TABLE `Teacher` (
  `Teacher_number` int NOT NULL COMMENT '教师编号, 作为登录用户名',
  `Teacher_name` varchar(255) NOT NULL COMMENT '教师姓名',
  `Password` varchar(255) NOT NULL COMMENT '加密后的密码',
  `Teacher_age` int DEFAULT NULL COMMENT '教师年龄',
  `Department_number` varchar(255) DEFAULT NULL COMMENT '外码，院系编号',
  `Post` varchar(255) DEFAULT NULL COMMENT '职称',
  `Teacher_gender` varchar(10) DEFAULT NULL COMMENT '性别',
  `Job` varchar(255) DEFAULT NULL COMMENT '职务',
  `Phone` varchar(255) DEFAULT NULL COMMENT '手机号',
  `Email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  PRIMARY KEY (`Teacher_number`),
  KEY `FK_Teacher_Department` (`Department_number`),
  CONSTRAINT `FK_Teacher_Department` FOREIGN KEY (`Department_number`) REFERENCES `Department` (`Department_number`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `CHK_Teacher_Age` CHECK ((`Teacher_age` >= 18)),
  CONSTRAINT `CHK_Teacher_Gender` CHECK ((`Teacher_gender` in ('男','女', '未知')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='教师表';

-- Add foreign key for Department.Administrator_id after Teacher table is created
ALTER TABLE `Department`
ADD CONSTRAINT `FK_Department_Administrator` FOREIGN KEY (`Administrator_id`) REFERENCES `Teacher` (`Teacher_number`) ON DELETE SET NULL ON UPDATE CASCADE;

-- Table structure for Project
CREATE TABLE `Project` (
  `Project_number` int NOT NULL COMMENT '项目编号',
  `Project_name` varchar(255) DEFAULT NULL COMMENT '项目名称',
  `completed_or_not` varchar(10) DEFAULT NULL COMMENT '是否计入工资（是否启用）',
  `Department_number` varchar(255) DEFAULT NULL COMMENT '院系编号',
  `Month` int DEFAULT NULL COMMENT '计入哪一月工资',
  `Leader` int DEFAULT NULL COMMENT '项目负责人ID (外键, 指向Teacher.Teacher_number)',
  PRIMARY KEY (`Project_number`),
  KEY `FK_Project_Department` (`Department_number`),
  KEY `FK_Project_Leader` (`Leader`),
  CONSTRAINT `FK_Project_Department` FOREIGN KEY (`Department_number`) REFERENCES `Department` (`Department_number`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `FK_Project_Leader` FOREIGN KEY (`Leader`) REFERENCES `Teacher` (`Teacher_number`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='项目表';

-- Table structure for Salary
CREATE TABLE `Salary` (
  `Teacher_number` int NOT NULL COMMENT '教师号',
  `Month` int NOT NULL COMMENT '月份 (例如 202407 代表 2024年7月)',
  `Salary_project` int DEFAULT 0 COMMENT '项目工资总和',
  `Salary_basic` int DEFAULT 0 COMMENT '基本工资',
  `Salary_payable` int DEFAULT 0 COMMENT '应付工资总和',
  `Salary_paid` int DEFAULT 0 COMMENT '实付工资总和',
  PRIMARY KEY (`Teacher_number`, `Month`),
  CONSTRAINT `FK_Salary_Teacher` FOREIGN KEY (`Teacher_number`) REFERENCES `Teacher` (`Teacher_number`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工资表';

-- Table structure for Teacher_project_assignment
CREATE TABLE `Teacher_project_assignment` (
  `Teacher_number` int NOT NULL COMMENT '教师编号',
  `Project_number` int NOT NULL COMMENT '项目编号',
  `Project_task` varchar(255) NOT NULL COMMENT '项目分工描述',
  `Project_salary` int DEFAULT 0 COMMENT '项目工资',
  `Project_salary_addition` int DEFAULT 0 COMMENT '项目工资加扣',
  `Comment` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`Teacher_number`, `Project_number`),
  KEY `FK_TPA_Project` (`Project_number`),
  CONSTRAINT `FK_TPA_Teacher` FOREIGN KEY (`Teacher_number`) REFERENCES `Teacher` (`Teacher_number`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_TPA_Project` FOREIGN KEY (`Project_number`) REFERENCES `Project` (`Project_number`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='教师项目分配表';

-- Insert sample data
SET FOREIGN_KEY_CHECKS=0; -- Disable temporarily for easier inserts if order is tricky

-- Departments
INSERT INTO `Department` (`Department_number`, `Department_name`, `Administrator_id`) VALUES
('DPT001', '计算机与软件学院', NULL), -- Admin ID will be updated later
('DPT002', '外国语学院', NULL),     -- Admin ID will be updated later
('DPT003', '数学与统计学院', NULL),
('DPT004', '物理与光电工程学院', NULL),
('DPT005', '经济管理学院', NULL);

-- Teachers
INSERT INTO `Teacher` (`Teacher_number`, `Teacher_name`, `Password`, `Teacher_age`, `Department_number`, `Post`, `Teacher_gender`, `Job`, `Phone`, `Email`) VALUES
(101, '张三丰', 'hashed_pass101', 45, 'DPT001', '教授', '男', '院长', '13800101001', 'zhangsf@example.com'),
(102, '李若曦', 'hashed_pass102', 38, 'DPT001', '副教授', '女', '系主任', '13900102002', 'lirx@example.com'),
(103, '王五', 'hashed_pass103', 32, 'DPT001', '讲师', '男', '财务负责人', '13700103003', 'wangwu@example.com'),
(104, '赵六', 'hashed_pass104', 50, 'DPT001', '教授', '男', '博导', '13700104004', 'zhaoliu@example.com'),
(105, '孙七', 'hashed_pass105', 28, 'DPT001', '助教', '女', '辅导员', '13700105005', 'sunqi@example.com'),
(201, '赵敏', 'hashed_pass201', 42, 'DPT002', '教授', '女', '院长', '13600201001', 'zhaom@example.com'),
(202, '周芷若', 'hashed_pass202', 29, 'DPT002', '讲师', '女', '教师', '13500202002', 'zhouzr@example.com'),
(203, '宋青书', 'hashed_pass203', 35, 'DPT002', '副教授', '男', '财务负责人', '13400203003', 'songqs@example.com'),
(204, '韦一笑', 'hashed_pass204', 48, 'DPT002', '副教授', '男', '系副主任', '13400204004', 'weiyx@example.com'),
(301, '欧阳锋', 'hashed_pass301', 55, 'DPT003', '教授', '男', '院长', '13300301001', 'ouyangf@example.com'),
(302, '黄药师', 'hashed_pass302', 52, 'DPT003', '教授', '男', '财务负责人', '13300302002', 'huangys@example.com'),
(303, '洪七公', 'hashed_pass303', 40, 'DPT003', '副教授', '男', '教师', '13300303003', 'hongqg@example.com'),
(401, '段智兴', 'hashed_pass401', 47, 'DPT004', '教授', '男', '院长', '13200401001', 'duanzx@example.com'),
(402, '瑛姑', 'hashed_pass402', 43, 'DPT004', '副教授', '女', '财务负责人', '13200402002', 'yinggu@example.com'),
(501, '郭靖', 'hashed_pass501', 39, 'DPT005', '副教授', '男', '院长', '13100501001', 'guojing@example.com'),
(502, '黄蓉', 'hashed_pass502', 36, 'DPT005', '教授', '女', '财务负责人', '13100502002', 'huangrong@example.com');


-- Update Department Administrator_id
UPDATE `Department` SET `Administrator_id` = 103 WHERE `Department_number` = 'DPT001';
UPDATE `Department` SET `Administrator_id` = 203 WHERE `Department_number` = 'DPT002';
UPDATE `Department` SET `Administrator_id` = 302 WHERE `Department_number` = 'DPT003';
UPDATE `Department` SET `Administrator_id` = 402 WHERE `Department_number` = 'DPT004';
UPDATE `Department` SET `Administrator_id` = 502 WHERE `Department_number` = 'DPT005';

-- Projects
INSERT INTO `Project` (`Project_number`, `Project_name`, `completed_or_not`, `Department_number`, `Month`, `Leader`) VALUES
(2024001, '智慧校园平台升级', '是', 'DPT001', 202407, 101),
(2024002, '企业级AI应用研发', '是', 'DPT001', 202407, 102),
(2024003, '多语种在线学习系统', '是', 'DPT002', 202407, 201),
(2024004, '虚拟现实教学资源开发', '否', 'DPT001', 202408, 101),
(2024005, '大数据分析平台构建', '是', 'DPT003', 202408, 301),
(2024006, '量子计算初步研究', '是', 'DPT004', 202408, 401),
(2024007, '金融风险智能预警模型', '是', 'DPT005', 202409, 501),
(2024008, '物联网安全协议研究', '否', 'DPT001', 202409, 104),
(2024009, '跨文化交流辅助工具开发', '是', 'DPT002', 202409, 204);

-- Teacher Project Assignments
INSERT INTO `Teacher_project_assignment` (`Teacher_number`, `Project_number`, `Project_task`, `Project_salary`, `Project_salary_addition`, `Comment`) VALUES
(101, 2024001, '项目总负责与架构设计', 8000, 1000, '项目负责人津贴'),
(102, 2024001, '核心模块开发', 6000, 0, NULL),
(103, 2024001, '前端与测试', 5000, 0, NULL),
(104, 2024001, '数据库设计', 5500, 0, NULL),
(101, 2024002, '技术指导', 3000, 0, NULL),
(102, 2024002, 'AI模型研发与集成', 7500, 800, '核心研发补贴'),
(105, 2024002, '数据标注与预处理', 4000, 0, NULL),
(201, 2024003, '项目策划与内容审核', 7000, 900, '项目负责人津贴'),
(202, 2024003, '课程资料翻译与整理', 5500, 0, NULL),
(203, 2024003, '系统测试与用户反馈', 4500, 0, NULL),
(204, 2024003, '多媒体内容制作', 4800, 0, NULL),
(101, 2024004, 'VR场景设计指导', 4000, 0, '项目未启用，预分配'),
(301, 2024005, '平台架构设计', 8500, 1200, '项目负责人津贴'),
(302, 2024005, '数据采集与清洗模块', 6500, 0, NULL),
(303, 2024005, '算法实现与优化', 7000, 500, '核心算法贡献'),
(401, 2024006, '理论研究与文献综述', 9000, 1500, '项目负责人津贴'),
(402, 2024006, '实验设计与模拟', 7000, 0, NULL),
(501, 2024007, '模型构建与验证', 8200, 1100, '项目负责人津贴'),
(502, 2024007, '金融数据分析', 6800, 0, NULL),
(104, 2024008, '安全协议调研', 3500, 0, '项目未启用，预分配'),
(204, 2024009, '需求分析与原型设计', 7200, 850, '项目负责人津贴'),
(202, 2024009, '语言模型集成', 6000, 0, NULL);

-- Salaries for July 2024 (202407)
INSERT INTO `Salary` (`Teacher_number`, `Month`, `Salary_project`, `Salary_basic`, `Salary_payable`, `Salary_paid`) VALUES
(101, 202407, (SELECT COALESCE(SUM(Project_salary + Project_salary_addition),0) FROM Teacher_project_assignment tpa JOIN Project p ON tpa.Project_number = p.Project_number WHERE tpa.Teacher_number = 101 AND p.Month = 202407 AND p.completed_or_not = '是'), 12000, 0, 0),
(102, 202407, (SELECT COALESCE(SUM(Project_salary + Project_salary_addition),0) FROM Teacher_project_assignment tpa JOIN Project p ON tpa.Project_number = p.Project_number WHERE tpa.Teacher_number = 102 AND p.Month = 202407 AND p.completed_or_not = '是'), 9000, 0, 0),
(103, 202407, (SELECT COALESCE(SUM(Project_salary + Project_salary_addition),0) FROM Teacher_project_assignment tpa JOIN Project p ON tpa.Project_number = p.Project_number WHERE tpa.Teacher_number = 103 AND p.Month = 202407 AND p.completed_or_not = '是'), 7000, 0, 0),
(104, 202407, (SELECT COALESCE(SUM(Project_salary + Project_salary_addition),0) FROM Teacher_project_assignment tpa JOIN Project p ON tpa.Project_number = p.Project_number WHERE tpa.Teacher_number = 104 AND p.Month = 202407 AND p.completed_or_not = '是'), 13000, 0, 0),
(105, 202407, (SELECT COALESCE(SUM(Project_salary + Project_salary_addition),0) FROM Teacher_project_assignment tpa JOIN Project p ON tpa.Project_number = p.Project_number WHERE tpa.Teacher_number = 105 AND p.Month = 202407 AND p.completed_or_not = '是'), 5000, 0, 0),
(201, 202407, (SELECT COALESCE(SUM(Project_salary + Project_salary_addition),0) FROM Teacher_project_assignment tpa JOIN Project p ON tpa.Project_number = p.Project_number WHERE tpa.Teacher_number = 201 AND p.Month = 202407 AND p.completed_or_not = '是'), 11000, 0, 0),
(202, 202407, (SELECT COALESCE(SUM(Project_salary + Project_salary_addition),0) FROM Teacher_project_assignment tpa JOIN Project p ON tpa.Project_number = p.Project_number WHERE tpa.Teacher_number = 202 AND p.Month = 202407 AND p.completed_or_not = '是'), 6500, 0, 0),
(203, 202407, (SELECT COALESCE(SUM(Project_salary + Project_salary_addition),0) FROM Teacher_project_assignment tpa JOIN Project p ON tpa.Project_number = p.Project_number WHERE tpa.Teacher_number = 203 AND p.Month = 202407 AND p.completed_or_not = '是'), 8000, 0, 0),
(204, 202407, (SELECT COALESCE(SUM(Project_salary + Project_salary_addition),0) FROM Teacher_project_assignment tpa JOIN Project p ON tpa.Project_number = p.Project_number WHERE tpa.Teacher_number = 204 AND p.Month = 202407 AND p.completed_or_not = '是'), 8500, 0, 0);

-- Update Salary_payable and Salary_paid for July 2024
UPDATE `Salary` SET `Salary_payable` = `Salary_project` + `Salary_basic`, `Salary_paid` = `Salary_project` + `Salary_basic` WHERE `Month` = 202407;

-- Salaries for August 2024 (202408) - Example with fewer projects
INSERT INTO `Salary` (`Teacher_number`, `Month`, `Salary_project`, `Salary_basic`, `Salary_payable`, `Salary_paid`) VALUES
(101, 202408, (SELECT COALESCE(SUM(Project_salary + Project_salary_addition),0) FROM Teacher_project_assignment tpa JOIN Project p ON tpa.Project_number = p.Project_number WHERE tpa.Teacher_number = 101 AND p.Month = 202408 AND p.completed_or_not = '是'), 12000, 0, 0), -- Project 2024004 is '否'
(102, 202408, (SELECT COALESCE(SUM(Project_salary + Project_salary_addition),0) FROM Teacher_project_assignment tpa JOIN Project p ON tpa.Project_number = p.Project_number WHERE tpa.Teacher_number = 102 AND p.Month = 202408 AND p.completed_or_not = '是'), 9000, 0, 0),
(201, 202408, (SELECT COALESCE(SUM(Project_salary + Project_salary_addition),0) FROM Teacher_project_assignment tpa JOIN Project p ON tpa.Project_number = p.Project_number WHERE tpa.Teacher_number = 201 AND p.Month = 202408 AND p.completed_or_not = '是'), 11000, 0, 0),
(301, 202408, (SELECT COALESCE(SUM(Project_salary + Project_salary_addition),0) FROM Teacher_project_assignment tpa JOIN Project p ON tpa.Project_number = p.Project_number WHERE tpa.Teacher_number = 301 AND p.Month = 202408 AND p.completed_or_not = '是'), 14000, 0, 0),
(302, 202408, (SELECT COALESCE(SUM(Project_salary + Project_salary_addition),0) FROM Teacher_project_assignment tpa JOIN Project p ON tpa.Project_number = p.Project_number WHERE tpa.Teacher_number = 302 AND p.Month = 202408 AND p.completed_or_not = '是'), 13500, 0, 0),
(303, 202408, (SELECT COALESCE(SUM(Project_salary + Project_salary_addition),0) FROM Teacher_project_assignment tpa JOIN Project p ON tpa.Project_number = p.Project_number WHERE tpa.Teacher_number = 303 AND p.Month = 202408 AND p.completed_or_not = '是'), 9500, 0, 0),
(401, 202408, (SELECT COALESCE(SUM(Project_salary + Project_salary_addition),0) FROM Teacher_project_assignment tpa JOIN Project p ON tpa.Project_number = p.Project_number WHERE tpa.Teacher_number = 401 AND p.Month = 202408 AND p.completed_or_not = '是'), 13000, 0, 0),
(402, 202408, (SELECT COALESCE(SUM(Project_salary + Project_salary_addition),0) FROM Teacher_project_assignment tpa JOIN Project p ON tpa.Project_number = p.Project_number WHERE tpa.Teacher_number = 402 AND p.Month = 202408 AND p.completed_or_not = '是'), 10000, 0, 0);


-- Update Salary_payable and Salary_paid for August 2024
UPDATE `Salary` SET `Salary_payable` = `Salary_project` + `Salary_basic`, `Salary_paid` = `Salary_project` + `Salary_basic` WHERE `Month` = 202408;

-- Salaries for September 2024 (202409)
INSERT INTO `Salary` (`Teacher_number`, `Month`, `Salary_project`, `Salary_basic`, `Salary_payable`, `Salary_paid`) VALUES
(101, 202409, (SELECT COALESCE(SUM(Project_salary + Project_salary_addition),0) FROM Teacher_project_assignment tpa JOIN Project p ON tpa.Project_number = p.Project_number WHERE tpa.Teacher_number = 101 AND p.Month = 202409 AND p.completed_or_not = '是'), 12000, 0, 0),
(104, 202409, (SELECT COALESCE(SUM(Project_salary + Project_salary_addition),0) FROM Teacher_project_assignment tpa JOIN Project p ON tpa.Project_number = p.Project_number WHERE tpa.Teacher_number = 104 AND p.Month = 202409 AND p.completed_or_not = '是'), 13000, 0, 0), -- Project 2024008 is '否'
(202, 202409, (SELECT COALESCE(SUM(Project_salary + Project_salary_addition),0) FROM Teacher_project_assignment tpa JOIN Project p ON tpa.Project_number = p.Project_number WHERE tpa.Teacher_number = 202 AND p.Month = 202409 AND p.completed_or_not = '是'), 6500, 0, 0),
(204, 202409, (SELECT COALESCE(SUM(Project_salary + Project_salary_addition),0) FROM Teacher_project_assignment tpa JOIN Project p ON tpa.Project_number = p.Project_number WHERE tpa.Teacher_number = 204 AND p.Month = 202409 AND p.completed_or_not = '是'), 8500, 0, 0),
(501, 202409, (SELECT COALESCE(SUM(Project_salary + Project_salary_addition),0) FROM Teacher_project_assignment tpa JOIN Project p ON tpa.Project_number = p.Project_number WHERE tpa.Teacher_number = 501 AND p.Month = 202409 AND p.completed_or_not = '是'), 12500, 0, 0),
(502, 202409, (SELECT COALESCE(SUM(Project_salary + Project_salary_addition),0) FROM Teacher_project_assignment tpa JOIN Project p ON tpa.Project_number = p.Project_number WHERE tpa.Teacher_number = 502 AND p.Month = 202409 AND p.completed_or_not = '是'), 11500, 0, 0);

-- Update Salary_payable and Salary_paid for September 2024
UPDATE `Salary` SET `Salary_payable` = `Salary_project` + `Salary_basic`, `Salary_paid` = `Salary_project` + `Salary_basic` WHERE `Month` = 202409;


SET FOREIGN_KEY_CHECKS=1; -- Re-enable foreign key checks

