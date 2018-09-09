package io.github.jeqo.talk;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;

public class HelloServiceApplication extends Application<Configuration> {
  @Override
  public void run(Configuration configuration, Environment environment) {
    final HelloResource helloResource = new HelloResource();
    environment.jersey().register(helloResource);

    final HelloServiceHealthCheck helloServiceHealthCheck = new HelloServiceHealthCheck();
    environment.healthChecks().register("hello-service", helloServiceHealthCheck);
  }

  public static void main(String[] args) throws Exception {
    final HelloServiceApplication app = new HelloServiceApplication();
    app.run(args);
  }
}
