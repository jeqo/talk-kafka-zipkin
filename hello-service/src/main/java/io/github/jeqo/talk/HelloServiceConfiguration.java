package io.github.jeqo.talk;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.HttpClientConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class HelloServiceConfiguration extends Configuration {
  @Valid
  @NotNull
  private String translationServiceUrl;

  @Valid
  @NotNull
  private HttpClientConfiguration httpClient = new HttpClientConfiguration();

  @JsonProperty("httpClient")
  public HttpClientConfiguration getHttpClientConfiguration() {
    return httpClient;
  }

  @JsonProperty("httpClient")
  public void setHttpClientConfiguration(HttpClientConfiguration httpClient) {
    this.httpClient = httpClient;
  }

  @NotNull
  @JsonProperty("translationServiceUrl")
  public String getTranslationServiceUrl() {
    return translationServiceUrl;
  }

  @JsonProperty("translationServiceUrl")
  public void setTranslationServiceUrl(@NotNull String translationServiceUrl) {
    this.translationServiceUrl = translationServiceUrl;
  }
}
