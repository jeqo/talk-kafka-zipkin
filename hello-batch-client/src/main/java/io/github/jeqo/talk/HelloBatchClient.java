package io.github.jeqo.talk;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.http.HttpTracing;
import brave.kafka.clients.KafkaTracing;
import brave.sampler.Sampler;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.client.HttpClientBuilder;
import com.linecorp.armeria.client.brave.BraveClient;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.urlconnection.URLConnectionSender;

import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

public class HelloBatchClient {

  public static void main(String[] args) {

    final Config config = ConfigFactory.load();

    /* START TRACING INSTRUMENTATION */
    final var sender = URLConnectionSender.newBuilder()
        .endpoint(config.getString("zipkin.http.url")).build();
    final var reporter = AsyncReporter.builder(sender).build();
    final var tracing = Tracing.newBuilder().localServiceName("hello-batch-client")
        .sampler(Sampler.ALWAYS_SAMPLE).spanReporter(reporter).build();
    final var httpTracing = HttpTracing.newBuilder(tracing).build();
    final var kafkaTracing = KafkaTracing.newBuilder(tracing)
        .remoteServiceName("kafka").build();
    final var tracer = Tracing.currentTracer();
    /* END TRACING INSTRUMENTATION */

    final HttpClient httpClient = new HttpClientBuilder(config.getString("hello-service.url"))
        .decorator(BraveClient.newDecorator(httpTracing.clientOf("hello-service")))
        .build();

    final var producerConfigs = new Properties();
    producerConfigs.setProperty(BOOTSTRAP_SERVERS_CONFIG,
        config.getString("kafka.bootstrap-servers"));
    producerConfigs.setProperty(KEY_SERIALIZER_CLASS_CONFIG,
        StringSerializer.class.getName());
    producerConfigs.setProperty(VALUE_SERIALIZER_CLASS_CONFIG,
        StringSerializer.class.getName());
    final var kafkaProducer = new KafkaProducer<String, String>(producerConfigs);
    final var tracedKafkaProducer = kafkaTracing.producer(kafkaProducer);

    /* START OPERATION */
    final var batchSpan = tracer.newTrace().name("call-hello-batch").start();
    try (Tracer.SpanInScope ignored = tracer.withSpanInScope(batchSpan)) {
      final var names = Arrays.asList("Jorge", "Eliana", "Jon", "Julio", "Walter");
      batchSpan.annotate("batch started");

      for (String name : names) {
        Span span = tracer.nextSpan().name("call-hello").start();
        try(Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
          span.tag("name", name);
          span.annotate("starting operation");

          var response = httpClient.get("/hello/" + name).aggregate().join();

          var hello = response.content(Charset.defaultCharset());

          span.annotate("sending message to kafka");
          tracedKafkaProducer.send(new ProducerRecord<>("hello", hello), (metadata, exception) -> {
            if (exception != null) span.error(exception);
          });
        } catch (Exception e) {
          span.error(e);
          e.printStackTrace();
        } finally {
          span.finish();
        }
      }
    } catch (Exception e) {
      batchSpan.error(e);
      e.printStackTrace();
    } finally {
      batchSpan.annotate("batch completed");
      batchSpan.finish();
    }
    /* END OPERATION */
    reporter.flush();
    reporter.close();
    tracing.close();
    tracedKafkaProducer.flush();
    tracedKafkaProducer.close();
  }
}
