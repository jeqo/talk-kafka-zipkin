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
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.urlconnection.URLConnectionSender;

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
		final var sender = URLConnectionSender.newBuilder()
				.endpoint(configuration.getZipkinEndpoint()).build();
		final var reporter = AsyncReporter.builder(sender).build();
		final var tracing = Tracing.newBuilder().localServiceName("hello-service")
				.sampler(Sampler.ALWAYS_SAMPLE).spanReporter(reporter).build();
		final var httpTracing = HttpTracing.newBuilder(tracing).build();
		final var jerseyTracingFilter = TracingApplicationEventListener
				.create(httpTracing);
		environment.jersey().register(jerseyTracingFilter);
		/* END TRACING INSTRUMENTATION */

		// Without instrumentation
		// final HttpClient httpClient =
		// new
		// HttpClientBuilder(environment).using(configuration.getHttpClientConfiguration())
		// .build(getName());
		final var httpClient = TracingHttpClientBuilder.create(httpTracing).build();
		final var url = configuration.getTranslationServiceUrl() + "/translate";
		final var translationServiceClient = new HelloTranslationServiceClient(httpClient,
				url);

		final var helloResource = new HelloResource(translationServiceClient);
		environment.jersey().register(helloResource);

		final var helloServiceHealthCheck = new HelloServiceHealthCheck();
		environment.healthChecks().register("hello-service", helloServiceHealthCheck);
	}

	public static void main(String[] args) throws Exception {
		HelloService app = new HelloService();
		app.run(args);
	}

}
