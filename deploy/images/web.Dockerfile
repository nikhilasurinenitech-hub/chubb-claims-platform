FROM nginxinc/nginx-unprivileged:stable-alpine
COPY nginx.conf /etc/nginx/templates/default.conf.template
COPY nginx-main.conf /etc/nginx/claimspace-nginx.conf
COPY container-entrypoint.sh /usr/local/bin/claimspace-entrypoint.sh
COPY site/ /usr/share/nginx/html/
ENV CLAIMS_UPSTREAM="claims-service:8081" INSIGHTS_UPSTREAM="insights-service:8082"
EXPOSE 8080
ENTRYPOINT ["/bin/sh","/usr/local/bin/claimspace-entrypoint.sh"]
