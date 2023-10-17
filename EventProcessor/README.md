# Kafka Connect

```bash
docker run -d \
  --name=kafka-connect \
  --net=host \
  -e CONNECT_BOOTSTRAP_SERVERS=localhost:9092 \
  -e CONNECT_REST_PORT=8082 \
  -e CONNECT_GROUP_ID="quickstart" \
  -e CONNECT_CONFIG_STORAGE_TOPIC="quickstart-config" \
  -e CONNECT_OFFSET_STORAGE_TOPIC="quickstart-offsets" \
  -e CONNECT_STATUS_STORAGE_TOPIC="quickstart-status" \
  -e CONNECT_KEY_CONVERTER="org.apache.kafka.connect.json.JsonConverter" \
  -e CONNECT_VALUE_CONVERTER="org.apache.kafka.connect.json.JsonConverter" \
  -e CONNECT_INTERNAL_KEY_CONVERTER="org.apache.kafka.connect.json.JsonConverter" \
  -e CONNECT_INTERNAL_VALUE_CONVERTER="org.apache.kafka.connect.json.JsonConverter" \
  -e CONNECT_REST_ADVERTISED_HOST_NAME="localhost" \
  -e CONNECT_PLUGIN_PATH=/usr/share/java \
  confluentinc/cp-kafka-connect:7.5.0
```
