package io.github.jeqo.talk;

import brave.Tracing;
import brave.http.HttpTracing;
import brave.jersey.server.TracingApplicationEventListener;
import brave.sampler.Sampler;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.kafka11.KafkaSender;

public class TranslationService extends Application<Configuration> {

  @Override
  public void run(Configuration configuration, Environment environment) {

    /* START TRACING INSTRUMENTATION */
    final KafkaSender sender = KafkaSender.newBuilder().bootstrapServers("localhost:29092").build();
    final AsyncReporter<Span> reporter = AsyncReporter.builder(sender).build();
    final Tracing tracing =
        Tracing.newBuilder()
            .localServiceName("translation-service")
            .sampler(Sampler.ALWAYS_SAMPLE)
            .spanReporter(reporter)
            .build();
    final HttpTracing httpTracing = HttpTracing.newBuilder(tracing).build();
    final ApplicationEventListener jerseyTracingFilter = TracingApplicationEventListener.create(httpTracing);
    environment.jersey().register(jerseyTracingFilter);
    /* END TRACING INSTRUMENTATION */

    final TranslationRepository repository = new TranslationRepository();
    final TranslationResource translationResource = new TranslationResource(repository);
    environment.jersey().register(translationResource);

    final TranslationServiceHealthCheck healthCheck = new TranslationServiceHealthCheck();
    environment.healthChecks().register("translation-service", healthCheck);
  }

  public static void main(String[] args) throws Exception {
    final TranslationService app = new TranslationService();
    app.run(args);
  }
}
