package consumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.KafkaConstants;
import kafka.KafkaTopics;

public class ConsumerAssingSeekDemo {

	public static void main(String[] args) {

		Logger logger = LoggerFactory.getLogger(ConsumerDemo.class);

		String groupdId = "my-kafka-application";

		// create consumer config
		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.bootStrapServer);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupdId);
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		// create consumer
		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

		// assign and seek are ostly used to replay data or fetch a specific message

		// assign
		TopicPartition partitionToReadFrom = new TopicPartition(KafkaTopics.firstTopic, 0);
		consumer.assign(Arrays.asList(partitionToReadFrom));

		long offsetToReadFrom = 5L;
		// seek
		consumer.seek(partitionToReadFrom, offsetToReadFrom);

		int numberOfMessagesToRead = 5;
		boolean keepOnReading = true;
		int numberOfMessagesReadSoFar = 0;

		// Poll for new data
		while (keepOnReading) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
			for (ConsumerRecord<String, String> record : records) {
				numberOfMessagesReadSoFar++;
				logger.info("Key: " + record.key() + ", Value:" + record.value());
				logger.info("Partition: " + record.partition() + ", Offset:" + record.offset());
				if (numberOfMessagesReadSoFar >= numberOfMessagesToRead) {
					keepOnReading = false;
					break;
				}
			}
		}
	}
}
