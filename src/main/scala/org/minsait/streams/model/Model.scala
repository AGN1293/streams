package org.minsait.streams.model

case class JsonMessage(
                       current_ts: String,
                       after: After
                      )

case class After(TENANT_ID: Int,
                 ID: Int,
                 FORMINSTANCEFIELDS: Option[List[InstanceFields]])

case class InstanceFields(FieldsList: List[Fields])

case class Fields(
                   DFormFieldTypeId: Int,
                   IsFilled: Option[Boolean],
                   Label: String,
                   TextBoxField: Option[TextBoxField],
                   NumericFieldId: Option[NumericFieldId],
                   LogicFieldId: Option[LogicFieldId],
                   OptionListFieldId: Option[OptionListFieldId],
                   DatetimeFieldId: Option[DatetimeFieldId],
                   AttachmentFieldId: Option[AttachmentFieldId])
case class Weight()

case class TextBoxField(Value: Option[String])

case class NumericFieldId(Value: Option[Float])

case class LogicFieldId(Value: Option[Boolean],
                        LabelTrue: Option[String],
                        LabelFalse: Option[String])

case class OptionListFieldId(
                              OptionChoicesList: Option[List[OptionChoices]])

case class OptionChoices(
                          Name: Option[String],
                          IsSelected: Option[Boolean]
                        )

case class DatetimeFieldId(isDateTime: Option[Boolean],
                           isDate: Option[Boolean],
                           Value: Option[String])

case class AttachmentFieldId(
                              AttachmentLines: Option[AttachmentLines]
                            )

case class AttachmentLines(
                            AttachmentLineFieldList: Option[List[AttachmentLineField]]
                          )

case class AttachmentLineField(
                                Value: Option[String],
                                FileName: Option[String]
                              )

case class JsonResponse(schema: Schema, payload: Option[ResponseMessage] = None)

case class Schema(`type`: String = "struct", fields: List[PayloadFields])

case class PayloadFields(`type`: String, field: String)

case class ResponseMessage(
                            ID: Int,
                            TENANT_ID: Int,
                            ITEM: String,
                            TIPO: Int,
                            ETIQUETA: String,
                            VALOR: String,
                            GG_T_TIMESTAMP: String
                          )