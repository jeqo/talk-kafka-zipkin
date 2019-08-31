package io.github.jeqo.talk;

import brave.Tracing;
import brave.kafka.clients.KafkaTracing;
import brave.sampler.Sampler;
import com.typesafe.config.ConfigFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.serialization.StringDeserializer;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.urlconnection.URLConnectionSender;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import static java.lang.System.out;

public class HelloConsumer {

	public static void main(String[] args) {

		final var config = ConfigFactory.load();
		/* START TRACING INSTRUMENTATION */
		final var sender = URLConnectionSender.newBuilder()
				.endpoint(config.getString("zipkin.http.url")).build();
		final var reporter = AsyncReporter.builder(sender).build();
		final var tracing = Tracing.newBuilder().localServiceName("hello-consumer")
				.sampler(Sampler.ALWAYS_SAMPLE).spanReporter(reporter).build();
		final var kafkaTracing = KafkaTracing.newBuilder(tracing)
				.remoteServiceName("kafka").build();
		/* END TRACING INSTRUMENTATION */

		final var consumerConfigs = new Properties();
		consumerConfigs.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
				config.getString("kafka.bootstrap-servers"));
		consumerConfigs.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				StringDeserializer.class.getName());
		consumerConfigs.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				StringDeserializer.class.getName());
		consumerConfigs.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "hello-consumer");
		consumerConfigs.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
				OffsetResetStrategy.EARLIEST.name().toLowerCase());
		final var kafkaConsumer = new KafkaConsumer<String, String>(consumerConfigs);
		final var tracingConsumer = kafkaTracing.consumer(kafkaConsumer);

		tracingConsumer.subscribe(Collections.singletonList("hello"));

		while (!Thread.interrupted()) {
			var records = tracingConsumer.poll(Duration.ofMillis(Long.MAX_VALUE));
			for (var record : records) {
				brave.Span span = kafkaTracing.nextSpan(record).name("print-hello")
						.start();
				span.annotate("starting printing");
				out.println(String.format("Record: %s", record));
				span.annotate("printing finished");
				span.finish();
			}
		}
	}

}
