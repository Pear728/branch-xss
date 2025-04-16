# XSS防御系统

## 项目结构

```bash
xss-defense-system/
│
├── backend/ # 后端代码目录
│ ├── src/ # 源代码目录
│ │ ├── main/ # 主代码目录
│ │ │ ├── java/ # Java源代码
│ │ │ │ └── com/xssdefense/system/ # 包含所有业务逻辑
│ │ │ ├── resources/ # 资源文件
│ │ │ │ ├── db/ # 数据库迁移脚本
│ │ │ │ └── application.properties # 配置文件
│ │ │ └── ...
│ │ └── test/ # 测试代码目录
│ │ └── java/ # 测试用例
│ │
│ ├── pom.xml # Maven项目配置文件
│ └── ...
│
├── frontend/ # 前端代码目录
│ ├── src/ # 源代码目录
│ │ ├── api/ # API请求模块
│ │ ├── components/ # Vue组件
│ │ ├── directives/ # 自定义指令
│ │ ├── router/ # 路由配置
│ │ ├── store/ # Vuex状态管理
│ │ ├── utils/ # 工具函数
│ │ ├── views/ # 页面视图
│ │ ├── App.vue # 主应用组件
│ │ └── main.js # 应用入口文件
│ │
│ ├── package.json # 项目依赖和脚本
│ ├── .eslintrc.js # ESLint配置文件
│ └── ...
│
└── README.md # 项目说明文档
 ```

## 项目说明

本项目是一个XSS攻击分析和防御系统，旨在帮助用户检测和防御Web应用程序中的XSS攻击。项目分为前端和后端两部分，前端使用Vue.js构建，后端使用Spring Boot。

## 安装和使用

### 后端

1. 修改数据库配置：
   进入
   ```bash
   cd backend/src/main/resources/application.properties
   ```
   文件，修改数据库配置 在resources目录下db文件夹下有数据库建表语句，导入到数据库即可。


3. 进入后端目录：
   ```bash
   cd xss-defense-system/backend
   ```

4. 使用Maven构建项目：
   ```bash
   mvn clean install
   ```

5. 启动后端服务：
   ```bash
   mvn spring-boot:run
   ```

### 前端

1. 进入前端目录：
   ```bash
   cd xss-defense-system/frontend
   ```

2. 安装依赖：
   ```bash
   npm install
   ```

3. 启动前端开发服务器：
   ```bash
   npm run dev
   ```

## 贡献

欢迎任何形式的贡献！请提交问题或拉取请求。

## 许可证

本项目采用MIT许可证，详细信息请查看LICENSE文件。
