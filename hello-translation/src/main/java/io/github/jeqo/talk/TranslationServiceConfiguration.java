package io.github.jeqo.talk;

import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class TranslationServiceConfiguration extends Configuration {

	@Valid
	@NotNull
	private String kafkaBootstrapServers;

	public String getKafkaBootstrapServers() {
		return kafkaBootstrapServers;
	}

	public void setKafkaBootstrapServers(String kafkaBootstrapServers) {
		this.kafkaBootstrapServers = kafkaBootstrapServers;
	}

}
