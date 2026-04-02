# SpringBoot+Vue 个人健康系统

## 项目简介

这是一个大学计算机系软件工程专业的计算机学生的毕业设计项目。

## 技术栈

- **后端**：SpringBoot + Mybatis Plus
- **前端**：Vue.js
- **数据库**：MySQL

## 项目结构

```
source code/
├── src/                    # 源代码目录
│   ├── api/               # SpringBoot 后端项目
│   ├── view/              # Vue 前端项目
│   └── sql/               # 数据库脚本
├── config/                 # 配置文件目录
│   ├── .vscode/           # VS Code 配置备份
│   └── god key/           # 密钥配置
├── docs/                   # 文档目录
│   ├── 对话记录/          # 工作日志和对话记录
│   ├── 论文/              # 毕业论文相关文档
│   ├── Learning/          # 学习资料
│   └── 个人计划表/        # 个人计划
├── temp/                   # 临时文件目录
├── .vscode/               # VS Code 项目配置
├── .gitignore             # Git 忽略规则
└── README.md              # 项目说明文档
```

## 快速开始

### 后端启动

1. 进入后端目录：
   ```bash
   cd src/api
   ```

2. 配置数据库连接（编辑 `src/main/resources/application.yml`）

3. 运行 SpringBoot 应用：
   ```bash
   mvn spring-boot:run
   ```

后端服务将在 `http://localhost:21090` 启动

### 前端启动

1. 进入前端目录：
   ```bash
   cd src/view
   ```

2. 安装依赖：
   ```bash
   npm install
   ```

3. 启动开发服务器：
   ```bash
   npm run serve
   ```

前端服务将在 `http://localhost:21091` 启动

### 数据库初始化

执行 `src/sql/health.sql` 脚本初始化数据库：
```bash
mysql -u root -p < src/sql/health.sql
```

## 开发环境要求

- JDK 8+
- Node.js 14+
- Maven 3.6+
- MySQL 5.7+

## 作者

杭州电子科技大学 信息工程学院 软件工程专业
指导教师：陈鑫
