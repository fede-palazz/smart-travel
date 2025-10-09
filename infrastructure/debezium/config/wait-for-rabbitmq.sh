#!/bin/bash

set -e

HOST=${BROKER_HOST:-rabbitmq}
PORT=${BROKER_PORT:-5672}
TIMEOUT=${WAIT_TIMEOUT:-60}

echo "Waiting for RabbitMQ at $HOST:$PORT..."

for i in $(seq 1 $TIMEOUT); do
  if (echo > /dev/tcp/$HOST/$PORT) >/dev/null 2>&1; then
    echo "RabbitMQ is up!"
    break
  fi
  echo "Waiting... ($i)"
  sleep 1
done

if [ "$i" -eq "$TIMEOUT" ]; then
  echo "Timeout! RabbitMQ not available after $TIMEOUT seconds"
  exit 1
fi

# Run Debezium server manually
echo "Starting Debezium Server..."
exec /debezium/run.sh