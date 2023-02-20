/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package views.utils

import play.api.data.{Field, FormError}
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.Aliases._
import uk.gov.hmrc.govukfrontend.views.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.input.Input

object ViewUtils {

  def breadCrumbTitle(title: String, mainContent: Html)(implicit messages: Messages): String =
    (if (mainContent.body.contains("govuk-error-summary")) s"${messages("error.title.prefix")} " else "") +
      s"$title - ${messages("site.service_name")} - GOV.UK"

  def errorClass(error: Option[FormError], dateArg: String): String =
    error.fold("") {
      e =>
        if (e.args.contains(dateArg) || e.args.isEmpty) {
          "govuk-input--error"
        } else {
          ""
        }
    }

  implicit class InputImplicits(input: Input)(implicit messages: Messages) extends RichInputSupport {

    def withHeadingAndCaption(heading: String, caption: Option[String]): Input =
      caption match {
        case Some(value) => input.withHeadingAndSectionCaption(Text(heading), Text(value))
        case None        => input.withHeading(Text(heading))
      }
  }

  implicit class RadiosImplicits(radios: Radios)(implicit messages: Messages) extends RichRadiosSupport {

    def withHeadingAndCaption(heading: String, caption: Option[String]): Radios =
      caption match {
        case Some(value) => radios.withHeadingAndSectionCaption(Text(heading), Text(value))
        case None        => radios.withHeading(Text(heading))
      }

    def withLegend(legend: String, legendIsVisible: Boolean = true): Radios = {
      val legendClass = if (legendIsVisible) "govuk-fieldset__legend--m" else "govuk-visually-hidden"
      radios.copy(
        fieldset = Some(Fieldset(legend = Some(Legend(content = Text(legend), classes = legendClass, isPageHeading = false))))
      )
    }
  }

  implicit class TextAreaImplicits(textArea: Textarea)(implicit messages: Messages) extends RichTextareaSupport {

    def withHeadingAndCaption(heading: String, caption: Option[String]): Textarea =
      caption match {
        case Some(value) => textArea.withHeadingAndSectionCaption(Text(heading), Text(value))
        case None        => textArea.withHeading(Text(heading))
      }
  }

  implicit class SelectImplicits(select: Select)(implicit messages: Messages) extends RichSelectSupport {

    def withHeadingAndCaption(heading: String, caption: Option[String]): Select =
      caption match {
        case Some(value) => select.withHeadingAndSectionCaption(Text(heading), Text(value))
        case None        => select.withHeading(Text(heading))
      }
  }

  implicit class FieldsetImplicits(fieldset: Fieldset)(implicit val messages: Messages) extends ImplicitsSupport[Fieldset] {
    override def withFormField(field: Field): Fieldset = fieldset

    override def withFormFieldWithErrorAsHtml(field: Field): Fieldset = fieldset

    def withHeadingAndCaption(heading: String, caption: Option[String]): Fieldset =
      withHeadingLegend(fieldset, Text(heading), caption.map(Text))(
        (fs, l) => fs.copy(legend = Some(l))
      )
  }

  implicit class CharacterCountImplicits(characterCount: CharacterCount)(implicit messages: Messages) extends RichCharacterCountSupport {

    def withHeadingAndCaption(heading: String, caption: Option[String]): CharacterCount =
      caption match {
        case Some(value) => characterCount.withHeadingAndSectionCaption(Text(heading), Text(value))
        case None        => characterCount.withHeading(Text(heading))
      }
  }

  implicit class StringImplicits(string: String) {
    def toParagraph: Html = Html(s"""<p class="govuk-body">$string</p>""")
  }
}
