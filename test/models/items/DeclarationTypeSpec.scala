package models.items

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import play.api.libs.json.{JsError, JsString, Json}

class DeclarationTypeSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "DeclarationType" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(DeclarationType.values)

      forAll(gen) {
        declarationType =>

          JsString(declarationType.toString).validate[DeclarationType].asOpt.value mustEqual declarationType
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!DeclarationType.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[DeclarationType] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(DeclarationType.values)

      forAll(gen) {
        declarationType =>

          Json.toJson(declarationType) mustEqual JsString(declarationType.toString)
      }
    }
  }
}
