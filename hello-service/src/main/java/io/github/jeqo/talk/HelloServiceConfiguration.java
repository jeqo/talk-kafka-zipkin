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
	private String kafkaBootstrapServers;

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
	@JsonProperty("translationServiceBaseUrl")
	public String getTranslationServiceUrl() {
		return translationServiceUrl;
	}

	@JsonProperty("translationServiceBaseUrl")
	public void setTranslationServiceUrl(@NotNull String translationServiceUrl) {
		this.translationServiceUrl = translationServiceUrl;
	}

	public String getKafkaBootstrapServers() {
		return kafkaBootstrapServers;
	}

	public void setKafkaBootstrapServers(String kafkaBootstrapServers) {
		this.kafkaBootstrapServers = kafkaBootstrapServers;
	}

}
