package forms

import forms.mappings.Mappings
import models.{Selectable, SelectableList}
import play.api.data.Form

import javax.inject.Inject

class SelectableFormProvider @Inject() extends Mappings {

  def apply[T <: Selectable](prefix: String, selectableList: SelectableList[T], args: Any*): Form[T] =
    Form(
      "value" -> selectable[T](selectableList, s"$prefix.error.required", args)
    )
}
