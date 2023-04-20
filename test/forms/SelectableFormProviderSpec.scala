package forms

import forms.behaviours.StringFieldBehaviours
import models.SelectableList
import play.api.data.FormError
import generators.Generators
import org.scalacheck.Gen

class SelectableFormProviderSpec extends StringFieldBehaviours with Generators {

  private val prefix      = Gen.alphaNumStr.sample.value
  private val requiredKey = s"$prefix.error.required"
  private val maxLength   = 8

  private val additionalReference1    = arbitraryAdditionalReference.arbitrary.sample.get
  private val additionalReference2    = arbitraryAdditionalReference.arbitrary.sample.get
  private val additionalReferenceList = SelectableList(Seq(additionalReference1, additionalReference2))

  private val form = new SelectableFormProvider()(prefix, additionalReferenceList)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "not bind if value does not exist in the list" in {
      val boundForm = form.bind(Map("value" -> "foobar"))
      val field     = boundForm("value")
      field.errors mustNot be(empty)
    }

    "bind a value which is in the list" in {
      val boundForm = form.bind(Map("value" -> additionalReference1.value))
      val field     = boundForm("value")
      field.errors must be(empty)
    }
  }
}
