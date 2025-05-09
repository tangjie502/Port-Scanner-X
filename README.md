好的，很乐意为您编写一份适用于 GitHub 仓库主页的 Markdown 格式文档 (`README.md`)。这份文档将基于我们之前讨论的项目功能和最终确定的 Docker 开发部署方式。

```markdown
# Port Sentinel X - 端口扫描与分析工具

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
一款现代化的网络端口扫描工具，采用 Spring Boot 后端和 Vue.js 前端构建，并通过 Docker 进行容器化部署。它允许用户指定 IP 地址、端口范围或常用端口列表，以多线程方式快速扫描目标主机暴露的端口，并提供美观、动态的用户界面来展示扫描结果。

![应用截图占位符 - 建议替换为您的应用截图](https://via.placeholder.com/800x450.png?text=Port+Sentinel+X+Application+Screenshot)
---

## ✨ 主要功能 (Features)

* **IP 地址扫描**: 支持输入单个 IPv4 地址或主机名进行扫描。
* **灵活的端口选择**:
    * 用户可自定义端口范围 (例如 `1-1024`)。
    * 用户可输入特定的、以逗号分隔的端口列表 (例如 `80,443,3000`)。
    * 提供预设的常用端口列表供用户快速选择。
* **多线程扫描**: 用户可自定义扫描时使用的线程数，以加快扫描进度。
* **超时设置**: 用户可自定义每个端口连接的超时时间 (毫秒)。
* **美观的动态界面**:
    * 采用 Vue.js 3 和 Tailwind CSS 构建，界面现代、响应式。
    * 包含粒子动画背景和毛玻璃效果，提升视觉体验。
    * 动态加载指示和 Toast 通知，提供友好的用户反馈。
* **清晰的结果展示**: 分别展示开放端口和关闭/错误/超时端口的列表。
* **容器化部署**: 使用 Docker 和 Docker Compose，方便部署和管理。

---

## 🛠️ 技术栈 (Tech Stack)

* **后端 (Backend)**:
    * Java 17
    * Spring Boot 3.x (用于构建 RESTful API)
    * Maven (项目构建和依赖管理)
    * `java.net.Socket` (核心端口扫描逻辑)
    * `java.util.concurrent` (多线程处理)
* **前端 (Frontend)**:
    * Vue.js 3 (组合式 API, `<script setup>`)
    * Vite (或 Vue CLI，取决于您的最终选择)
    * Tailwind CSS (功能类优先的 CSS 框架)
    * Axios (HTTP 请求)
    * `vue3-particles` (背景粒子动画)
    * `vue-toastification` (通知提示)
* **开发与部署 (Development & Deployment)**:
    * Docker
    * Docker Compose

---

## 📂 项目结构 (Project Structure)

项目主要包含两个核心模块：

* `port-scanner-backend/`: Spring Boot 后端应用。
* `port-scanner-ui/`: Vue.js 前端应用。

每个模块都有其独立的 `Dockerfile` 用于构建镜像，并通过根目录下的 `docker-compose.yml` 文件进行编排。

---

## 🚀 开始使用 (Getting Started)

### 前提条件 (Prerequisites)

确保您的开发环境中已安装以下工具：

* **Java JDK 17+**: 用于运行和构建后端。([OpenJDK](https://openjdk.java.net/) / [Amazon Corretto](https://aws.amazon.com/corretto/))
* **Maven 3.6+**: 用于构建后端。([Apache Maven](https://maven.apache.org/))
* **Node.js 18+ (或 LTS 版本)**: 用于运行和构建前端。([Node.js](https://nodejs.org/))
* **npm 8+ / yarn**: Node.js 包管理器。
* **Docker**: 用于容器化运行应用。([Docker Desktop](https://www.docker.com/products/docker-desktop/))
* **Docker Compose**: 用于编排多容器应用 (通常随 Docker Desktop 一起安装)。

### 本地开发运行 (Local Development)

#### 1. 后端 (Spring Boot)

```bash
# 克隆仓库 (如果尚未克隆)
# git clone [https://github.com/](https://github.com/)[您的GitHub用户名]/[您的仓库名].git
# cd [您的仓库名]/port-scanner-backend

# 使用 Maven 构建并运行 Spring Boot 应用
./mvnw spring-boot:run
# 或者 (如果您全局安装了 Maven)
# mvn spring-boot:run
```
后端服务默认将在 `http://localhost:8081` 上运行。

#### 2. 前端 (Vue.js)

```bash
# 进入前端项目目录
# cd ../port-scanner-ui

# 安装依赖
npm install
# 或 yarn install

# 启动 Vue 开发服务器
npm run serve
# 或 yarn serve
```
前端开发服务器通常在 `http://localhost:8080` 或 `http://localhost:8082` (具体看终端输出) 上运行。
在 `port-scanner-ui` 目录下，确保您有一个 `.env.development` 文件，内容如下，以指向本地后端：
```
VUE_APP_API_BASE_URL=http://localhost:8081/api
```

### 使用 Docker 和 Docker Compose 运行 (推荐)

这是推荐的运行方式，因为它模拟了生产环境，并处理了服务间的网络连接。

1.  **构建后端 JAR 文件 (如果尚未构建)**:
    ```bash
    cd port-scanner-backend
    ./mvnw clean package -DskipTests
    # 或 mvn clean package -DskipTests
    cd ..
    ```

