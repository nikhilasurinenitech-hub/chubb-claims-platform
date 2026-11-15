FROM node:24-alpine AS build
WORKDIR /workspace
COPY package*.json .npmrc ./
RUN npm ci
COPY . .
RUN npm run build
FROM nginxinc/nginx-unprivileged:stable-alpine
COPY nginx.conf /etc/nginx/templates/default.conf.template
COPY nginx-main.conf /etc/nginx/claimspace-nginx.conf
COPY container-entrypoint.sh /usr/local/bin/claimspace-entrypoint.sh
COPY --from=build /workspace/dist/shell/browser/ /usr/share/nginx/html/
COPY --from=build /workspace/dist/claimant/browser/ /usr/share/nginx/html/mfe/claimant/
COPY --from=build /workspace/dist/staff/browser/ /usr/share/nginx/html/mfe/staff/
ENV CLAIMS_UPSTREAM="claims-service:8081" INSIGHTS_UPSTREAM="insights-service:8082"
EXPOSE 8080
ENTRYPOINT ["/bin/sh","/usr/local/bin/claimspace-entrypoint.sh"]
