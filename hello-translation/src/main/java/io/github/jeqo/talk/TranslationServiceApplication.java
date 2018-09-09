package io.github.jeqo.talk;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;

public class TranslationServiceApplication extends Application<Configuration> {

  @Override
  public void run(Configuration configuration, Environment environment) {
    final TranslationResource translationResource = new TranslationResource();
    environment.jersey().register(translationResource);

    final TranslationServiceHealthCheck healthCheck = new TranslationServiceHealthCheck();
    environment.healthChecks().register("translation-service", healthCheck);
  }

  public static void main(String[] args) throws Exception {
    final TranslationServiceApplication app = new TranslationServiceApplication();
    app.run(args);
  }
}
