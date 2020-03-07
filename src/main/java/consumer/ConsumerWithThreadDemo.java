package consumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.KafkaConstants;
import kafka.KafkaTopics;

public class ConsumerWithThreadDemo {

	public static void main(String[] args) {
		new ConsumerWithThreadDemo().run();
	}

	private void run() {
		final Logger logger = LoggerFactory.getLogger(ConsumerWithThreadDemo.class.getName());
		
		CountDownLatch latch = new CountDownLatch(1);
		String groupId = "my-kafka-application";
		
		logger.info("Creating the consumer thread");
		Runnable myConsumerRunnable = new ConsumerRunnable(KafkaConstants.bootStrapServer, groupId, KafkaTopics.firstTopic, latch);
		Thread myThread = new Thread(myConsumerRunnable);
		myThread.start();
		
		// add a shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			logger.info("Caught shutdown hook");
			((ConsumerRunnable)myConsumerRunnable).shutdown();
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			logger.info("Application exited");
		}));
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			logger.error("Application got interrupted", e);
		}
	}

	public class ConsumerRunnable implements Runnable {

		private CountDownLatch latch;
		private KafkaConsumer<String, String> consumer;
		final Logger logger = LoggerFactory.getLogger(ConsumerRunnable.class.getName());

		public ConsumerRunnable(String bootStrapServers, String groupId, String topic, CountDownLatch latch) {
			this.latch = latch;
			// create consumer config
			Properties properties = new Properties();
			properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.bootStrapServer);
			properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
			properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

			// create consumer
			consumer = new KafkaConsumer<String, String>(properties);

			// Subscribe consumer to our topic(s)
			consumer.subscribe(Arrays.asList(KafkaTopics.firstTopic));

		}

		public void run() {
			// Poll for new data
			try {
				while (true) {
					ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
					for (ConsumerRecord<String, String> record : records) {
						logger.info("Key: " + record.key() + ", Value:" + record.value());
						logger.info("Partition: " + record.partition() + ", Offset:" + record.offset());
					}
				}
			} catch (WakeupException we) {
				logger.info("Received shutdown signal");
			} finally {
				logger.info("Consumer is closing");
				consumer.close();
				// tell our main code we are done with consumer
				latch.countDown();
			}

		}

		public void shutdown() {
			// interrupt consumer.poll()
			// It will throw exception WakeupException
			consumer.wakeup();
		}
	}
}
