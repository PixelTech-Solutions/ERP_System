# ---- Build stage ----
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# ---- Runtime stage (nginx) ----
FROM nginx:1.27-alpine
# nginx.conf is a template; BACKEND_URL is filled in at startup.
COPY nginx.conf /etc/nginx/templates/default.conf.template
COPY --from=build /app/dist /usr/share/nginx/html
ENV BACKEND_URL=http://backend:8080
EXPOSE 80
# nginx:alpine auto-renders /etc/nginx/templates/*.template using envsubst.
CMD ["nginx", "-g", "daemon off;"]
