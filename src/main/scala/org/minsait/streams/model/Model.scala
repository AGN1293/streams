package org.minsait.streams.model

case class JsonMessage(
                       current_ts: String,
                       after: After
                      )

case class After(TENANT_ID: Int,
                 ID: Int,
                 FORMINSTANCEFIELDS: List[InstanceFields])

case class InstanceFields(Name: String,
                          Guid: String,
                          FieldsList: List[Fields],
                          Order: Int)

case class Fields(
                   DFormFieldTypeId: Int,
                   Guid: String,
                   IsMandatory: Option[Boolean],
                   IsFilled: Option[Boolean],
                   HelpText: Option[String],
                   Label: String,
                   Order: Int,
                   BusinessConceptId: Option[Int],
                   TextBoxField: Option[TextBoxField],
                   NumericFieldId: Option[NumericFieldId],
                   LogicFieldId: Option[LogicFieldId],
                   OptionListFieldId: Option[OptionListFieldId],
                   DatetimeFieldId: Option[DatetimeFieldId],
                   AttachmentFieldId: Option[AttachmentFieldId],
                   LabelFieldId: Option[LabelField],
                   WeightField: Option[String])

case class TextBoxField(
                         HasMultiLine: Option[Boolean],
                         MaxLength: Option[Int],
                         Prompt: Option[String],
                         Value: Option[String]
                       )

case class NumericFieldId(
                           Value: Option[Int],
                           MaxVaule: Option[Long]
                         )

case class LogicFieldId(
                         Value: Option[Boolean],
                         LabelTrue: Option[String],
                         LabelFalse: Option[String]
                       )

case class OptionListFieldId(
                              OptionChoicesList: Option[List[OptionChoices]]
                            )

case class OptionChoices(
                          Name: Option[String],
                          Guid: Option[String],
                          Order: Option[Int],
                          IsSelected: Option[Boolean],
                          Value: Option[String]
                        )

case class DatetimeFieldId(
                          isDateTime: Option[Boolean],
                          isDate: Option[Boolean],
                          Value: Option[String]
                          )

case class AttachmentFieldId(
                            isOnlyFile: Option[Boolean],
                            isOnlyPicture: Option[Boolean],
                            AttachmentLines: Option[AttachmentLines]
                            )

case class AttachmentLines(
                            AttachmentLineFieldList: Option[List[AttachmentLineField]]
                          )

case class AttachmentLineField(
                              Value: Option[String],
                              FileName: Option[String]
                              )

case class LabelField(
                     HasMultiLine: Option[Boolean]
                     )

case class ResponseMessage(
                            ID: Int,
                            TENANT_ID: Int,
                            ITEM: String,
                            TIPO: Int,
                            ETIQUETA: String,
                            VALOR: String,
                            GG_T_TIMESTAMP: String
                          )