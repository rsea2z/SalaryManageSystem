# 大作业 - 工资管理系统

## 目录

- [大作业 - 工资管理系统](#大作业---工资管理系统)
  - [目录](#目录)
  - [项目概述](#项目概述)
  - [主要功能](#主要功能)
  - [技术栈](#技术栈)
    - [后端](#后端)
    - [前端](#前端)
    - [数据库](#数据库)
  - [项目结构](#项目结构)
  - [API 文档](#api-文档)
  - [环境准备](#环境准备)
  - [部署与运行](#部署与运行)
    - [快速运行](#快速运行)
    - [后端](#后端-1)
    - [前端](#前端-1)
  - [构建项目](#构建项目)
    - [后端](#后端-2)
    - [前端](#前端-2)
  - [使用说明](#使用说明)
  - [Acknowledgements](#acknowledgements)
  - [License](#license)

## 项目概述

本项目是一个基于 Spring Boot 和 Vue.js 的前后端分离的工资管理系统。旨在提供一个便捷的教师工资管理、项目管理和信息查询平台。系统支持教师和管理员两种角色，各自拥有不同的操作权限。
由于懒得安装 IDEA ，本项目在 VS Code 中开发~

## 主要功能

-   **用户认证与授权**:
    -   基于 JWT 的登录认证机制。
    -   区分教师和管理员角色，实现权限控制。
-   **教师模块**:
    -   查看个人信息。
    -   查询每月工资详情（基本工资、项目工资、津贴等）。
    -   查看参与的项目列表及详情。
    -   工资构成图表可视化。
-   **管理员模块**:
    -   管理院系信息。
    -   管理教师信息（增删改查、导入导出 CSV）。
    -   管理项目信息（增删改查、分配项目成员、导入导出 CSV）。
    -   查看并管理所有教师的工资信息。
    -   查看院系项目及教师的统计信息。
-   **文件管理**:
    -   提供项目和教师信息的 CSV 模板下载。
    -   支持通过 CSV 文件批量导入项目和教师数据。

## 技术栈

### 后端

-   **框架**: Spring Boot ([`com.example.salarymanagementsystem.SalaryManagementSystemApplication`](src/main/java/com/example/salarymanagementsystem/SalaryManagementSystemApplication.java))
-   **数据持久化**: Spring Data JPA (Hibernate)
-   **安全性**: Spring Security
-   **API**: RESTful APIs
-   **构建工具**: Gradle ([`build.gradle`](build.gradle))
-   **编程语言**: Java

### 前端

-   **框架**: Vue.js 3 (Composition API, `<script setup>`) ([`frontend/src/main.js`](frontend/src/main.js))
-   **构建工具**: Vite ([`frontend/vite.config.js`](frontend/vite.config.js))
-   **状态管理**: Pinia ([`frontend/src/stores/`](frontend/src/stores/projectStore.js))
-   **UI 组件库**: Element Plus
-   **HTTP 请求**: Axios
-   **图表**: Chart.js ([`frontend/src/views/HomeView.vue`](frontend/src/views/HomeView.vue))
-   **编程语言**: JavaScript, HTML, CSS

### 数据库

-   MySQL (或其他兼容 JPA 的关系型数据库)
-   初始化数据脚本: [`data.sql`](data.sql)

## 项目结构

```
.
├── build.gradle        # 后端 Gradle 构建文件
├── data.sql            # 数据库初始化脚本
├── frontend            # 前端项目目录
│   ├── public
│   ├── src             # 前端源码
│   ├── index.html
│   ├── package.json    # 前端依赖管理
│   └── vite.config.js  # Vite 配置文件
├── src/main/java       # 后端 Java 源码
│   └── com/example/salarymanagementsystem
│       ├── controller  # API 控制器
│       ├── dto         # 数据传输对象
│       ├── model       # JPA 实体
│       ├── repository  # 数据仓库接口
│       ├── service     # 业务逻辑服务
│       ├── config      # Spring 配置类
│       └── SalaryManagementSystemApplication.java # Spring Boot 启动类
├── src/main/resources
│   └── application.properties # 后端配置文件
└── API.md              # API 详细文档
```

## API 文档

详细的 API 接口文档请参见项目根目录下的 [`API.md`](API.md) 文件。该文件描述了所有端点的 URL、HTTP 方法、权限要求、请求参数和响应结构。

## 环境准备

-   JDK 21 或更高版本
-   Node.js 16 或更高版本
-   MySQL 8.0 或更高版本
-   Gradle 7.x 或更高版本 (如果通过命令行构建后端)

## 部署与运行

### 快速运行
在 Windows 上，直接点击 `run.bat` 文件即可启动后端服务和前端开发服务器。

### 后端

1.  **配置数据库**:
    -   创建名为 `salary_management_system` (或您选择的名称) 的数据库。
    -   修改 [`src/main/resources/application.properties`](src/main/resources/application.properties) 文件，配置数据库连接信息 (URL, username, password)。
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/salary_management_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    spring.datasource.username=your_mysql_user
    spring.datasource.password=your_mysql_password
    ```
    -   配置 JWT 密钥:
    ```properties
    // filepath: src/main/resources/application.properties
    // ...existing code...
    jwt.secret=YOUR_VERY_STRONG_SECRET_KEY_PLEASE_CHANGE_IT 
    // ...existing code...
    ```
    -   生成新的 JWT 密钥:
    -   `python -c "import secrets, base64; print(base64.b64encode(secrets.token_bytes(64)).decode('utf-8'))"`
    -   或:
    -   `openssl rand -base64 64`

2.  **初始化数据库**:
    -   连接到您的 MySQL 数据库。(使用 Navicat 或其他数据库管理工具)
    -   执行 [`data.sql`](data.sql) 脚本以创建表结构并导入初始数据。
3.  **运行后端服务**:
    -   可以直接在 IDE (如 IntelliJ IDEA, Eclipse) 中运行 [`SalaryManagementSystemApplication.java`](src/main/java/com/example/salarymanagementsystem/SalaryManagementSystemApplication.java) 类。
    -   或者使用 Gradle:
      ```bash
      gradle bootRun
      ```
    -   后端服务默认运行在 `http://localhost:8080`。

### 前端

1.  **进入前端目录**:
    ```bash
    cd frontend
    ```
2.  **安装依赖**:
    ```bash
    npm install
    ```
3.  **运行开发服务器**:
    ```bash
    vue serve
    ```
    或
    ```bash
    npm run dev
    ```
    -   前端开发服务器默认运行在 `http://localhost:3000` (根据 [`frontend/vite.config.js`](frontend/vite.config.js) 配置)。
    -   前端应用会通过 Axios 调用 `http://localhost:8080/api` 下的后端接口 (根据 [`frontend/src/main.js`](frontend/src/main.js) 配置)。

## 构建项目

### 后端

使用 Gradle 构建可执行的 JAR 文件:

```bash
gradle bootRun
```

生成的 JAR 文件位于 `build/libs/` 目录下。您可以使用 `java -jar your-app-name.jar` 来运行它。

### 前端

构建用于生产环境的静态文件:

```bash
cd frontend
vue serve
```

生成的静态文件位于 `frontend/dist/` 目录下。您可以将这些文件部署到任何静态文件服务器 (如 Nginx, Apache) 或 Spring Boot 的 `static` 目录下。

## 使用说明

1.  **访问应用**: 打开浏览器，访问前端应用的地址 (默认为 `http://localhost:3000`)。
2.  **登录**:
    -   使用 [`data.sql`](data.sql) 中提供的教师或管理员账户登录。
    -   例如，教师 `101` 密码 `password123`。
3.  **功能操作**:
    -   **教师**: 登录后可以查看个人工资、项目信息等。
    -   **管理员**: 登录后可以进行院系、教师、项目和工资的管理操作。
    -   具体操作流程请参照系统界面提示。

## Acknowledgements
Expressions of gratitude are extended to Copilot.

## License
My pleasure if this project helps you.