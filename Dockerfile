# --- 阶段 1: 构建 Angular 前端 ---
FROM node:20-alpine AS frontend-builder
WORKDIR /app

# (关键修复) 先安装 pnpm 工具
RUN npm install -g pnpm

# 复制依赖描述文件以利用缓存
COPY frontend/package.json frontend/pnpm-lock.yaml ./
RUN pnpm install
# 复制所有剩余源代码
COPY frontend/ ./
RUN pnpm run build

# --- 阶段 2: 构建 Spring Boot 后端 ---
FROM maven:3.8.5-openjdk-8 AS backend-builder
WORKDIR /app
# 缓存 Maven 依赖
COPY backend/pom.xml .
RUN mvn dependency:go-offline
# 复制后端源代码
COPY backend/src ./src

# (关键修复) 从前端构建产物的 browser 子目录中复制内容
COPY --from=frontend-builder /app/dist/*/browser/* ./src/main/resources/static/

# 打包后端应用，此时前端文件已在 static 目录中
RUN mvn package -DskipTests

# --- 阶段 3: 创建最终的运行镜像 ---
FROM eclipse-temurin:8-jre-alpine
WORKDIR /app
# 使用通配符复制 JAR 包
COPY --from=backend-builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]