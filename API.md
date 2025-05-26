# API 文档

## 1. 认证相关

### 登录
-   **URL**: `/api/auth/login`
-   **HTTP 方法**: POST
-   **权限**: 公开
-   **请求体**: `com.example.salarymanagementsystem.dto.LoginRequest` (JSON)
    ```json
    {
      "userid": "用户工号",
      "password": "密码"
    }
    ```
-   **响应**: `com.example.salarymanagementsystem.dto.JwtAuthResponse`
    ```json
    {
      "token": "JWT Token",
      "tokenType": "Bearer", // 例如 "Bearer"
      "userId": "用户ID",
      "userName": "用户名",
      "isAdmin": true, // 或 false
      "adminDepartmentNumber": "管理员院系编号" // 如果是管理员且有关联院系
    }
    ```
-   **实现位置**: `com.example.salarymanagementsystem.controller.AuthController.authenticateUser`

## 2. 教师相关

### 获取教师个人信息
-   **URL**: `/api/teacher/information/{teacherId}`
-   **HTTP 方法**: GET
-   **权限**: 本人或管理员 (`@PreAuthorize("isAuthenticated() and #teacherId.toString() == authentication.name or hasRole('ADMIN')")`)
-   **路径参数**:
    -   `teacherId` (Integer): 教师工号
-   **响应**: `com.example.salarymanagementsystem.dto.TeacherDto`
-   **实现位置**: `com.example.salarymanagementsystem.controller.TeacherController.getTeacherInformation`

### 判断是否为院系管理员
-   **URL**: `/api/teacher/admin_of_department/{teacherId}`
-   **HTTP 方法**: GET
-   **权限**: 本人 (`@PreAuthorize("isAuthenticated() and #teacherId.toString() == authentication.name")`)
-   **路径参数**:
    -   `teacherId` (Integer): 教师工号
-   **响应**: `com.example.salarymanagementsystem.dto.AdminDepartmentInfoDto`
-   **实现位置**: `com.example.salarymanagementsystem.controller.TeacherController.getAdminOfDepartment`

### 获取教师工资月份
-   **URL**: `/api/teacher/salary/month/{teacherId}`
-   **HTTP 方法**: GET
-   **权限**: 本人或管理员 (`@PreAuthorize("isAuthenticated() and #teacherId.toString() == authentication.name or hasRole('ADMIN')")`)
-   **路径参数**:
    -   `teacherId` (Integer): 教师工号
-   **响应**: `Set<Integer>` (包含有工资记录的月份集合)
-   **实现位置**: `com.example.salarymanagementsystem.controller.TeacherController.getSalaryMonths`

### 获取所有教师基础信息（管理员）
-   **URL**: `/api/teacher/all`
-   **HTTP 方法**: GET
-   **权限**: 管理员 (`@PreAuthorize("hasRole('ADMIN')")`)
-   **响应**: `List<com.example.salarymanagementsystem.dto.TeacherBasicDto>`
-   **实现位置**: `com.example.salarymanagementsystem.controller.TeacherController.getAllTeachersBasicInfo`

## 3. 工资相关

### 获取教师某月工资详情
-   **URL**: `/api/teacher/salary/{teacherNumber}/{month}`
-   **HTTP 方法**: GET
-   **权限**: 本人或管理员 (`@PreAuthorize("isAuthenticated() and #teacherNumber.toString() == authentication.name or hasRole('ADMIN')")`)
-   **路径参数**:
    -   `teacherNumber` (Integer): 教师工号
    -   `month` (Integer): 月份 (例如 202301)
-   **响应**: `com.example.salarymanagementsystem.dto.SalaryDto`
-   **实现位置**: `com.example.salarymanagementsystem.controller.SalaryController.getTeacherSalary`

## 4. 项目相关

### 教师查看自己参与的项目
-   **URL**: `/api/project/teacher/{teacherNumber}`
-   **HTTP 方法**: GET
-   **权限**: 本人 (`@PreAuthorize("isAuthenticated() and #teacherNumber.toString().equals(authentication.name.toString())")`)
-   **路径参数**:
    -   `teacherNumber` (Integer): 教师工号
-   **查询参数**:
    -   `month` (Integer, 可选): 月份 (例如 202301)，如果未提供或为0，则查询所有月份
