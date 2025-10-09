FROM rabbitmq:4.1-management

# Set a consistent working directory
WORKDIR /etc/rabbitmq

# Copy configuration files into the container
COPY ./config/rabbitmq.conf /etc/rabbitmq/rabbitmq.conf
COPY ./config/definitions.json /etc/rabbitmq/definitions.json

# Define healthcheck
HEALTHCHECK --interval=8s --timeout=5s --retries=5 --start-period=10s \
  CMD rabbitmq-diagnostics ping || exit 1

# Expose RabbitMQ ports
EXPOSE 5672 15672
