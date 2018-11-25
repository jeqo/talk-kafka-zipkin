package io.github.jeqo.talk;

import brave.Tracing;
import brave.http.HttpTracing;
import brave.httpclient.TracingHttpClientBuilder;
import brave.jersey.server.TracingApplicationEventListener;
import brave.sampler.Sampler;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.http.client.HttpClient;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.kafka11.KafkaSender;

public class HelloService extends Application<HelloServiceConfiguration> {

	@Override
	public void initialize(Bootstrap<HelloServiceConfiguration> bootstrap) {
		// Enable variable substitution with environment variables
		bootstrap.setConfigurationSourceProvider(
				new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
						new EnvironmentVariableSubstitutor(false)));
	}

	@Override
	public void run(HelloServiceConfiguration configuration, Environment environment) {
		/* START TRACING INSTRUMENTATION */
		final KafkaSender sender = KafkaSender.newBuilder()
				.bootstrapServers(configuration.getKafkaBootstrapServers()).build();
		final AsyncReporter<Span> reporter = AsyncReporter.builder(sender).build();
		final Tracing tracing = Tracing.newBuilder().localServiceName("hello-service")
				.sampler(Sampler.ALWAYS_SAMPLE).spanReporter(reporter).build();
		final HttpTracing httpTracing = HttpTracing.newBuilder(tracing).build();
		final ApplicationEventListener jerseyTracingFilter = TracingApplicationEventListener
				.create(httpTracing);
		environment.jersey().register(jerseyTracingFilter);
		/* END TRACING INSTRUMENTATION */

		// Without instrumentation
		// final HttpClient httpClient =
		// new
		// HttpClientBuilder(environment).using(configuration.getHttpClientConfiguration())
		// .build(getName());
		final HttpClient httpClient = TracingHttpClientBuilder.create(httpTracing)
				.build();
		final String url = configuration.getTranslationServiceUrl() + "/translate";
		final HelloTranslationServiceClient translationServiceClient = new HelloTranslationServiceClient(
				httpClient, url);

		final HelloResource helloResource = new HelloResource(translationServiceClient);
		environment.jersey().register(helloResource);

		final HelloServiceHealthCheck helloServiceHealthCheck = new HelloServiceHealthCheck();
		environment.healthChecks().register("hello-service", helloServiceHealthCheck);
	}

	public static void main(String[] args) throws Exception {
		final HelloService app = new HelloService();
		app.run(args);
	}

}