-   **响应**: `List<com.example.salarymanagementsystem.dto.ProjectDto>`
-   **实现位置**: `com.example.salarymanagementsystem.controller.ProjectController.getTeacherProjects`

### 教师/管理员查看项目详情
-   **URL**: `/api/project/{projectId}/{teacherId}`
-   **HTTP 方法**: GET
-   **权限**: 本人或管理员 (`@PreAuthorize("(isAuthenticated() and #teacherId.toString().equals(authentication.name.toString())) or hasRole('ADMIN')")`)
-   **路径参数**:
    -   `projectId` (Integer): 项目编号
    -   `teacherId` (Integer): 教师工号 (用于权限验证和获取教师在该项目中的特定信息)
-   **响应**: `com.example.salarymanagementsystem.dto.ProjectDetailDto`
-   **实现位置**: `com.example.salarymanagementsystem.controller.ProjectController.getProjectDetailsForTeacher`

## 5. 院系（管理员）相关
所有在此分类下的 API 均需要管理员权限 (`@PreAuthorize("hasRole('ADMIN')")` 应用于 `DepartmentController` 类级别)。

### 获取所有院系
-   **URL**: `/api/department/projects/all`
-   **HTTP 方法**: GET
-   **权限**: 管理员
-   **响应**: `List<com.example.salarymanagementsystem.dto.DepartmentDto>`
-   **实现位置**: `com.example.salarymanagementsystem.controller.DepartmentController.getAllDepartments`

### 获取院系某月所有项目
-   **URL**: `/api/department/projects/{departmentNumber}/{month}`
-   **HTTP 方法**: GET
-   **权限**: 管理员
-   **路径参数**:
    -   `departmentNumber` (String): 院系编号
    -   `month` (Integer): 月份
-   **响应**: `List<com.example.salarymanagementsystem.dto.ProjectDto>`
-   **实现位置**: `com.example.salarymanagementsystem.controller.DepartmentController.getDepartmentProjectsByMonth`

### 按名称搜索院系项目
-   **URL**: `/api/department/projects/{departmentNumber}/{month}/{projectName}`
-   **HTTP 方法**: GET
-   **权限**: 管理员
-   **路径参数**:
    -   `departmentNumber` (String): 院系编号
    -   `month` (Integer): 月份
    -   `projectName` (String): 项目名称关键词
-   **响应**: `List<com.example.salarymanagementsystem.dto.ProjectDto>`
-   **实现位置**: `com.example.salarymanagementsystem.controller.DepartmentController.searchDepartmentProjectsByMonthAndName`

### 获取院系项目所有月份
-   **URL**: `/api/department/projects/month/{departmentNumber}`
-   **HTTP 方法**: GET
-   **权限**: 管理员
-   **路径参数**:
    -   `departmentNumber` (String): 院系编号
-   **响应**: `Set<Integer>` (包含项目记录的月份集合)
-   **实现位置**: `com.example.salarymanagementsystem.controller.DepartmentController.getDepartmentProjectMonths`

### 新增/编辑项目
-   **URL**: `/api/department/project`
-   **HTTP 方法**: POST
-   **权限**: 管理员
-   **请求体**: `com.example.salarymanagementsystem.dto.ProjectUpsertDto`
-   **响应**: `com.example.salarymanagementsystem.dto.ProjectUpsertDto`
-   **实现位置**: `com.example.salarymanagementsystem.controller.DepartmentController.createOrUpdateProject`

### 删除项目
-   **URL**: `/api/department/project/{projectNumber}`
-   **HTTP 方法**: DELETE
-   **权限**: 管理员
-   **路径参数**:
    -   `projectNumber` (Integer): 项目编号
-   **响应**: `com.example.salarymanagementsystem.dto.ApiResponse`
-   **实现位置**: `com.example.salarymanagementsystem.controller.DepartmentController.deleteProject`

### 获取项目详情（管理员）
-   **URL**: `/api/department/project/{projectNumber}`
-   **HTTP 方法**: GET
-   **权限**: 管理员
-   **路径参数**:
    -   `projectNumber` (Integer): 项目编号
