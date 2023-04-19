package forms

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class IdentificationNumber @Inject() extends Mappings {

  def apply(prefix: String): Form[String] =
    Form(
      "value" -> text(s"$prefix.error.required")
        .verifying(maxLength(35, s"$prefix.error.length"))
    )
}
