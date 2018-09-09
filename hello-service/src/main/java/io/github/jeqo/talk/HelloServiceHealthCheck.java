package io.github.jeqo.talk;

import com.codahale.metrics.health.HealthCheck;

public class HelloServiceHealthCheck extends HealthCheck {
  @Override
  protected Result check() {
    return Result.healthy();
  }
}
