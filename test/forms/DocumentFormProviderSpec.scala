/*
 * Copyright 2024 HM Revenue & Customs
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

package forms

import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import generators.Generators
import models.{Document, ItemLevelDocuments, SelectableList}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.data.FormError

class DocumentFormProviderSpec extends SpecBase with StringFieldBehaviours with Generators {

  private val prefix             = Gen.alphaNumStr.sample.value
  private val requiredKey        = s"$prefix.error.required"
  private val maxLimitReachedKey = s"$prefix.error.maxLimitReached"

  private val selectable1 = arbitrary[Document](arbitraryPreviousDocument).sample.value
  private val selectable2 = arbitrary[Document](arbitraryTransportDocument).sample.value

  private val selectableList = SelectableList(Seq(selectable1, selectable2))
  private val arg            = Gen.alphaNumStr.sample.value

  private val itemLevelDocuments = ItemLevelDocuments(Nil)

  private val form = new DocumentFormProvider()(prefix, selectableList, itemLevelDocuments, arg)

  ".value" - {

    val fieldName = "document"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      nonEmptyString
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(arg))
    )

    "not bind if value does not exist in the list" in {
      val boundForm = form.bind(Map(fieldName -> "foobar"))
      val field     = boundForm(fieldName)
      field.errors must contain(FormError(fieldName, requiredKey, Seq(arg)))
    }

    "bind a value which is in the list" in {
      val boundForm = form.bind(Map(fieldName -> selectable1.value))
      val field     = boundForm(fieldName)
      field.errors mustBe empty
    }

    "not bind if value takes me over the max limit" - {
      "when previous" in {
        val document1          = arbitrary[Document](arbitraryPreviousDocument).sample.value
        val document2          = arbitrary[Document](arbitraryPreviousDocument).sample.value
        val documents          = SelectableList(Seq(document1, document2))
        val itemLevelDocuments = ItemLevelDocuments(frontendAppConfig.maxPreviousDocuments, 0, 0)
        val form               = new DocumentFormProvider()(prefix, documents, itemLevelDocuments, arg)
        val boundForm          = form.bind(Map(fieldName -> document2.toString))
        val field              = boundForm(fieldName)
        field.errors must contain(FormError(fieldName, maxLimitReachedKey))
      }

      "when supporting" in {
        val document1          = arbitrary[Document](arbitrarySupportingDocument).sample.value
        val document2          = arbitrary[Document](arbitrarySupportingDocument).sample.value
        val documents          = SelectableList(Seq(document1, document2))
        val itemLevelDocuments = ItemLevelDocuments(0, frontendAppConfig.maxSupportingDocuments, 0)
        val form               = new DocumentFormProvider()(prefix, documents, itemLevelDocuments, arg)
        val boundForm          = form.bind(Map(fieldName -> document2.toString))
        val field              = boundForm(fieldName)
        field.errors must contain(FormError(fieldName, maxLimitReachedKey))
      }

      "when transport" in {
        val document1          = arbitrary[Document](arbitraryTransportDocument).sample.value
        val document2          = arbitrary[Document](arbitraryTransportDocument).sample.value
        val documents          = SelectableList(Seq(document1, document2))
        val itemLevelDocuments = ItemLevelDocuments(0, 0, frontendAppConfig.maxTransportDocuments)
        val form               = new DocumentFormProvider()(prefix, documents, itemLevelDocuments, arg)
        val boundForm          = form.bind(Map(fieldName -> document2.toString))
        val field              = boundForm(fieldName)
        field.errors must contain(FormError(fieldName, maxLimitReachedKey))
      }
    }

    "bind if limit reached for supporting and previous but transport doc is chosen" in {
      val previousDocument  = arbitrary[Document](arbitraryPreviousDocument).sample.value
      val transportDocument = arbitrary[Document](arbitraryTransportDocument).sample.value
      val documents         = SelectableList(Seq(previousDocument, transportDocument))
      val itemLevelDocuments =
        ItemLevelDocuments(frontendAppConfig.maxPreviousDocuments, frontendAppConfig.maxSupportingDocuments, 0)
      val form      = new DocumentFormProvider()(prefix, documents, itemLevelDocuments, arg)
      val boundForm = form.bind(Map(fieldName -> transportDocument.toString))
      val field     = boundForm(fieldName)
      field.errors mustBe empty
    }
  }
}
