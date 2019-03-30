package io.github.jeqo.talk;

import brave.ScopedSpan;
import brave.Tracing;
import brave.http.HttpTracing;
import brave.httpclient.TracingHttpClientBuilder;
import brave.kafka.clients.KafkaTracing;
import brave.sampler.Sampler;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.urlconnection.URLConnectionSender;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class HelloClient {

	public static void main(String[] args) throws InterruptedException, IOException {

		final Config config = ConfigFactory.load();

		/* START TRACING INSTRUMENTATION */
		final var sender = URLConnectionSender.newBuilder()
				.endpoint(config.getString("zipkin.endpoint")).build();
		final var reporter = AsyncReporter.builder(sender).build();
		final var tracing = Tracing.newBuilder().localServiceName("hello-client")
				.sampler(Sampler.ALWAYS_SAMPLE).spanReporter(reporter).build();
		final var httpTracing = HttpTracing.newBuilder(tracing).build();
		final var kafkaTracing = KafkaTracing.newBuilder(tracing)
				.remoteServiceName("kafka").build();
		final var tracer = Tracing.currentTracer();
		/* END TRACING INSTRUMENTATION */

		final HttpClient httpClient = TracingHttpClientBuilder.create(httpTracing)
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
		final String baseUrl = config.getString("hello-service.base-url");

		/* START OPERATION */
		final var batchSpan = tracer.startScopedSpan("call-hello-batch");
		final var names = Arrays.asList("Jorge", "Eliana", "Jon", "Robin", "Jun", "Neha");
		batchSpan.annotate("batch started");
		for (String name : names) {
			ScopedSpan span = tracer.startScopedSpan("call-hello");
			span.tag("name", name);
			span.annotate("starting operation");
			var response = httpClient.execute(new HttpGet(baseUrl + "/hello/" + name));

			var hello = EntityUtils.toString(response.getEntity());

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
