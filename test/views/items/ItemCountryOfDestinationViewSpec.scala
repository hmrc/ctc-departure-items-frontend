package views.items

import forms.CountryFormProvider
import views.behaviours.InputSelectViewBehaviours
import models.NormalMode
import models.reference.Country
import models.CountryList
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.html.items.ItemCountryOfDestinationView

class ItemCountryOfDestinationViewSpec extends InputSelectViewBehaviours[Country] {

  override def form: Form[Country] = new CountryFormProvider()(prefix, CountryList(values))

  override def applyView(form: Form[Country]): HtmlFormat.Appendable =
    injector.instanceOf[ItemCountryOfDestinationView].apply(form, lrn, values, NormalMode)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[Country] = arbitraryCountry

  override val prefix: String = "items.itemCountryOfDestination"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithSelect()

  behave like pageWithHint("What is the item’s country of destination? hint")

  behave like pageWithContent("label", "What is the item’s country of destination? label")

  behave like pageWithSubmitButton("Save and continue")
}
