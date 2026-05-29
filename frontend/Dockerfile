# Stage 1: Build stage
FROM node:20-alpine AS build
WORKDIR /app

# Copy package descriptor and install dependencies (exclude package-lock.json for clean platform resolution)
COPY package.json ./
RUN npm install

# Copy source code and build React app
COPY . .
RUN npm run build

# Stage 2: Serve stage using Nginx
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html

# Copy custom nginx configuration for routing & proxying
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose HTTP port
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
