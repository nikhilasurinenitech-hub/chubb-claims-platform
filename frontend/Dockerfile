FROM node:24-alpine AS build
WORKDIR /workspace
COPY package*.json .npmrc ./
RUN npm ci
COPY . .
RUN npm run build
FROM nginxinc/nginx-unprivileged:stable-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /workspace/dist/shell/browser/ /usr/share/nginx/html/
COPY --from=build /workspace/dist/claimant/browser/ /usr/share/nginx/html/mfe/claimant/
COPY --from=build /workspace/dist/staff/browser/ /usr/share/nginx/html/mfe/staff/
EXPOSE 8080
