package io.github.jeqo.talk;

import brave.Tracing;
import brave.http.HttpTracing;
import brave.jersey.server.TracingApplicationEventListener;
import brave.sampler.Sampler;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.urlconnection.URLConnectionSender;

public class TranslationService extends Application<TranslationServiceConfiguration> {

	@Override
	public void initialize(Bootstrap<TranslationServiceConfiguration> bootstrap) {
		// Enable variable substitution with environment variables
		bootstrap.setConfigurationSourceProvider(
				new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
						new EnvironmentVariableSubstitutor(false)));
	}

	@Override
	public void run(TranslationServiceConfiguration configuration,
			Environment environment) {

		/* START TRACING INSTRUMENTATION */
		final var sender = URLConnectionSender.newBuilder()
				.endpoint(configuration.getZipkinEndpoint()).build();
		final var reporter = AsyncReporter.builder(sender).build();
		final var tracing = Tracing.newBuilder().localServiceName("translation-service")
				.sampler(Sampler.ALWAYS_SAMPLE).spanReporter(reporter).build();
		final var httpTracing = HttpTracing.newBuilder(tracing).build();
		final var jerseyTracingFilter = TracingApplicationEventListener
				.create(httpTracing);
		environment.jersey().register(jerseyTracingFilter);
		/* END TRACING INSTRUMENTATION */

		final var repository = new TranslationRepository();
		final var translationResource = new TranslationResource(repository);
		environment.jersey().register(translationResource);

		final var healthCheck = new TranslationServiceHealthCheck();
		environment.healthChecks().register("translation-service", healthCheck);
	}

	public static void main(String[] args) throws Exception {
		final var app = new TranslationService();
		app.run(args);
	}

}
