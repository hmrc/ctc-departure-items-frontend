package forms

import forms.behaviours.StringFieldBehaviours
import models.CountryList
import play.api.data.FormError
import generators.Generators
import org.scalacheck.Gen

class CountryFormProviderSpec extends StringFieldBehaviours with Generators {

  private val prefix      = Gen.alphaNumStr.sample.value
  private val requiredKey = s"$prefix.error.required"
  private val maxLength   = 8

  private val country1    = arbitraryCountry.arbitrary.sample.get
  private val country2    = arbitraryCountry.arbitrary.sample.get
  private val countryList = CountryList(Seq(country1, country2))

  private val form = new CountryFormProvider()(prefix, countryList)

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

    "not bind if customs office id does not exist in the countryList" in {
      val boundForm = form.bind(Map("value" -> "foobar"))
      val field     = boundForm("value")
      field.errors mustNot be(empty)
    }

    "bind a country id which is in the list" in {
      val boundForm = form.bind(Map("value" -> country1.id))
      val field     = boundForm("value")
      field.errors must be(empty)
    }
  }
}