-   **响应**: `com.example.salarymanagementsystem.dto.ProjectDetailDto`
-   **实现位置**: `com.example.salarymanagementsystem.controller.DepartmentController.getProjectDetailsForAdmin`

### 获取院系教师列表
-   **URL**: `/api/department/teachers/{departmentNumber}/{month}`
-   **HTTP 方法**: GET
-   **权限**: 管理员
-   **路径参数**:
    -   `departmentNumber` (String): 院系编号
    -   `month` (Integer): 月份 (用于查询该月工资相关的教师信息，若为0或不关心特定月份，服务层可能处理为查询所有教师)
-   **响应**: `List<com.example.salarymanagementsystem.dto.AdminTeacherViewDto>`
-   **实现位置**: `com.example.salarymanagementsystem.controller.DepartmentController.getDepartmentTeachers`

### 按姓名搜索院系教师
-   **URL**: `/api/department/teachers/{departmentNumber}/{month}/{teacherName}`
-   **HTTP 方法**: GET
-   **权限**: 管理员
-   **路径参数**:
    -   `departmentNumber` (String): 院系编号
    -   `month` (Integer): 月份
    -   `teacherName` (String): 教师姓名关键词
-   **响应**: `List<com.example.salarymanagementsystem.dto.AdminTeacherViewDto>`
-   **实现位置**: `com.example.salarymanagementsystem.controller.DepartmentController.searchDepartmentTeachers`

### 新增/编辑教师
-   **URL**: `/api/department/teacher`
-   **HTTP 方法**: POST
-   **权限**: 管理员
-   **请求体**: `com.example.salarymanagementsystem.dto.TeacherUpsertDto`
-   **响应**: `com.example.salarymanagementsystem.dto.TeacherUpsertDto`
-   **实现位置**: `com.example.salarymanagementsystem.controller.DepartmentController.createOrUpdateTeacher`

### 删除教师
-   **URL**: `/api/department/teacher/{teacherId}`
-   **HTTP 方法**: DELETE
-   **权限**: 管理员
-   **路径参数**:
    -   `teacherId` (Integer): 教师工号
-   **响应**: `com.example.salarymanagementsystem.dto.ApiResponse`
-   **实现位置**: `com.example.salarymanagementsystem.controller.DepartmentController.deleteTeacher`

### 获取教师详情（管理员）
-   **URL**: `/api/department/teacher/detail/{teacherId}`
-   **HTTP 方法**: GET
-   **权限**: 管理员
-   **路径参数**:
    -   `teacherId` (Integer): 教师工号
-   **响应**: `com.example.salarymanagementsystem.dto.AdminTeacherDetailDto`
-   **实现位置**: `com.example.salarymanagementsystem.controller.DepartmentController.getTeacherDetailForAdmin`

## 6. 文件相关

### 下载项目模板
-   **URL**: `/api/files/download/template/project`
-   **HTTP 方法**: GET
-   **权限**: 管理员 (`@PreAuthorize("hasRole('ADMIN')")`，尽管 `SecurityConfig` 中有 `permitAll()`，方法级别的权限控制会生效)
-   **响应**: CSV 文件 (项目导入模板)
-   **实现位置**: `com.example.salarymanagementsystem.controller.FileController.downloadProjectTemplate`

### 下载教师模板
-   **URL**: `/api/files/download/template/teacher`
-   **HTTP 方法**: GET
-   **权限**: 管理员 (`@PreAuthorize("hasRole('ADMIN')")`)
-   **响应**: CSV 文件 (教师导入模板)
-   **实现位置**: `com.example.salarymanagementsystem.controller.FileController.downloadTeacherTemplate`

## 7. 其他说明
-   **所有接口均以 `/api` 开头，需携带 JWT Token（除登录、模板下载等特定配置为公开的接口外）。**
-   **DTO 结构详见 `com.example.salarymanagementsystem.dto` 包。**
-   **Controller 实现位置见 `com.example.salarymanagementsystem.controller` 包。**
-   **如需上传 CSV、批量导入等功能，请参考前端 `el-upload` 组件的 action 路径及后端 Controller (例如项目导入功能在 `ProjectServiceImpl.importProjectsFromCsv` 中实现，但具体的上传 Controller 端点未在当前信息中明确列出，通常会是一个 POST 请求到类似 `/api/department/project/import` 的路径)。**