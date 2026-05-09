# 构建阶段
FROM node:20-alpine AS build
WORKDIR /app

# 配置npm淘宝镜像源
RUN npm config set registry https://registry.npmmirror.com

# 复制package文件
COPY package*.json ./

# 使用 npm ci 进行快速安装（要求package-lock.json存在）
RUN npm ci

# 复制源代码
COPY . .

# 构建应用
RUN npm run build

# 生产阶段
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# 删除默认配置
RUN rm -rf ./*

# 复制构建产物
COPY --from=build /app/dist .

# 复制nginx配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露端口
EXPOSE 3000

# 启动nginx
CMD ["nginx", "-g", "daemon off;"]
