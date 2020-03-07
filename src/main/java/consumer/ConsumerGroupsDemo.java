package consumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.KafkaConstants;
import kafka.KafkaTopics;

public class ConsumerGroupsDemo {

	public static void main(String[] args) {
		
		Logger logger = LoggerFactory.getLogger(ConsumerGroupsDemo.class);
		
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
		
		// Subscribe consumer to our topic(s)
		consumer.subscribe(Arrays.asList(KafkaTopics.firstTopic));
		
		// Poll for new data
		while(true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
			for(ConsumerRecord<String, String> record: records) {
				logger.info("Key: "+ record.key() + ", Value:"+ record.value());
				logger.info("Partition: "+ record.partition() + ", Offset:"+ record.offset());
			}
		}
	}
}
