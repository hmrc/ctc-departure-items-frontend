package forms

import forms.mappings.Mappings
import play.api.data.Form
import utils.Format.RichLocalDate

import java.time.LocalDate
import javax.inject.Inject

class DateFormProvider @Inject() extends Mappings {

  def apply(prefix: String, minimumDate: LocalDate, maximumDate: LocalDate): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey = s"\$prefix.error.invalid",
        requiredKey = s"\$prefix.error.required"
      ).verifying(
        minDate(minimumDate, s"\$prefix.error.min.date", minimumDate.minusDays(1).formatAsString),
        maxDate(maximumDate, s"\$prefix.error.max.date", maximumDate.plusDays(1).formatAsString)
      )
    )
}
