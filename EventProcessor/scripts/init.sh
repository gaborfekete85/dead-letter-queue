confluent local services start
# --config cleanup.policy=compact

kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic pcmproser_pcoevents_serviceagreement_v2
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic pcmproser_pcoevents_serviceagreement_v2_out
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic pcmproser_pcoevents_serviceagreement_v2_dlt
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic pcmproser_pcoevents_serviceagreement_v2_dlt_avro
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic pcmproser_pcoevents_serviceagreement_v2_dlt_avro_out

kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic transaction_v1
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic transaction_v2
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic transaction_v2_dlt

### Listening on the topic:
kafka-console-consumer --topic pcmproser_pcoevents_serviceagreement_v2_dlt --bootstrap-server localhost:9092 --from-beginning --property print.key=true --property key.separator=":"
kafka-console-consumer --topic pcmproser_pcoevents_serviceagreement_v2_dlt_avro --bootstrap-server localhost:9092 --from-beginning --property print.key=true --property key.separator=":"
kafka-console-consumer --topic pcmproser_pcoevents_serviceagreement_v2_dlt_avro_out --bootstrap-server localhost:9092 --from-beginning --property print.key=true --property key.separator=":"

kafka-console-producer --topic FOO_02 --broker-list localhost:9092

confluent local services stop

## Connector
confluent local services connect connector list
confluent local services connect log

## KSQL: https://www.youtube.com/watch?v=ABOJGB5G35k
CREATE STREAM FOO_01 (COL1 INT, COL2 INT) WITH (KAFKA_TOPIC='FOO_01', VALUE_FORMAT='AVRO', PARTITIONS='1');
insert into foo_01(col1, col2) values(1, 5);
print FOO_01 from beginning limit 1;

```
CREATE SINK CONNECTOR SINK_FOO_01 WITH (
  'connector.class' = 'io.confluent.connect.jdbc.JdbcSinkConnector',
  'connection.url' = 'jdbc:postgresql://localhost:5432/',
  'connection.user' = 'postgres',
  'connection.password' = 'postgres',
  'topics' = 'FOO_01',
  'key.converter' = 'io.confluent.connect.avro.AvroConverter',
  'key.converter.schema.registry.url' = 'http://localhost:8081',
  'value.converter' = 'io.confluent.connect.avro.AvroConverter',
  'value.converter.schema.registry.url' = 'http://localhost:8081',
  'auto.create' = 'true'
);
```

CREATE STREAM FOO_02 (COL1 INT, COL2 INT) WITH (KAFKA_TOPIC='FOO_02', VALUE_FORMAT='AVRO', PARTITIONS='1');
insert into foo_02(col1, col2) values(1, 5);
```
  CREATE SINK CONNECTOR SINK_FOO_02 WITH (
    'connector.class' = 'io.confluent.connect.jdbc.JdbcSinkConnector',
    'connection.url' = 'jdbc:postgresql://localhost:5432/',
    'connection.user' = 'postgres',
    'connection.password' = 'postgres',
    'topics' = 'FOO_02',
    'key.converter' = 'io.confluent.connect.avro.AvroConverter',
    'key.converter.schema.registry.url' = 'http://localhost:8081',
    'value.converter' = 'io.confluent.connect.avro.AvroConverter',
    'value.converter.schema.registry.url' = 'http://localhost:8081',
    'auto.create' = 'true',
    'pk.mode' = 'record_value',
    'pk.fields' = 'COL1'
  );
```
describe connector SINK_FOO_02;
print FOO_02 from beginning limit 3;

3.,
```
  CREATE SINK CONNECTOR SINK_FOO_02_1 WITH (
    'connector.class' = 'io.confluent.connect.jdbc.JdbcSinkConnector',
    'connection.url' = 'jdbc:postgresql://localhost:5432/',
    'connection.user' = 'postgres',
    'connection.password' = 'postgres',
    'topics' = 'FOO_02',
    'key.converter' = 'io.confluent.connect.avro.AvroConverter',
    'key.converter.schema.registry.url' = 'http://localhost:8081',
    'value.converter' = 'io.confluent.connect.avro.AvroConverter',
    'value.converter.schema.registry.url' = 'http://localhost:8081',
    'auto.create' = 'true',
    'pk.mode' = 'record_value',
    'pk.fields' = 'COL1',
    'insert.mode' = 'upsert'
  );
```
insert into foo_02(col1, col2) values(1, 5);
insert into foo_02(col1, col2) values(1, 45);
insert into foo_02(col1, col2) values(1, 29);

TODO: Try the kafka-cat




CREATE STREAM SERVICE_AGREEMENT_V2 (BUS_REL_UID string, PARTNERT_UID string, SERVICE_AGREEMENT_UID string, SERVICE_AGREEMENT_STATUS string) WITH (KAFKA_TOPIC='pcmproser_pcoevents_serviceagreement_v2', VALUE_FORMAT='AVRO', PARTITIONS='1');
CREATE STREAM MODIFICATION_STREAM (MODIFIED_BY string) WITH (KAFKA_TOPIC='pcmproser_pcoevents_serviceagreement_v2', VALUE_FORMAT='AVRO', PARTITIONS='1');


