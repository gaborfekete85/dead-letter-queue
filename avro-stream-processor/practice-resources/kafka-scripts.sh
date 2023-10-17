

netsh interface portproxy add v4tov4 listenport=9092 listenaddress=0.0.0.0 connectport=9092 connectaddress=<your-ip>

netsh interface portproxy add v4tov4 listenport=8081 listenaddress=0.0.0.0 connectport=8081 connectaddress=<your-ip>

confluent local services start

kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic loyalty-topic
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic hadoop-sink-topic
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic transactionApp-avro-pos-topic


kafka-console-consumer --topic loyalty-topic --bootstrap-server localhost:9092 --from-beginning --property print.key=true --property key.separator=":"
kafka-console-consumer --topic transactionApp-avro-pos-topic --bootstrap-server localhost:9092 --from-beginning --property print.key=true --property key.separator=":"


http://localhost:9021/

confluent local services stop
confluent local destroy