2.  **在项目根目录下创建 Dockerfile 和 docker-compose.yml 文件**

    **`port-scanner-backend/Dockerfile`**:
    ```dockerfile
    # 使用包含 JRE 的 Java 17 基础镜像
    FROM eclipse-temurin:17-jre-jammy

    # 设置工作目录
    WORKDIR /app

    # 将构建好的 JAR 文件从 target 目录复制到镜像中
    # 确保 JAR 文件名与您实际构建的匹配
    COPY target/port-scanner-backend-0.0.1-SNAPSHOT.jar app.jar

    # 暴露后端服务端口
    EXPOSE 8081

    # 容器启动时运行 JAR 文件
    ENTRYPOINT ["java", "-jar", "/app/app.jar"]
    ```

    **`port-scanner-ui/Dockerfile` (多阶段构建)**:
    ```dockerfile
    # ---- 构建阶段 ----
    FROM node:18-alpine as build-stage

    WORKDIR /app

    # 复制 package.json 和 package-lock.json (或 yarn.lock)
    COPY package*.json ./
    # COPY yarn.lock ./ # 如果使用 yarn

    # 安装依赖
    RUN npm install
    # RUN yarn install # 如果使用 yarn

    # 复制应用源代码
    COPY . .

    # 构建生产版本的静态文件 (VUE_APP_API_BASE_URL 可以通过构建参数或后续 Nginx 配置覆盖)
    # 如果您在 .env.production 中设置了 VUE_APP_API_BASE_URL=/api，这里通常不需要额外参数
    RUN npm run build

    # ---- 服务阶段 ----
    FROM nginx:stable-alpine as production-stage

    # 复制构建好的静态文件到 Nginx 的 HTML 目录
    COPY --from=build-stage /app/dist /usr/share/nginx/html

    # (可选) 复制自定义的 Nginx 配置文件以支持 SPA 路由
    # COPY nginx.conf /etc/nginx/conf.d/default.conf

    EXPOSE 80

    CMD ["nginx", "-g", "daemon off;"]
    ```

    **(可选) `port-scanner-ui/nginx.conf` (用于支持 Vue Router 的 history 模式):**
    ```nginx
    server {
        listen 80;
        server_name localhost;

        root /usr/share/nginx/html;
        index index.html index.htm;

        location / {
            try_files $uri $uri/ /index.html;
        }

        # (可选) 将 API 请求代理到后端服务
        # location /api/ {
        #     proxy_pass http://port-scanner-backend:8081/api/; # "port-scanner-backend" 是 docker-compose 中的服务名
        #     proxy_set_header Host $host;
        #     proxy_set_header X-Real-IP $remote_addr;
        #     proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        #     proxy_set_header X-Forwarded-Proto $scheme;
        # }
    }
    ```
    *如果使用 Nginx 代理 API，前端的 `VUE_APP_API_BASE_URL` 应设为 `/api`。*

    **项目根目录 `docker-compose.yml`**:
    ```yaml
    version: '3.8'

    services:
      port-scanner-backend:
        build: ./port-scanner-backend
        container_name: port-scanner-backend-app
        ports:
          - "8081:8081" # 将容器的 8081 端口映射到主机的 8081 端口
        restart: unless-stopped
        networks:
          - port_scanner_network

      port-scanner-frontend:
        build: ./port-scanner-ui
        container_name: port-scanner-frontend-app
        ports:
          - "8080:80" # 将容器的 80 端口 (Nginx) 映射到主机的 8080 端口
        depends_on:
          - port-scanner-backend
        restart: unless-stopped
        environment:
          # 如果不通过 Nginx 代理 API，且前端需要直接访问后端容器的地址：
          # VUE_APP_API_BASE_URL: http://port-scanner-backend:8081/api 
          # 注意：这个环境变量需要被构建进 Vue 应用，或者由 Nginx 运行时注入到 index.html
          # 对于 Vue CLI 构建的应用，环境变量在构建时嵌入。
          # 如果使用 Nginx 代理 API (如上面的 nginx.conf 示例)，则前端 VUE_APP_API_BASE_URL 应为 /api
          # 确保 port-scanner-ui/Dockerfile 构建时，.env 文件中的 VUE_APP_API_BASE_URL=/api
          # 或者，在构建前端镜像时通过 build-arg 传入
          # args:
          #   VUE_APP_API_BASE_URL: /api
        networks:
          - port_scanner_network

    networks:
      port_scanner_network:
        driver: bridge
    ```

3.  **使用 Docker Compose 构建并启动服务**:
    在包含 `docker-compose.yml` 的项目根目录下运行：
    ```bash
    docker-compose up --build
    ```
    第一次构建可能需要一些时间。成功后：
    * 前端应用将通过 `http://localhost:8080` 访问。
    * 后端 API 将在 `http://localhost:8081` 可用 (也可被前端容器通过 `http://port-scanner-backend:8081` 访问)。

---

## 🔧 配置 (Configuration)

* **后端**: 主要配置在 `port-scanner-backend/src/main/resources/application.properties` 中，例如 `server.port`。
* **前端**: API 地址通过 `.env` 文件中的 `VUE_APP_API_BASE_URL` 进行配置。扫描参数 (IP, 端口, 线程数, 超时) 均通过 UI 输入。

---

## 🤝 贡献 (Contributing)

欢迎为此项目做出贡献！如果您有任何改进建议或发现 bug，请随时：

1.  Fork 本仓库
2.  创建您的 Feature 分支 (`git checkout -b feature/AmazingFeature`)
3.  提交您的更改 (`git commit -m 'Add some AmazingFeature'`)
4.  推送到分支 (`git push origin feature/AmazingFeature`)
5.  开启一个 Pull Request

---

## 📄 许可证 (License)

该项目采用 [MIT 许可证](https://opensource.org/licenses/MIT)授权。详情请参阅 `LICENSE` 文件。
---

## 🙏 致谢 (Acknowledgements) (可选)

* 感谢所有使用的开源库和工具。
* ...

---

**作者**: [tangjie502] ([https://github.com/tangjie502/Port-Scanner-X])

```