# 自我健康管理系统 - 技术框架文档

## 一、项目概述

**项目名称**：Self-Health System（自我健康管理系统）

**项目类型**：前后端分离的 Web 应用（毕业设计）

**技术栈**：

| 层级 | 技术选型 |
|------|----------|
| 后端 | Java + Spring Boot + MyBatis |
| 前端 | Vue 2 + Element UI |
| 数据库 | MySQL 5.7 |
| 图表可视化 | ECharts |
| 认证机制 | JWT |
| 富文本编辑器 | WangEditor / Toast UI Editor |

---

## 二、系统架构图

### 2.1 整体架构

```mermaid
graph TB
    subgraph 客户端层 ["客户端层 (Vue 2)"]
        A[用户端 /user]
        B[管理端 /admin]
        C[登录页 /login]
        D[注册页 /register]
    end

    subgraph 网关层 ["网关层"]
        E[JWT 拦截器]
        F[路由守卫]
    end

    subgraph 服务层 ["服务层 (Spring Boot)"]
        G[Controller 层]
        H[Service 层]
        I[Mapper 层]
    end

    subgraph 数据层 ["数据层"]
        J[(MySQL 数据库)]
        K[流量统计表]
    end

    C --> E
    D --> E
    A --> E
    B --> E
    E --> F
    F --> G
    G --> H
    H --> I
    I --> J
    I --> K
```

### 2.2 前端路由架构

```mermaid
graph TD
    subgraph 公开路由
        A[/login] --> B[登录页]
        C[/register] --> D[注册页]
        E[/recipe-detail] --> F[食谱详情]
        G[/health-news-detail] --> H[资讯详情]
    end

    subgraph 用户端 ["用户端 /user (role=2)"]
        I[/user] --> J[主布局]
        J --> K[/home - 首页]
        J --> L[/recipe-list - 食谱列表]
        J --> M[/health-data - 健康数据]
        J --> N[/collection-folder - 收藏夹]
        J --> O[/health-record - 健康记录]
        J --> P[/my-diet - 我的饮食]
    end

    subgraph 管理端 ["管理端 /admin (role=1)"]
        Q[/admin] --> R[管理后台布局]
        R --> S[/admin-layout - 仪表盘]
        R --> T[/user-manage - 用户管理]
        R --> U[/health-news-manage - 资讯管理]
        R --> V[/health-model-manage - 健康模组]
        R --> W[/health-record-manage - 健康记录]
        R --> X[/evaluations-manage - 评论管理]
        R --> Y[/recipe-manage - 食谱管理]
        R --> Z[/diet-history-manage - 饮食记录]
    end
```

### 2.3 后端模块架构

```mermaid
graph TB
    subgraph Controller ["Controller 层"]
        A[UserController]
        B[HealthRecordController]
        C[RecipeController]
        D[HealthNewsController]
        E[HealthModelController]
        F[EvaluationsController]
        G[DashboardController]
        H[FileController]
    end

    subgraph Service ["Service 层"]
        I[UserService]
        J[HealthRecordService]
        K[RecipeService]
        L[HealthNewsService]
        M[HealthModelService]
        N[EvaluationsService]
        O[DashboardService]
    end

    subgraph Mapper ["Mapper 层"]
        P[UserMapper]
        Q[HealthRecordMapper]
        R[RecipeMapper]
        S[HealthNewsMapper]
        T[HealthModelMapper]
        U[EvaluationsMapper]
    end

    A --> I
    B --> J
    C --> K
    D --> L
    E --> M
    F --> N
    G --> O

    I --> P
    J --> Q
    K --> R
    L --> S
    M --> T
    N --> U
```

---

## 三、核心功能模块

### 3.1 用户模块

```mermaid
flowchart LR
    A[用户注册] --> B[输入信息]
    B --> C[后端验证]
    C --> D{验证结果}
    D -->|成功| E[写入数据库]
    D -->|失败| F[返回错误信息]
    E --> G[登录系统]
    G --> H[JWT 令牌]
    H --> I[后续请求携带 Token]
```

**功能**：
- 用户注册（普通用户 / 管理员）
- 用户登录（JWT 认证）
- 密码修改
- 头像上传

### 3.2 健康档案模块

```mermaid
flowchart TB
    A[健康档案] --> B[BMI 计算]
    A --> C[健康记录录入]
    A --> D[数据可视化]
    A --> E[历史查询]

    B --> B1[身高体重输入]
    B1 --> B2[公式计算]
    B2 --> B3[结果展示]

    C --> C1[血压/血糖/心率]
    C1 --> C2[数据存储]

    D --> D1[ECharts 折线图]
    D1 --> D2[趋势分析]

    E --> E1[按时间筛选]
    E1 --> E2[列表展示]
```

### 3.3 饮食管理模块

```mermaid
flowchart LR
    A[食谱浏览] --> B[分类筛选]
    B --> C[食谱详情]
    C --> D[营养信息]
    D --> E[收藏/记录]

    F[饮食记录] --> G[选择食谱]
    G --> H[输入摄入量]
    H --> I[保存记录]
    I --> J[历史查询]
```

### 3.4 推荐算法模块

