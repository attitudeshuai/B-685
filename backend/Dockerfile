# 构建阶段
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# 拷贝 Maven 加速配置
COPY settings.xml /usr/share/maven/ref/settings.xml

# 复制pom.xml并下载依赖
COPY pom.xml .
RUN mvn -s /usr/share/maven/ref/settings.xml dependency:go-offline -B

# 复制源代码
COPY src ./src

# 打包应用
RUN mvn -s /usr/share/maven/ref/settings.xml clean package -DskipTests -B

# 运行阶段 - 使用非alpine版本提高兼容性
FROM eclipse-temurin:17-jre
WORKDIR /app

# 创建日志目录
RUN mkdir -p /app/logs

# 复制jar包
COPY --from=build /app/target/*.jar app.jar

# 设置时区
ENV TZ=Asia/Shanghai

# 暴露端口
EXPOSE 8000

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8000/api/units || exit 1

# 启动命令
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]
