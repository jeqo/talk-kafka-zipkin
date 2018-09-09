package io.github.jeqo.talk;

import com.codahale.metrics.health.HealthCheck;

public class TranslationServiceHealthCheck extends HealthCheck {
  @Override
  protected Result check() {
    return Result.healthy();
  }
}