```mermaid
flowchart TD
    A[推荐系统] --> B[基于用户的协同过滤]
    B --> C[计算用户相似度]
    C --> D[余弦相似度公式]
    D --> E[相似用户筛选]
    E --> F[推荐相似用户喜欢的食谱]
```

### 3.5 资讯与评论模块

```mermaid
flowchart TB
    A[健康资讯] --> B[资讯列表]
    A --> C[资讯详情]
    A --> D[分类管理]

    C --> E[发表评论]
    E --> F{回复类型}
    F -->|一级评论| G[直接发布]
    F -->|回复| H[关联父评论]

    G --> I[点赞功能]
    H --> I
    I --> J[收藏功能]
```

### 3.6 流量统计模块

```mermaid
flowchart LR
    A[流量指标] --> B[展现量]
    A --> C[浏览量]
    A --> D[点赞数]
    A --> E[收藏数]
    A --> F[停留时长]

    B --> G[数据采集]
    C --> G
    D --> G
    E --> G
    F --> G

    G --> H[统计分析]
    H --> I[ECharts 图表]
    I --> J[折线图/饼图]
```

---

## 四、数据库设计

### 4.1 核心表关系

```mermaid
erDiagram
    USER ||--o{ HEALTH_RECORD : "1对多"
    USER ||--o{ DIET_HISTORY : "1对多"
    USER ||--o{ RECIPE : "1对多"
    USER ||--o{ HEALTH_NEWS : "1对多"
    USER ||--o{ EVALUATIONS : "1对多"
    USER ||--o{ COLLECTION : "1对多"

    RECIPE ||--o{ DIET_HISTORY : "1对多"
    HEALTH_NEWS ||--o{ EVALUATIONS : "1对多"
    
    HEALTH_MODEL ||--o{ HEALTH_RECORD : "1对多"

    USER {
        int id PK
        string username
        string password
        string avatar
        int role
        datetime create_time
    }

    HEALTH_RECORD {
        int id PK
        int user_id FK
        int model_id FK
        double value
        datetime create_time
    }

    DIET_HISTORY {
        int id PK
        int user_id FK
        int recipe_id FK
        double value
        string detail
        datetime create_time
    }

    RECIPE {
        int id PK
        string title
        string content
        string type
        string image
        int collect_count
        datetime create_time
    }

    HEALTH_NEWS {
        int id PK
        string title
        string content
        string type
        string image
        datetime create_time
    }

    EVALUATIONS {
        int id PK
        int parent_id FK
        int commenter_id FK
        string content_type
        int content_id
        string content
        datetime create_time
    }
```

---

## 五、安全机制

### 5.1 JWT 认证流程

```mermaid
sequenceDiagram
    participant U as 用户
    participant F as 前端
    participant J as JWT拦截器
    participant C as Controller
    participant S as Service

    U->>F: 输入用户名密码
    F->>S: POST /user/login
    S->>S: 验证用户名密码
    S-->>F: 返回 JWT Token
    F->>U: 登录成功

    U->>F: 访问受保护资源
    F->>J: 请求携带 Token
    J->>J: 验证 Token 有效性
    J->>C: 验证通过，放行
    C-->>F: 返回数据
    F-->>U: 展示页面
```

### 5.2 路由权限控制

```mermaid
flowchart TB
    A[路由守卫] --> B{检查 Token}
    B -->|无 Token| C[跳转登录页]
    B -->|有 Token| D{检查角色}
    
    D -->|访问 /admin| E{role === 1?}
    D -->|访问 /user| F{role === 2?}
    
    E -->|是| G[放行]
    E -->|否| H[清除 Token 并跳转登录]
    
    F -->|是| G
    F -->|否| H
```

---

## 六、技术亮点

1. **前后端分离架构** - 解耦设计，独立部署
2. **JWT 无状态认证** - 支持分布式部署
3. **基于用户协同过滤推荐算法** - 个性化推荐
4. **ECharts 数据可视化** - 图表展示直观
5. **完善的权限控制** - 路由守卫 + 拦截器双重保护
6. **敏感词过滤** - Aho-Corasick 算法实现
7. **统一响应封装** - Result 统一返回格式

---

## 七、项目结构

```
self-health-system/
├── api/                    # Spring Boot 后端
│   ├── src/main/java/com/kmbeast/
│   │   ├── controller/    # 控制器
│   │   ├── service/       # 业务逻辑
│   │   ├── mapper/        # 数据访问
│   │   ├── pojo/          # 实体类
│   │   ├── utils/         # 工具类
│   │   ├── config/        # 配置类
│   │   ├── aop/           # 切面编程
│   │   └── exception/     # 异常处理
│
├── view/                   # Vue 2 前端
│   ├── src/
│   │   ├── views/         # 页面组件
│   │   │   ├── admin/     # 管理端
│   │   │   ├── user/      # 用户端
│   │   │   ├── login/     # 登录
│   │   │   └── register/ # 注册
│   │   ├── components/   # 公共组件
│   │   ├── router/        # 路由配置
│   │   ├── utils/         # 工具函数
│   │   └── assets/        # 静态资源
│   └── package.json
│
├── sql/                    # 数据库脚本
│   └── health.sql
│
└── docs/                  # 技术文档
    └── README.md
```

---

*文档版本：v1.0*  
*更新时间：2026-03-29*
