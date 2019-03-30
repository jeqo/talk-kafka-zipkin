package io.github.jeqo.talk;

import brave.Tracing;
import brave.kafka.streams.KafkaStreamsTracing;
import brave.sampler.Sampler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.ConfigFactory;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.github.jeqo.talk.avro.Tweet;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.urlconnection.URLConnectionSender;

import java.util.Objects;
import java.util.Properties;

public class TwitterStreamProcessor {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static void main(String[] args) {

		final var config = ConfigFactory.load();

		/* START TRACING INSTRUMENTATION */
		final var sender = URLConnectionSender.newBuilder()
				.endpoint(config.getString("zipkin.endpoint")).build();
		final var reporter = AsyncReporter.builder(sender).build();
		final var tracing = Tracing.newBuilder().localServiceName("stream-transform")
				.sampler(Sampler.ALWAYS_SAMPLE).spanReporter(reporter).build();
		final var kafkaStreamsTracing = KafkaStreamsTracing.create(tracing);
		/* END TRACING INSTRUMENTATION */

		final var streamsConfig = new Properties();
		streamsConfig.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
				config.getString("kafka.bootstrap-servers"));
		streamsConfig.setProperty(StreamsConfig.APPLICATION_ID_CONFIG,
				"stream-transform");
		streamsConfig.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
				Serdes.String().getClass().getName());
		streamsConfig.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
				SpecificAvroSerde.class);
		streamsConfig.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
				config.getString("schema-registry.url"));

		final var builder = new StreamsBuilder();
		builder.stream(config.getString("topics.input-tweets-json"),
				Consumed.with(Serdes.String(), Serdes.String()))
				.transform(kafkaStreamsTracing.map("parse_json",
						TwitterStreamProcessor::parseJson))
				.filterNot((k, v) -> Objects.isNull(v))
				.filter(TwitterStreamProcessor::hasHashtag)
				.transformValues(kafkaStreamsTracing.mapValues("json_to_avro",
						TwitterStreamProcessor::parseTweet))
				.to(config.getString("topics.output-tweets-avro"));

		final var topology = builder.build();
		final var kafkaStreams = kafkaStreamsTracing.kafkaStreams(topology,
				streamsConfig);
		kafkaStreams.start();

		Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
	}

	private static KeyValue<String, JsonNode> parseJson(String key, String value) {
		try {
			return KeyValue.pair(key, OBJECT_MAPPER.readTree(value));
		}
		catch (Exception e) {
			e.printStackTrace();
			return KeyValue.pair(key, null);
		}
	}

	private static boolean hasHashtag(String key, JsonNode value) {
		return true;
	}

	private static Tweet parseTweet(JsonNode jsonValue) {
		var tweet = Tweet.newBuilder().setText(jsonValue.get("Text").textValue())
				.setLang(jsonValue.get("Lang").textValue())
				.setUsername(jsonValue.get("User").get("ScreenName").textValue()).build();
		var span = Tracing.currentTracer().currentSpan();
		span.tag("tweet.username", tweet.getUsername().toString());
		// if you want to add traceId to payload:
		// tweetBuilder.setTraceId(span.context().traceIdString());
		return tweet;
	}

}
