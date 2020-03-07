package producer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.KafkaTopics;

public class ProducerKeysDemo {
	public static void main(String[] args) throws InterruptedException, ExecutionException {

		final Logger logger = LoggerFactory.getLogger(ProducerKeysDemo.class);

		// create producer properties
		Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		// create the producer
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

		for (int i = 0; i < 10; i++) {
			// create producer record

			String key = "id_" + Integer.toString(i);
			String value = "Kafka message with keys:" + i;
			ProducerRecord<String, String> record = new ProducerRecord<String, String>(KafkaTopics.firstTopic, key,
					value);
			logger.info("Key:"+key);
			// send data
			producer.send(record, new Callback() {

				public void onCompletion(RecordMetadata metadata, Exception exception) {

					if (exception == null) {
						logger.info("Partition:"+metadata.partition());
					} else {
						logger.error("Error while producing", exception);
					}
				}
			}).get();
		}
		producer.flush();
		producer.close();
	}
}
