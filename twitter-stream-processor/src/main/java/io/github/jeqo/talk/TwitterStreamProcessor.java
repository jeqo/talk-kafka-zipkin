package io.github.jeqo.talk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.github.jeqo.talk.avro.Tweet;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class TwitterStreamProcessor {

  public static void main(String[] args) {
    final ObjectMapper objectMapper = new ObjectMapper();

    final StreamsBuilder builder = new StreamsBuilder();
    builder.stream("twitter_json_01", Consumed.with(Serdes.String(), Serdes.String()))
    .mapValues(value -> {
      try {
        return objectMapper.readTree(value);
      } catch (IOException e) {
        e.printStackTrace();
        return null;
      }
    })
    .filterNot((k, v) -> Objects.isNull(v))
    .mapValues((k, v) -> parseTweet(v))
    .to("twitter_avro_01");

    final Topology topology = builder.build();

    final Properties config = new Properties();
    config.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
    config.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "stream-transform-v01");
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
    config.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

    KafkaStreams kafkaStreams = new KafkaStreams(topology, config);
    kafkaStreams.start();

    Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
  }

  private static Tweet parseTweet(JsonNode jsonValue) {
    return Tweet.newBuilder()
        .setText(jsonValue.get("Text").textValue())
        .setLang(jsonValue.get("Lang").textValue())
        .setUsername(jsonValue.get("User").get("ScreenName").textValue())
        .build();
  }
}
