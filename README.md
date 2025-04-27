# 电影管理系统

一个现代化的电影管理和观看平台，支持用户观影、评论、收藏等功能，以及管理员对内容和用户的管理。

## 技术栈

### 前端
- Vue 3 - 渐进式 JavaScript 框架
- Ant Design Vue - UI组件库
- Axios - HTTP客户端
- Less - CSS预处理器
- Day.js - 时间处理库
- pnpm - 包管理工具
- Vite - 构建工具

### 后端
- Spring Boot - Java后端框架
- Spring Data JPA - ORM框架
- MySQL - 数据库
- Maven - 项目管理工具

## 项目结构

```
movie/
├── front-end/                # 前端项目目录
│   ├── src/
│   │   ├── assets/          # 静态资源
│   │   ├── components/      # 公共组件
│   │   ├── services/        # API服务
│   │   ├── router/          # 路由配置
│   │   ├── store/           # 状态管理
│   │   └── views/           # 页面组件
│   │       ├── admin/       # 管理员页面
│   │       └── user/        # 用户页面
│   ├── package.json         # 前端依赖配置
│   └── vite.config.js       # Vite配置
│
└── server/                   # 后端项目目录
    ├── src/main/
    │   ├── java/com/movie/
    │   │   ├── controller/  # 控制器
    │   │   ├── entity/      # 实体类
    │   │   ├── repository/  # 数据访问层
    │   │   ├── service/     # 业务逻辑层
    │   │   └── common/      # 公共工具类
    │   └── resources/
    │       └── application.properties  # 配置文件
    └── pom.xml              # Maven配置文件
```

## 功能特性

### 用户端
- 用户注册登录
- 电影浏览和搜索
- 电影收藏、点赞、评分、评论
- 付费观影
- 个性化推荐
- 观影历史记录

### 管理端
- 用户管理
- 电影管理
- 订单管理
- 评论管理
- 数据统计

## 环境要求

- Node.js 16+
- JDK 17+
- MySQL 8.0+
- Maven 3.8+
- pnpm 8.0+

## 安装说明

1. 克隆项目
```bash
git clone [项目地址]
cd movie
```

2. 配置数据库
```sql
CREATE DATABASE movie_system;
```

3. 配置后端
```bash
cd server
# 修改 application.properties 中的数据库配置
```

4. 安装前端依赖
```bash
cd front-end
pnpm install
```

5. 安装后端依赖
```bash
cd server
mvn install
```

## 启动说明

1. 启动后端服务
```bash
cd server
mvn spring-boot:run
```

2. 启动前端服务
```bash
cd front-end
pnpm dev
```

服务启动后：
- 前端访问地址：http://localhost:5173
- 后端接口地址：http://localhost:8080

## 开发规范

1. 代码规范
   - 前端遵循 Vue 3 组合式 API 规范
   - 后端遵循 RESTful API 设计规范
   - 使用统一的代码格式化工具

2. 提交规范
   - feat: 新功能
   - fix: 修复bug
   - docs: 文档更新
   - style: 代码格式化
   - refactor: 重构
   - test: 测试用例
   - chore: 构建过程或辅助工具的变动

## 目录命名规范

1. 前端
   - 组件文件：PascalCase (如 `UserProfile.vue`)
   - 工具文件：camelCase (如 `utils.js`)
   - 样式文件：kebab-case (如 `main-style.less`)

2. 后端
   - 包名：全小写 (如 `com.movie.controller`)
   - 类名：PascalCase (如 `UserController.java`)
   - 配置文件：kebab-case (如 `application-dev.properties`)

## 接口规范

所有接口统一返回格式：
```typescript
interface ApiResponse<T> {
  code: number;      // 状态码：200成功，400客户端错误，500服务器错误
  data: T;          // 返回数据
  message: string;  // 提示信息
}
```

## 贡献指南

1. Fork 项目
2. 创建特性分支
3. 提交代码
4. 创建 Pull Request

## 常见问题

1. 启动失败
   - 检查数据库配置是否正确
   - 检查端口是否被占用
   - 检查依赖是否安装完整

2. 编译错误
   - 检查 JDK 版本
   - 检查 Node.js 版本
   - 清理并重新安装依赖

## 许可证

[MIT License](LICENSE) 