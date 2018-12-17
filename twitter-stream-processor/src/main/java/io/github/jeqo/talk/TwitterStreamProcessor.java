package io.github.jeqo.talk;

import brave.Tracing;
import brave.kafka.streams.KafkaStreamsTracing;
import brave.sampler.Sampler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.github.jeqo.talk.avro.Tweet;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.Consumed;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.kafka11.KafkaSender;

import java.util.Objects;
import java.util.Properties;

public class TwitterStreamProcessor {

	public static void main(String[] args) {
		final ObjectMapper objectMapper = new ObjectMapper();

		final Config config = ConfigFactory.load();
		final String kafkaBootstrapServers = config.getString("kafka.bootstrap-servers");

		/* START TRACING INSTRUMENTATION */
		final KafkaSender sender = KafkaSender.newBuilder()
				.bootstrapServers(kafkaBootstrapServers).build();
		final AsyncReporter<Span> reporter = AsyncReporter.builder(sender).build();
		final Tracing tracing = Tracing.newBuilder().localServiceName("stream-transform")
				.sampler(Sampler.ALWAYS_SAMPLE).spanReporter(reporter).build();
		final KafkaStreamsTracing kafkaStreamsTracing = KafkaStreamsTracing
				.create(tracing);
		/* END TRACING INSTRUMENTATION */

		final Properties streamsConfig = new Properties();
		streamsConfig.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
				kafkaBootstrapServers);
		streamsConfig.setProperty(StreamsConfig.APPLICATION_ID_CONFIG,
				"stream-transform-v03");
		streamsConfig.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
				Serdes.String().getClass().getName());
		streamsConfig.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
				SpecificAvroSerde.class);
		streamsConfig.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
				config.getString("schema-registry.url"));

		final StreamsBuilder builder = new StreamsBuilder();
		builder.stream(config.getString("topics.input-tweets"),
				Consumed.with(Serdes.String(), Serdes.String()))
				.transform(kafkaStreamsTracing.map("parse_json",
						(String key, String value) -> {
							try {
								return KeyValue.pair(key, objectMapper.readTree(value));
							}
							catch (Exception e) {
								e.printStackTrace();
								return KeyValue.pair(key, null);
							}
						}))
				.filterNot((k, v) -> Objects.isNull(v))
				.transformValues(kafkaStreamsTracing.mapValues("json_to_avro",
						TwitterStreamProcessor::parseTweet))
				.to("twitter_avro_v01");

		final Topology topology = builder.build();
		final KafkaStreams kafkaStreams = kafkaStreamsTracing.kafkaStreams(topology,
				streamsConfig);
		kafkaStreams.start();

		Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
	}

	private static Tweet parseTweet(JsonNode jsonValue) {
		return Tweet.newBuilder().setText(jsonValue.get("Text").textValue())
				.setLang(jsonValue.get("Lang").textValue())
				.setUsername(jsonValue.get("User").get("ScreenName").textValue()).build();
	}

}
