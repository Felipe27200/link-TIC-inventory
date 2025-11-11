# Stage 1: Build Angular app
FROM node:20 AS builder
WORKDIR /app

# Copy this file separately to leverage
# Docker layer caching
COPY package*.json ./
RUN npm install

COPY . .

# -- --project inventory-frontend --configuration production: Uses for Angular CLI
# inventory-frontend is the project name. 
RUN npm run build -- --project inventory-frontend --configuration production

# Stage 2: serve app using Nginx
FROM nginx:alpine

# The build app will be copied on the nginx directory
COPY --from=builder /app/dist/inventory-frontend/browser/ /usr/share/nginx/html

# Copy your custom nginx config
# It's necessary to handle the Angular routes
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]