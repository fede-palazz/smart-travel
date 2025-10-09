FROM quay.io/debezium/server:3.2.0

# Set working directory for clarity
WORKDIR /debezium

# Copy Debezium configuration and custom startup script
COPY ./config /debezium/config

# Set static environment variables
ENV JAVA_TOOL_OPTIONS="-Dlog4j2.configurationFile=/debezium/conf/log4j2.xml"

# Set custom script as container entrypoint
ENTRYPOINT ["/debezium/config/wait-for-rabbitmq.sh"]

# Expose Debezium's HTTP port
EXPOSE 8080
