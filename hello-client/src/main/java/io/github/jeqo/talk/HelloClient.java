package io.github.jeqo.talk;

import brave.Tracer;
import brave.Tracing;
import brave.http.HttpTracing;
import brave.httpclient.TracingHttpClientBuilder;
import brave.kafka.clients.KafkaTracing;
import brave.sampler.Sampler;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.kafka11.KafkaSender;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class HelloClient {

	public static void main(String[] args) throws InterruptedException, IOException {

		final Config config = ConfigFactory.load();
		final String kafkaBootstrapServers = config.getString("kafka.bootstrap-servers");

		/* START TRACING INSTRUMENTATION */
		final KafkaSender sender = KafkaSender.newBuilder()
				.bootstrapServers(kafkaBootstrapServers).build();
		final AsyncReporter<Span> reporter = AsyncReporter.builder(sender).build();
		final Tracing tracing = Tracing.newBuilder().localServiceName("hello-client")
				.sampler(Sampler.ALWAYS_SAMPLE).spanReporter(reporter).build();
		final HttpTracing httpTracing = HttpTracing.newBuilder(tracing).build();
		final KafkaTracing kafkaTracing = KafkaTracing.newBuilder(tracing)
				.remoteServiceName("kafka").build();
		final Tracer tracer = Tracing.currentTracer();
		/* END TRACING INSTRUMENTATION */

		final HttpClient httpClient = TracingHttpClientBuilder.create(httpTracing)
				.build();

		final Properties producerConfigs = new Properties();
		producerConfigs.setProperty(BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
		producerConfigs.setProperty(KEY_SERIALIZER_CLASS_CONFIG,
				StringSerializer.class.getName());
		producerConfigs.setProperty(VALUE_SERIALIZER_CLASS_CONFIG,
				StringSerializer.class.getName());
		final Producer<String, String> kafkaProducer = new KafkaProducer<>(
				producerConfigs);
		final Producer<String, String> tracedKafkaProducer = kafkaTracing
				.producer(kafkaProducer);

		final List<String> names = Arrays.asList("Jorge", "Eliana", "Jon", "Robin", "Jun",
				"Neha");

		final String baseUrl = config.getString("hello-service.base-url");
		/* START OPERATION */
		brave.ScopedSpan batchSpan = tracer.startScopedSpan("call-hello-batch");
		batchSpan.annotate("batch started");
		for (String name : names) {
			brave.ScopedSpan span = tracer.startScopedSpan("call-hello");
			span.tag("name", name);
			span.annotate("starting operation");
			final HttpResponse response = httpClient
					.execute(new HttpGet(baseUrl + "/hello/" + name));

			final String hello = EntityUtils.toString(response.getEntity());

			span.annotate("sending message to kafka");
			tracedKafkaProducer.send(new ProducerRecord<>("hello", hello));
			span.annotate("complete operation");
			span.finish();
		}
		batchSpan.annotate("batch completed");
		batchSpan.finish();
		/* END OPERATION */

		Thread.sleep(10_000);
	}

}
