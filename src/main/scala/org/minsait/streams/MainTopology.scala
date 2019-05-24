package org.minsait.streams


import org.apache.kafka.streams.{KeyValue, StreamsBuilder}
import org.minsait.streams.Transformation.{formatEvents, jsonToClass}
import org.minsait.streams.model.JsonMessage

class MainTopology {

  val createTopology = (sourceTopic: String, sinkTopic: String) => {
    val builder = new StreamsBuilder()
    builder.stream(sourceTopic)
      .map[String, Option[JsonMessage]]((key, value) => toJsonEvent(key, value))
      .map[String, String]((key, value) => toFormattedEvents(key, value))
      .to(sinkTopic)
    builder.build()
  }

  private val toJsonEvent = (key: String, value: String) => {
    println(value)
    val jsonEventsAsCaseClasses = jsonToClass(value)
    new KeyValue(key, jsonEventsAsCaseClasses)
  }

  private val toFormattedEvents = (key: String, value: Option[JsonMessage]) => {
    val jsonEvents = formatEvents(value)
    println(jsonEvents)
    new KeyValue(key, jsonEvents)
  }

}
