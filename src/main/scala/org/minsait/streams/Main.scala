package org.minsait.streams

import java.util.Properties
import java.util.concurrent.TimeUnit

import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.slf4j.LoggerFactory

object Main extends App {
  private val logger = LoggerFactory.getLogger("Main")

  val appName = ConfigFactory.load.getString("kafka.application.id")
  val sourceTopic = ConfigFactory.load.getString("kafka.topic.source")
  val sinkTopic = ConfigFactory.load.getString("kafka.topic.sink")
  val bootstrapServers = ConfigFactory.load.getString("kafka.bootstrap.servers")

  val config: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, appName)
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    p.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass)
    p.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass)
    p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    p
  }

  val mainTopology = new MainTopology()

  val streams: KafkaStreams = new KafkaStreams(mainTopology.createTopology(sourceTopic, sinkTopic), config)
  logger.info(s"[$appName] [KAFKA STREAMS] Starting streaming")
  streams.start()

  sys.ShutdownHookThread {
    streams.close(10, TimeUnit.SECONDS)
  }

}
