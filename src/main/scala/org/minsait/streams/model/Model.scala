package org.minsait.streams.model

case class JsonMessage(table: String,
                       op_type: String,
                       op_ts: String,
                       current_ts: String,
                       pos: String,
                       after: After)

case class After(TENANT_ID: Int,
                 ID: Int,
                 FORMINSTANCEFIELDS: Option[List[InstanceFields]],
                 FORMINSTANCEWEIGHTINGS: Option[Seq[String]])

case class InstanceFields(Name: String,
                          Guid: String,
                          FieldsList: List[Fields],
                          Order: Option[Int],
                          IsFork: Option[Boolean],
                          ParentId: Option[String],
                          ParentGuid: Option[String],
                          ForkOrder: Option[Int])

case class Fields(
                   DFormFieldTypeId: Int,
                   Guid: String,
                   IsMandatory: Option[Boolean],
                   IsFilled: Option[Boolean],
                   IsHidden: Option[Boolean],
                   Label: String,
                   Order: Int,
                   BusinessConceptId: Option[Int],
                   TextBoxField: Option[TextBoxField],
                   NumericFieldId: Option[NumericFieldId],
                   LogicFieldId: Option[LogicFieldId],
                   OptionListFieldId: Option[OptionListFieldId],
                   DatetimeFieldId: Option[DatetimeFieldId],
                   AttachmentFieldId: Option[AttachmentFieldId],
                   LabelFieldId: Option[LabelFieldId])

case class Weight()

case class TextBoxField(
                         HasMultiLine: Option[Boolean],
                         MaxLength: Option[Int],
                         Prompt: Option[String],
                         Value: Option[String]
                       )

case class NumericFieldId(
                           Value: Option[Int],
                           MaxValue: Option[Long],
                           MinValue: Option[Long]
                         )

case class LogicFieldId(
                         Value: Option[Boolean],
                         LabelTrue: Option[String],
                         LabelFalse: Option[String],
                         TrueIsFork: Option[Boolean],
                         TrueForkGuid: Option[String],
                         TrueForkId: Option[String]
                       )

case class OptionListFieldId(
                              OptionChoicesList: Option[List[OptionChoices]],
                              HasMultipleChoice: Option[Boolean]
                            )

case class OptionChoices(
                          Name: Option[String],
                          Guid: Option[String],
                          Order: Option[Int],
                          IsSelected: Option[Boolean],
                          Value: Option[String],
                          ForkGuid: Option[String]
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
case class LabelFieldId (
                          HasMultiline: Option[Boolean]
                        )

case class ResponseMessage(
                     ID: Int,
                     TENANT_ID: Int,
                     CUSTOM_ID: String,
                     FORM_FIELD: Int,
                     LABEL: String,
                     VALUE: String,
                     FILE_NAME: Option[String] = None
                   )