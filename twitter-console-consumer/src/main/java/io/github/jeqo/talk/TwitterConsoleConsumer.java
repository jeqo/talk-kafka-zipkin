package io.github.jeqo.talk;

import brave.Tracing;
import brave.kafka.clients.KafkaTracing;
import brave.sampler.Sampler;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroDeserializer;
import io.github.jeqo.talk.avro.Tweet;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.kafka11.KafkaSender;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import static java.lang.System.out;

public class TwitterConsoleConsumer {

	public static void main(String[] args) {

		final Config config = ConfigFactory.load();
		final String kafkaBootstrapServers = config.getString("kafka.bootstrap-servers");
		final String schemaRegistryUrl = config.getString("schema-registry.url");

		/* START TRACING INSTRUMENTATION */
		final KafkaSender sender = KafkaSender.newBuilder()
				.bootstrapServers(kafkaBootstrapServers).build();
		final AsyncReporter<Span> reporter = AsyncReporter.builder(sender).build();
		final Tracing tracing = Tracing.newBuilder()
				.localServiceName("twitter-console-consumer")
				.sampler(Sampler.ALWAYS_SAMPLE).spanReporter(reporter).build();
		final KafkaTracing kafkaTracing = KafkaTracing.newBuilder(tracing)
				.remoteServiceName("kafka").build();
		/* END TRACING INSTRUMENTATION */

		final Properties consumerConfigs = new Properties();
		consumerConfigs.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
				kafkaBootstrapServers);
		consumerConfigs.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				StringDeserializer.class.getName());
		consumerConfigs.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				SpecificAvroDeserializer.class.getName());
		consumerConfigs.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
				schemaRegistryUrl);
		consumerConfigs.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "twitter-console");
		final Consumer<String, Tweet> kafkaConsumer = new KafkaConsumer<>(
				consumerConfigs);
		final Consumer<String, Tweet> tracingConsumer = kafkaTracing
				.consumer(kafkaConsumer);

		tracingConsumer.subscribe(
				Collections.singletonList(config.getString("topics.input-tweets")));

		while (!Thread.interrupted()) {
			final ConsumerRecords<String, Tweet> records = tracingConsumer
					.poll(Duration.ofMillis(Long.MAX_VALUE));
			for (ConsumerRecord<String, Tweet> record : records) {
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