```
  CREATE SINK CONNECTOR jdbc_sa_jsonb WITH (
    'connector.class' = 'io.confluent.connect.jdbc.JdbcSinkConnector',
    'connection.url' = 'jdbc:postgresql://localhost:5432/',
    'connection.user' = 'postgres',
    'connection.password' = 'postgres',
    'topics' = 'pcmproser_pcoevents_serviceagreement_v2',
    'table.name.format' = 'sa_dlt',
    'key.converter' = 'io.confluent.connect.avro.AvroConverter',
    'key.converter.schema.registry.url' = 'http://localhost:8081',
    'value.converter' = 'io.confluent.connect.avro.AvroConverter',
    'value.converter.schema.registry.url' = 'http://localhost:8081',
    'auto.create' = 'true',
    'insert.mode' = 'upsert'
  );

## Transaction V1
  CREATE SINK CONNECTOR jdbc_transaction_jsonb WITH (
    'connector.class' = 'io.confluent.connect.jdbc.JdbcSinkConnector',
    'connection.url' = 'jdbc:postgresql://localhost:5432/',
    'connection.user' = 'postgres',
    'connection.password' = 'postgres',
    'topics' = 'transaction_v1',
    'table.name.format' = 'transactions',
    'key.converter' = 'org.apache.kafka.connect.storage.StringConverter',
    'key.converter.schema.registry.url' = 'http://localhost:8081',
    'value.converter' = 'io.confluent.connect.avro.AvroConverter',
    'value.converter.schema.registry.url' = 'http://localhost:8081',
    'pk.fields' = 'event_key',
    'auto.create' = 'true'
  );

# This is the alternative of:
## kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic transaction_v2

CREATE STREAM transactions_v2 (TRANSACTION_ID VARCHAR, name VARCHAR, amount INT) WITH (KAFKA_TOPIC='transaction_v2', VALUE_FORMAT='AVRO', PARTITIONS='1');
CREATE STREAM transactions_v2 (transactionId VARCHAR, name VARCHAR, amount INT) WITH (KAFKA_TOPIC='transaction_v2', VALUE_FORMAT='AVRO', PARTITIONS='1');
insert into transactions_v2(TRANSACTIONID, name, amount) values('414b92c1-bdfd-4c5e-862b-ae3a022c26de', 'Gabor Fekete', 5);
## Transaction V2
  CREATE SINK CONNECTOR jdbc_transactionv2_jsonb WITH (
    'connector.class' = 'io.confluent.connect.jdbc.JdbcSinkConnector',
    'connection.url' = 'jdbc:postgresql://localhost:5432/',
    'connection.user' = 'postgres',
    'connection.password' = 'postgres',
    'topics' = 'transaction_v2',
    'table.name.format' = 'transactions_v2',
    'key.converter' = 'org.apache.kafka.connect.storage.StringConverter',
    'key.converter.schema.registry.url' = 'http://localhost:8081',
    'value.converter' = 'io.confluent.connect.avro.AvroConverter',
    'value.converter.schema.registry.url' = 'http://localhost:8081',
    'transforms' = 'Flatten,RenameFields',
    'transforms.Flatten.type' = 'org.apache.kafka.connect.transforms.Flatten$Value',
    'transforms.Flatten.delimiter' = '_',
    'transforms.RenameFields.type' = 'org.apache.kafka.connect.transforms.ReplaceField$Value',
    'transforms.RenameFields.renames' = 'transactionId:TRANSACTION_ID',
    'pk.mode' = 'record_key',
    'pk.fields' = 'EVENT_KEY',
    'auto.create' = 'true'
  );

drop connector jdbc_transactionv2_jsonb;


```



,'insert.mode' = 'upsert'

io.confluent.kafka.streams.serdes.avro.SpecificAvroDeserializer

{
  "name": "jdbc_sa_jsonb",
  "config": {
          "connector.class": "io.confluent.connect.jdbc.JdbcSourceConnector",
          'connection.url' = 'jdbc:postgresql://localhost:5432/',
          'connection.user' = 'postgres',
          'connection.password' = 'postgres',
          "tasks.max": "1",
          'topics' = 'pcmproser_pcoevents_serviceagreement_v2',
          "mode":"incrementing",
          "incrementing.column.name":"dlt_id",
          "poll.interval.ms":"1000",
          "key.converter": "io.confluent.connect.avro.AvroConverter",
          "key.converter.schema.registry.url": "http://localhost:8081",
          "value.converter": "io.confluent.connect.avro.AvroConverter",
          "value.converter.schema.registry.url": "http://localhost:8081"
      }
}


{
    "name": "file_source_connector",
    "config": {
        "connector.class": "org.apache.kafka.connect.file.FileStreamSourceConnector",
        "topic": "connect_topic",
        "file": "/Users/feketegabor/input.txt",
        "value.converter": "org.apache.kafka.connect.storage.StringConverter"
    }
}