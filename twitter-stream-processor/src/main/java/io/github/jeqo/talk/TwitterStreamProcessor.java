package io.github.jeqo.talk;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;

import java.util.Properties;

public class TwitterStreamProcessor {

  public static void main(String[] args) {
    final StreamsBuilder builder = new StreamsBuilder();

    final Topology topology = builder.build();

    final Properties config = new Properties();
    KafkaStreams kafkaStreams = new KafkaStreams(topology, config);
    kafkaStreams.start();

    Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
  }
}
