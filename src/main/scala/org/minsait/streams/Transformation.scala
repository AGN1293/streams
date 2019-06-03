package org.minsait.streams

import io.circe.Printer
import org.minsait.streams.model.{JsonMessage, JsonResponse, PayloadFields, ResponseMessage, Schema}
import io.circe.parser.decode
import org.slf4j.LoggerFactory
import io.circe.generic.auto._
import io.circe.generic.extras.Configuration
import io.circe.syntax._

import scala.collection.mutable.ArrayBuffer

object Transformation {

  val logger = LoggerFactory.getLogger(getClass)

  implicit val configuration: Configuration = Configuration.default.withDefaults

  val printer: Printer = Printer.noSpaces.copy(dropNullValues = true)

  def jsonToClass(input: String): Option[JsonMessage] = {
    val inputFormatted = input.replace("\\\\\\\"", "").replace("\"[", "[").replace("]\"", "]").replace("\\", "")
    val decoded = decode[JsonMessage](inputFormatted)
    decoded match {
      case Right(msg) => Some(msg)
      case Left(left) => {
        logger.debug(s"[OSUSR_DGL_DFORM_I1] Error: $left")
        logger.debug(s"[OSUSR_DGL_DFORM_I1] Couldn't parse the message: $input")
        None
      }
    }
  }

  val fieldList = List(
    PayloadFields("int64", "ID"),
    PayloadFields("int64", "TENANT_ID"),
    PayloadFields("string", "ITEM"),
    PayloadFields("int64", "TIPO"),
    PayloadFields("string", "ETIQUETA"),
    PayloadFields("string", "VALOR"),
    PayloadFields("string", "GG_T_TIMESTAMP"))

  val dummyJson = JsonResponse(Schema(fields = fieldList))

  def formatEvents(json: Option[JsonMessage]): String = {
    var results: ArrayBuffer[JsonResponse] = ArrayBuffer.empty
    json match {
      case Some(msg) => {
        logger.debug(s"[OSUSR_DGL_DFORM_I1] Parsing message with id: {${json.get.after.ID}}")
        val id = msg.after.ID
        val tenantId = msg.after.TENANT_ID
        var xIndex = -1
        msg.after.FORMINSTANCEFIELDS.get.foreach {
          x =>
            xIndex += 1
            var yIndex = -1
            x.FieldsList.foreach {
              y =>
                yIndex += 1
                y.DFormFieldTypeId match {
                  case 3 =>
                    if (y.IsFilled.getOrElse(false)) {
                      results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-0", 3, y.Label, y.TextBoxField.get.Value.get, msg.current_ts)))
                    }
                  case 4 =>
                    if (y.DatetimeFieldId.isDefined && y.DatetimeFieldId.get.Value.isDefined) {
                      results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-0", 4, y.Label, y.DatetimeFieldId.get.Value.get, msg.current_ts)))
                    }
                  case 5 =>
                    if (y.IsFilled.getOrElse(false) && y.LogicFieldId.isDefined) {
                      if (y.LogicFieldId.get.Value.getOrElse(false))
                        results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-0", 5, y.Label, y.LogicFieldId.get.LabelTrue.getOrElse(""), msg.current_ts)))
                      else
                        results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-0", 5, y.Label, y.LogicFieldId.get.LabelFalse.getOrElse(""), msg.current_ts)))
                    }
                  case 6 =>
                    if (y.OptionListFieldId.isDefined && y.OptionListFieldId.get.OptionChoicesList.isDefined) {
                      val optionList = y.OptionListFieldId.get.OptionChoicesList.get
                      var zIndex = -1
                      optionList.foreach {
                        option =>
                          zIndex += 1
                          if (option.IsSelected.isDefined && option.IsSelected.get && option.Name.isDefined)
                            results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-" + zIndex, 6, y.Label, option.Name.get, msg.current_ts)))
                      }
                    }
                  case 7 =>
                    if (y.NumericFieldId.isDefined && y.NumericFieldId.get.Value.isDefined)
                      results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-0", 7, y.Label, y.NumericFieldId.get.Value.get.toString, msg.current_ts)))
                  case 8 =>
                    if (y.AttachmentFieldId.isDefined && y.AttachmentFieldId.get.AttachmentLines.isDefined && y.AttachmentFieldId.get.AttachmentLines.get.AttachmentLineFieldList.isDefined && y.AttachmentFieldId.get.AttachmentLines.get.AttachmentLineFieldList.get.nonEmpty) {
                      val value = y.AttachmentFieldId.get.AttachmentLines.get.AttachmentLineFieldList.get.head.Value.getOrElse("")
                      val fileName = y.AttachmentFieldId.get.AttachmentLines.get.AttachmentLineFieldList.get.head.FileName.get
                      results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-0", 8, y.Label, value + ":" + fileName, msg.current_ts)))
                    }
                  case 9 =>
                    if (y.OptionListFieldId.isDefined && y.OptionListFieldId.get.OptionChoicesList.isDefined) {
                      var zIndex = -1
                      y.OptionListFieldId.get.OptionChoicesList.get.foreach {
                        option =>
                          zIndex += 1
                          if (option.IsSelected.getOrElse(false))
                            results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-" + zIndex, 9, y.Label, option.Name.get, msg.current_ts)))
                      }
                    }
                  case _ =>
                }
            }
        }
        results.toList.map(event => event.asJson.pretty(printer))
      }
    }
  }

}
