package views.item.consignee

import forms.DynamicAddressFormProvider
import generators.Generators
import models.{DynamicAddress, NormalMode}
import org.scalacheck.Gen
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.DynamicAddressViewBehaviours
import views.html.item.consignee.AddressView

class AddressViewSpec extends DynamicAddressViewBehaviours with Generators {

  private val name = nonEmptyString.sample.value

  override def form: Form[DynamicAddress] = new DynamicAddressFormProvider()(prefix, isPostalCodeRequired, name)

  override def applyView(form: Form[DynamicAddress]): HtmlFormat.Appendable =
    injector.instanceOf[AddressView].apply(form, lrn, NormalMode, name, isPostalCodeRequired)(fakeRequest, messages)

  override val prefix: String = "item.consignee.address"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading(name)

  behave like pageWithAddressInput()

  behave like pageWithSubmitButton("Save and continue")
}
