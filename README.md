# Kafka
Kafka producer and consumer

# Useful commands for zookeeper and kafka
list topics
	> kafka-topics.bat --zookeeper 127.0.0.1:2181 --list
create topic
	kafka-topics.bat --zookeeper 127.0.0.1:2181 --create --topic first-topic --partitions 3 --replication-factor 1  
describe topic
	kafka-topics.bat --zookeeper 127.0.0.1:2181 --topic first-topic --describe
	Topic: first-topic      PartitionCount: 3       ReplicationFactor: 1    Configs:
        Topic: first-topic      Partition: 0    Leader: 0       Replicas: 0     Isr: 0
        Topic: first-topic      Partition: 1    Leader: 0       Replicas: 0     Isr: 0
        Topic: first-topic      Partition: 2    Leader: 0       Replicas: 0     Isr: 0
producer
	kafka-console-producer --broker-list 127.0.0.1:9092 --topic first-topic
	>Hello welcome to kafka
	
consumer
	kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic first-topic
	Hello welcome to kafka
consumer from beginning
	kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic first-topic --from-beginning
	
	kafka-console-consumer --bootstrap-server localhost:9092 --topic first-topic --group first-application

consumer group
	
consumer group list
	kafka-consumer-groups --bootstrap-server localhost:9092 --list
consumer group describe
	kafka-consumer-groups --bootstrap-server localhost:9092 --group first-application --describe
consumer offset reset
	kafka-consumer-groups --bootstrap-server localhost:9092 --group first-application --topic fist-topic --reset-offsets --to-earliest --execute
	kafka-consumer-groups --bootstrap-server localhost:9092 --group first-application --topic fist-topic --reset-offsets --shift-by -2 --execute

properties:
	num.partitions=1 --> default number of partitiones when producing the topic
	default.replication.factor=1 --> default number of replication factor when producing the topic
	port=9092 --> leader config port
	advertised.host.name=localhost --> leader configuration
