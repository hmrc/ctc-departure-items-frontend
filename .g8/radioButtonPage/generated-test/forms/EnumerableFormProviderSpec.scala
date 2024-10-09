package forms

import forms.behaviours.OptionFieldBehaviours
import models.{EnumerableType, Radioable}
import org.scalacheck.Gen
import play.api.data.FormError

class EnumerableFormProviderSpec extends OptionFieldBehaviours {

  private val prefix: String = Gen.alphaNumStr.sample.value

  sealed private trait FakeEnum extends Radioable[FakeEnum] {
    override val messageKeyPrefix: String = prefix
  }

  private object FakeEnum extends EnumerableType[FakeEnum] {
    lazy val messageKeyPrefix: String = prefix

    case class Foo(code: String) extends FakeEnum
    case class Bar(code: String) extends FakeEnum

    override val values: Seq[FakeEnum] = Seq(Foo("foo"), Bar("bar"))
  }

  private val formProvider = new EnumerableFormProvider()
  private val form         = formProvider.apply[FakeEnum](prefix)

  ".value" - {

    val fieldName   = "value"
    val requiredKey = s"\$prefix.error.required"

    behave like optionsField[FakeEnum](
      form,
      fieldName,
      validValues = FakeEnum.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
