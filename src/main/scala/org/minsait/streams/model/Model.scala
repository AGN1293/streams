package org.minsait.streams.model

case class JsonMessage(payload: After)

case class After(TENANT_ID: Int,
                 ID: Int,
                 POS: String,
                 FORMINSTANCEFIELDS: Option[List[InstanceFields]])

case class InstanceFields(FieldsList: Option[List[Fields]])

case class Fields(
                   DFormFieldTypeId: Int,
                   IsFilled: Option[Boolean],
                   Label: String,
                   TextBoxField: Option[TextBoxField],
                   NumericFieldId: Option[NumericFieldId],
                   LogicFieldId: Option[LogicFieldId],
                   OptionListFieldId: Option[OptionListFieldId],
                   DatetimeFieldId: Option[DatetimeFieldId],
                   AttachmentFieldId: Option[AttachmentFieldId],
                   BusinessConceptId: Option[Int],
                   WeightField: Option[WeightField],
                   QualificationField: Option[QualificationField],
                   TableRecordLines: Option[List[TableRecordLineFieldList]])

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

case class WeightField(
                        ValueWeight: Option[Float]
                      )

case class QualificationField(
                             Value: Option[Int]
                             )

case class TableRecordLineFieldList(
                             TableRecordLineFieldList: Option[List[Fields]]
                           )

case class JsonResponse(schema: Schema, payload: Option[ResponseMessage] = None)

case class Schema(`type`: String = "struct", fields: List[PayloadFields])

case class PayloadFields(`type`: String, field: String)

case class ResponseMessage(
                            ID: Int,
                            TENANT_ID: Int,
                            ITEM: String,
                            TIPO: Int,
                            BUSINESS_CONCEPT: Int,
                            ETIQUETA: String,
                            VALOR: String,
                            TABLERECORDNAME: String,
                            LINE: Int,
                            GG_T_TYPE: String,
                            GG_T_TIMESTAMP: String,
                            TD_T_TIMESTAMP: String,
                            POS: String
                          )