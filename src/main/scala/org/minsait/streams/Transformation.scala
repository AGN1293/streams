package org.minsait.streams

import java.text.SimpleDateFormat
import java.util.Date

import io.circe.Printer
import org.minsait.streams.model._
import io.circe.parser.decode
import org.slf4j.LoggerFactory
import io.circe.generic.auto._
import io.circe.generic.extras.Configuration
import io.circe.syntax._

import scala.collection.mutable.ArrayBuffer

object Transformation {

  val logger = LoggerFactory.getLogger(getClass)

  implicit val configuration: Configuration = Configuration.default.withDefaults

  val printer: Printer = Printer.noSpaces

  var results: ArrayBuffer[JsonResponse] = ArrayBuffer.empty

  def jsonToClass(input: String): Option[JsonMessage] = {
    val inputFormatted = input.replace("\\\\\\\"", "").replace("\"[", "[").replace("]\"", "]").replace("\\", "").replaceAll("[^\u0000-\uFFFF]", "")
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
    PayloadFields("int64", "BUSINESS_CONCEPT"),
    PayloadFields("string", "ETIQUETA"),
    PayloadFields("string", "VALOR"),
    PayloadFields("string", "TABLERECORDNAME"),
    PayloadFields("int64", "LINE"),
    PayloadFields("string", "GG_T_TYPE"),
    PayloadFields("string", "GG_T_TIMESTAMP"),
    PayloadFields("string", "TD_T_TIMESTAMP"),
    PayloadFields("string", "POS"))

  val dummyJson = JsonResponse(Schema(fields = fieldList))

  def formatEvents(json: Option[JsonMessage]): List[String] = {
    var results: ArrayBuffer[JsonResponse] = ArrayBuffer.empty
    json match {
      case Some(msg) => {
        logger.debug(s"[OSUSR_DGL_DFORM_I1] Parsing message with id: {${json.get.payload.ID}}")
        val id = msg.payload.ID
        val tenantId = msg.payload.TENANT_ID
        val currentDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS").format(new Date())
        if (msg.payload.FORMINSTANCEFIELDS.isDefined) loopFields(msg.payload.FORMINSTANCEFIELDS.get, id, tenantId, "I", currentDate, currentDate, msg.payload.pos, None).map(event => event.asJson.pretty(printer))
        else List.empty
      }
    }
  }

