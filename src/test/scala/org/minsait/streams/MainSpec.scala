package org.minsait.streams

import cats.effect.IO
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.test.ConsumerRecordFactory
import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

class MainSpec extends FlatSpec with Matchers with TestSpec {

  it should "transform the JSON properly" in {
    val mainTopology = new MainTopology()
    implicit val driver = new TopologyTestDriver(mainTopology.createTopology("input-topic", "output-topic"), config)
    val recordFactory = new ConsumerRecordFactory("input-topic", new StringSerializer(), new StringSerializer())
    val words = IO {
      Source.fromInputStream(getClass.getClassLoader.getResourceAsStream("input.json")).getLines().mkString
    }.unsafeRunSync()
    val expected = IO {
      Source.fromInputStream(getClass.getClassLoader.getResourceAsStream("response.json")).getLines().mkString
    }.unsafeRunSync()
    publishToKafka("input-topic", words)
    consumeMessage("output-topic") shouldBe expected
  }

  def publishToKafka(topic: String, value: String)(implicit testDriver: TopologyTestDriver) = {
    val consumerRecordFactory: ConsumerRecordFactory[String, String] = new ConsumerRecordFactory[String, String](topic, new StringSerializer(), new StringSerializer())
    testDriver.pipeInput(consumerRecordFactory.create(value))
  }

  def consumeMessage(topic: String)(implicit testDriver: TopologyTestDriver) = {
    val transformedRecord = testDriver.readOutput(topic, new StringDeserializer(), new StringDeserializer())
    transformedRecord.value()
  }
}