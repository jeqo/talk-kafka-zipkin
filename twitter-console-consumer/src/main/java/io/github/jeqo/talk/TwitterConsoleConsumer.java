package io.github.jeqo.talk;

import brave.Tracing;
import brave.kafka.clients.KafkaTracing;
import brave.sampler.Sampler;
import com.typesafe.config.ConfigFactory;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroDeserializer;
import io.github.jeqo.talk.avro.Tweet;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.urlconnection.URLConnectionSender;

import static java.lang.System.out;

public class TwitterConsoleConsumer {

  public static void main(String[] args) {

    final var config = ConfigFactory.load();
    final var kafkaBootstrapServers = config.getString("kafka.bootstrap-servers");
    final var schemaRegistryUrl = config.getString("schema-registry.url");

    /* START TRACING INSTRUMENTATION */
    final var sender = URLConnectionSender.newBuilder()
        .endpoint(config.getString("zipkin.endpoint")).build();
    final var reporter = AsyncReporter.builder(sender).build();
    final var tracing = Tracing.newBuilder()
        .localServiceName("twitter-console-consumer")
        .sampler(Sampler.ALWAYS_SAMPLE).spanReporter(reporter).build();
    final var kafkaTracing = KafkaTracing.newBuilder(tracing)
        .remoteServiceName("kafka").build();
    /* END TRACING INSTRUMENTATION */

    final var consumerConfigs = new Properties();
    consumerConfigs.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
        kafkaBootstrapServers);
    consumerConfigs.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class.getName());
    consumerConfigs.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        SpecificAvroDeserializer.class.getName());
    consumerConfigs.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
        schemaRegistryUrl);
    consumerConfigs.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "twitter-console");
    final var kafkaConsumer = new KafkaConsumer<String, Tweet>(consumerConfigs);
    final var tracingConsumer = kafkaTracing.consumer(kafkaConsumer);

    tracingConsumer.subscribe(
        Collections.singletonList(config.getString("topics.input-tweets")));

    while (!Thread.interrupted()) {
      final var records = tracingConsumer.poll(Duration.ofMillis(Long.MAX_VALUE));
      for (var record : records) {
        var span = kafkaTracing.nextSpan(record).name("print-hello").start();
        try (var ignored = tracing.tracer().withSpanInScope(span)) {
          span.annotate("starting printing");
          out.println(String.format("Record: %s", record));
          span.annotate("printing finished");
        } catch (RuntimeException | Error e) {
          span.error(e);
          throw e;
        } finally {
          span.finish();
        }
      }
    }
  }
}
