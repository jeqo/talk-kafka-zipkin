package io.github.jeqo.talk;

import io.dropwizard.Application;
import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.setup.Environment;
import org.apache.http.client.HttpClient;

public class HelloServiceApplication extends Application<HelloServiceConfiguration> {
  @Override
  public void run(HelloServiceConfiguration configuration, Environment environment) {
    final HttpClient httpClient =
        new HttpClientBuilder(environment).using(configuration.getHttpClientConfiguration())
            .build(getName());
    final String url = configuration.getTranslationServiceUrl();
    final HelloTranslationServiceClient translationServiceClient =
        new HelloTranslationServiceClient(httpClient, url);

    final HelloResource helloResource = new HelloResource(translationServiceClient);
    environment.jersey().register(helloResource);

    final HelloServiceHealthCheck helloServiceHealthCheck = new HelloServiceHealthCheck();
    environment.healthChecks().register("hello-service", helloServiceHealthCheck);
  }

  public static void main(String[] args) throws Exception {
    final HelloServiceApplication app = new HelloServiceApplication();
    app.run(args);
  }
}