  def loopFields(fieldList: List[InstanceFields], id: Int, tenantId: Int, opType: String, ggTimestamp: String, tdTimestamp: String, pos: String, tableRecord: Option[String]): List[JsonResponse] = {
    var xIndex = -1
    val tableRecordName = tableRecord match {
      case Some(x) => x
      case _ => "null"
    }
    fieldList.foreach {
      x =>
        xIndex += 1
        var yIndex = -1
        x.FieldsList.foreach {
          y =>
            yIndex += 1
            val businessConcept = y.BusinessConceptId.getOrElse(0)
            y.DFormFieldTypeId match {
              case 1 =>
                if (y.TableRecordLines.isDefined) {
                  var yIndex2 = -1
                  y.TableRecordLines.get.foreach {
                    tableRecordLine =>
                      yIndex2 += 1
                      if (tableRecordLine.TableRecordLineFieldList.isDefined)
                        results ++ loopFieldsTableRecord(List(InstanceFields(tableRecordLine.TableRecordLineFieldList.get)), id, tenantId, opType, ggTimestamp, tdTimestamp, pos, Some(y.Label), xIndex, yIndex2)
                  }
                }
              case 3 =>
                if (y.IsFilled.getOrElse(false)) {
                  results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-0", 3, businessConcept, y.Label, y.TextBoxField.get.Value.get, tableRecordName, -1, opType, ggTimestamp, tdTimestamp, pos)))
                }
              case 4 =>
                if (y.DatetimeFieldId.isDefined && y.DatetimeFieldId.get.Value.isDefined) {
                  results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-0", 4, businessConcept, y.Label, y.DatetimeFieldId.get.Value.get, tableRecordName, -1, opType, ggTimestamp, tdTimestamp, pos)))
                }
              case 5 =>
                if (y.IsFilled.getOrElse(false) && y.LogicFieldId.isDefined) {
                  if (y.LogicFieldId.get.Value.getOrElse(false))
                    results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-0", 5, businessConcept, y.Label, y.LogicFieldId.get.LabelTrue.getOrElse(""), tableRecordName, -1, opType, ggTimestamp, tdTimestamp, pos)))
                  else
                    results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-0", 5, businessConcept, y.Label, y.LogicFieldId.get.LabelFalse.getOrElse(""), tableRecordName, -1, opType, ggTimestamp, tdTimestamp, pos)))
                }
              case 6 =>
                if (y.OptionListFieldId.isDefined && y.OptionListFieldId.get.OptionChoicesList.isDefined) {
                  val optionList = y.OptionListFieldId.get.OptionChoicesList.get
                  var zIndex = -1
                  optionList.foreach {
                    option =>
                      zIndex += 1
                      if (option.IsSelected.isDefined && option.IsSelected.get && option.Name.isDefined)
                        results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-" + zIndex, 6, businessConcept, y.Label, option.Name.get, tableRecordName, -1, opType, ggTimestamp, tdTimestamp, pos)))
                  }
                }
              case 7 =>
                if (y.NumericFieldId.isDefined && y.NumericFieldId.get.Value.isDefined)
                  results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-0", 7, businessConcept, y.Label, y.NumericFieldId.get.Value.get.toString, tableRecordName, -1, opType, ggTimestamp, tdTimestamp, pos)))
              case 8 =>
                if (y.AttachmentFieldId.isDefined && y.AttachmentFieldId.get.AttachmentLines.isDefined && y.AttachmentFieldId.get.AttachmentLines.get.AttachmentLineFieldList.isDefined && y.AttachmentFieldId.get.AttachmentLines.get.AttachmentLineFieldList.get.nonEmpty) {
                  val value = y.AttachmentFieldId.get.AttachmentLines.get.AttachmentLineFieldList.get.head.Value.getOrElse("")
                  val fileName = y.AttachmentFieldId.get.AttachmentLines.get.AttachmentLineFieldList.get.head.FileName.get
                  results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-0", 8, businessConcept, y.Label, value + ":" + fileName, tableRecordName, -1, opType, ggTimestamp, tdTimestamp, pos)))
                }
              case 9 =>
                if (y.OptionListFieldId.isDefined && y.OptionListFieldId.get.OptionChoicesList.isDefined) {
                  var zIndex = -1
                  y.OptionListFieldId.get.OptionChoicesList.get.foreach {
                    option =>
                      zIndex += 1
                      if (option.IsSelected.getOrElse(false))
                        results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-" + zIndex, 9, businessConcept, y.Label, option.Name.get, tableRecordName, -1, opType, ggTimestamp, tdTimestamp, pos)))
                  }
                }
              case 11 =>
                if (y.WeightField.isDefined && y.WeightField.get.ValueWeight.isDefined) {
                  val value = y.WeightField.get.ValueWeight.get.toString
                  results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-0", 11, businessConcept, y.Label, value, tableRecordName, -1, opType, ggTimestamp, tdTimestamp, pos)))
                }
              case 12 =>
                if (y.QualificationField.isDefined && y.QualificationField.get.Value.isDefined) {
                  val value = y.QualificationField.get.Value.get.toString
                  results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-0", 12, businessConcept, y.Label, value, tableRecordName, -1, opType, ggTimestamp, tdTimestamp, pos)))
                }
              case _ =>
            }
        }
    }
    results.toList
  }

