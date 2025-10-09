FROM nginx:1.29

# Copy custom nginx config
COPY nginx.conf /etc/nginx/nginx.conf
COPY default.conf /etc/nginx/conf.d/default.conf

# Support running as arbitrary user which belogs to the root group
RUN chmod g+rwx /var/cache/nginx /var/run /var/log/nginx

# Copy built Angular app from Jenkins workspace
COPY dist/frontend/browser /usr/share/nginx/html

# Expose port
EXPOSE 8088

# Start Nginx server
CMD ["nginx", "-g", "daemon off;"]