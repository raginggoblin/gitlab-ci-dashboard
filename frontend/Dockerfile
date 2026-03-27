FROM node:24.14.0-alpine AS build
WORKDIR /app
COPY . .
RUN npm ci --legacy-peer-deps --ignore-scripts && npm run build

FROM nginx:1.29.7-alpine

ENV FRONTEND_PORT=80
ENV BACKEND_HOST=gitlab-ci-dashboard-backend
ENV BACKEND_PORT=8080

COPY nginx.conf /etc/nginx/templates/default.conf.template
COPY --from=build /app/dist/gitlab-ci-dashboard/browser /usr/share/nginx/html

EXPOSE 80
