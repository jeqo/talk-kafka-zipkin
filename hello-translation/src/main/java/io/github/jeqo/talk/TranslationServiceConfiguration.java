package io.github.jeqo.talk;

import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class TranslationServiceConfiguration extends Configuration {

	@Valid
	@NotNull
	private String kafkaBootstrapServers;

	@Valid
	@NotNull
	private String zipkinEndpoint;

	public String getKafkaBootstrapServers() {
		return kafkaBootstrapServers;
	}

	public void setKafkaBootstrapServers(String kafkaBootstrapServers) {
		this.kafkaBootstrapServers = kafkaBootstrapServers;
	}

	public String getZipkinEndpoint() {
		return zipkinEndpoint;
	}

	public void setZipkinEndpoint(String zipkinEndpoint) {
		this.zipkinEndpoint = zipkinEndpoint;
	}

}