  def loopFieldsTableRecord(fieldList: List[InstanceFields], id: Int, tenantId: Int, opType: String, ggTimestamp: String, tdTimestamp: String, pos: String, tableRecord: Option[String], xIndex: Int, yIndex: Int): List[JsonResponse] = {
    val tableRecordName = tableRecord match {
      case Some(x) => x
      case _ => "null"
    }
    var zIndex = -1
    fieldList.foreach {
      x =>
        x.FieldsList.foreach {
          y =>
            zIndex += 1
            val businessConcept = y.BusinessConceptId.getOrElse(0)
            y.DFormFieldTypeId match {
              case 3 =>
                if (y.IsFilled.getOrElse(false)) {
                  results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-" + zIndex, 3, businessConcept, y.Label, y.TextBoxField.get.Value.get, tableRecordName, yIndex, opType, ggTimestamp, tdTimestamp, pos)))
                }
              case 4 =>
                if (y.DatetimeFieldId.isDefined && y.DatetimeFieldId.get.Value.isDefined) {
                  results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-" + zIndex, 4, businessConcept, y.Label, y.DatetimeFieldId.get.Value.get, tableRecordName, yIndex, opType, ggTimestamp, tdTimestamp, pos)))
                }
              case 5 =>
                if (y.IsFilled.getOrElse(false) && y.LogicFieldId.isDefined) {
                  if (y.LogicFieldId.get.Value.getOrElse(false))
                    results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-" + zIndex, 5, businessConcept, y.Label, y.LogicFieldId.get.LabelTrue.getOrElse(""), tableRecordName, yIndex, opType, ggTimestamp, tdTimestamp, pos)))
                  else
                    results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-" + zIndex, 5, businessConcept, y.Label, y.LogicFieldId.get.LabelFalse.getOrElse(""), tableRecordName, yIndex, opType, ggTimestamp, tdTimestamp, pos)))
                }
              case 6 =>
                if (y.OptionListFieldId.isDefined && y.OptionListFieldId.get.OptionChoicesList.isDefined) {
                  val optionList = y.OptionListFieldId.get.OptionChoicesList.get
                  var zIndex = -1
                  optionList.foreach {
                    option =>
                      zIndex += 1
                      if (option.IsSelected.isDefined && option.IsSelected.get && option.Name.isDefined)
                        results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-" + zIndex, 6, businessConcept, y.Label, option.Name.get, tableRecordName, yIndex, opType, ggTimestamp, tdTimestamp, pos)))
                  }
                }
              case 7 =>
                if (y.NumericFieldId.isDefined && y.NumericFieldId.get.Value.isDefined)
                  results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-" + zIndex, 7, businessConcept, y.Label, y.NumericFieldId.get.Value.get.toString, tableRecordName, yIndex, opType, ggTimestamp, tdTimestamp, pos)))
              case 8 =>
                if (y.AttachmentFieldId.isDefined && y.AttachmentFieldId.get.AttachmentLines.isDefined && y.AttachmentFieldId.get.AttachmentLines.get.AttachmentLineFieldList.isDefined && y.AttachmentFieldId.get.AttachmentLines.get.AttachmentLineFieldList.get.nonEmpty) {
                  val value = y.AttachmentFieldId.get.AttachmentLines.get.AttachmentLineFieldList.get.head.Value.getOrElse("")
                  val fileName = y.AttachmentFieldId.get.AttachmentLines.get.AttachmentLineFieldList.get.head.FileName.get
                  results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-" + zIndex, 8, businessConcept, y.Label, value + ":" + fileName, tableRecordName, yIndex, opType, ggTimestamp, tdTimestamp, pos)))
                }
              case 9 =>
                if (y.OptionListFieldId.isDefined && y.OptionListFieldId.get.OptionChoicesList.isDefined) {
                  var zIndex = -1
                  y.OptionListFieldId.get.OptionChoicesList.get.foreach {
                    option =>
                      zIndex += 1
                      if (option.IsSelected.getOrElse(false))
                        results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-" + zIndex, 9, businessConcept, y.Label, option.Name.get, tableRecordName, yIndex, opType, ggTimestamp, tdTimestamp, pos)))
                  }
                }
              case 11 =>
                if (y.WeightField.isDefined && y.WeightField.get.ValueWeight.isDefined) {
                  val value = y.WeightField.get.ValueWeight.get.toString
                  results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-" + zIndex, 11, businessConcept, y.Label, value, tableRecordName, yIndex, opType, ggTimestamp, tdTimestamp, pos)))
                }
              case 12 =>
                if (y.QualificationField.isDefined && y.QualificationField.get.Value.isDefined) {
                  val value = y.QualificationField.get.Value.get.toString
                  results += dummyJson.copy(payload = Some(ResponseMessage(id, tenantId, xIndex + "-" + yIndex + "-" + zIndex, 12, businessConcept, y.Label, value, tableRecordName, yIndex, opType, ggTimestamp, tdTimestamp, pos)))
                }
              case _ =>
            }
        }
    }
    results.toList
  }

}
