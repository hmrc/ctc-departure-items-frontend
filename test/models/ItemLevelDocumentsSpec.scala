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

package models

import base.{AppWithDefaultMockFixtures, SpecBase}
import config.FrontendAppConfig
import generators.Generators
import org.mockito.Mockito.reset
import org.scalacheck.Gen
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.test.Helpers.running
import services.DocumentsService

class ItemLevelDocumentsSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators with BeforeAndAfterEach {

  implicit private val mockDocumentsService: DocumentsService = mock[DocumentsService]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDocumentsService)
  }

  "Item Level Documents" - {

    "must not allow addition of another document" - {
      "when current amount is maximum amount" - {
        "when during transition" - {

          val app = transitionApplicationBuilder().build()
          running(app) {

            val frontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

            "and previous document" in {
              forAll(Gen.choose(frontendAppConfig.maxPreviousDocuments, Int.MaxValue)) {
                previous =>
                  val ild = ItemLevelDocuments(
                    previous = previous,
                    support = 0,
                    transport = 0
                  )
                  ild.canAdd(DocumentType.Previous)(frontendAppConfig) mustBe false
              }
            }

            "and supporting document" in {
              forAll(Gen.choose(frontendAppConfig.maxSupportingDocuments, Int.MaxValue)) {
                support =>
                  val ild = ItemLevelDocuments(
                    previous = 0,
                    support = support,
                    transport = 0
                  )
                  ild.canAdd(DocumentType.Support)(frontendAppConfig) mustBe false
              }
            }

            "and transport document" in {
              forAll(Gen.choose(frontendAppConfig.maxTransportDocuments, Int.MaxValue)) {
                transport =>
                  val ild = ItemLevelDocuments(
                    previous = 0,
                    support = 0,
                    transport = transport
                  )
                  ild.canAdd(DocumentType.Transport)(frontendAppConfig) mustBe false
              }
            }
          }
        }

        "when post-transition" - {

          val app = postTransitionApplicationBuilder().build()
          running(app) {

            val frontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

            "and previous document" in {
              forAll(Gen.choose(frontendAppConfig.maxPreviousDocuments, Int.MaxValue)) {
                previous =>
                  val ild = ItemLevelDocuments(
                    previous = previous,
                    support = 0,
                    transport = 0
                  )
                  ild.canAdd(DocumentType.Previous)(frontendAppConfig) mustBe false
              }
            }

            "and supporting document" in {
              forAll(Gen.choose(frontendAppConfig.maxSupportingDocuments, Int.MaxValue)) {
                support =>
                  val ild = ItemLevelDocuments(
                    previous = 0,
                    support = support,
                    transport = 0
                  )
                  ild.canAdd(DocumentType.Support)(frontendAppConfig) mustBe false
              }
            }

            "and transport document" in {
              forAll(Gen.choose(frontendAppConfig.maxTransportDocuments, Int.MaxValue)) {
                transport =>
                  val ild = ItemLevelDocuments(
                    previous = 0,
                    support = 0,
                    transport = transport
                  )
                  ild.canAdd(DocumentType.Transport)(frontendAppConfig) mustBe false
              }
            }
          }
        }
      }
    }

    "must allow addition of another document" - {
      "when current amount is less than maximum amount" - {
        "when during transition" - {

          val app = transitionApplicationBuilder().build()
          running(app) {

            val frontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

            "and previous document" in {
              forAll(Gen.choose(0, frontendAppConfig.maxPreviousDocuments - 1)) {
                previous =>
                  val ild = ItemLevelDocuments(
                    previous = previous,
                    support = 0,
                    transport = 0
                  )
                  ild.canAdd(DocumentType.Previous)(frontendAppConfig) mustBe true
              }
            }

            "and supporting document" in {
              forAll(Gen.choose(0, frontendAppConfig.maxSupportingDocuments - 1)) {
                support =>
                  val ild = ItemLevelDocuments(
                    previous = 0,
                    support = support,
                    transport = 0
                  )
                  ild.canAdd(DocumentType.Support)(frontendAppConfig) mustBe true
              }
            }

            "and transport document" in {
              forAll(Gen.choose(0, frontendAppConfig.maxTransportDocuments - 1)) {
                transport =>
                  val ild = ItemLevelDocuments(
                    previous = 0,
                    support = 0,
                    transport = transport
                  )
                  ild.canAdd(DocumentType.Transport)(frontendAppConfig) mustBe true
              }
            }
          }
        }

        "when post-transition" - {

          val app = postTransitionApplicationBuilder().build()
          running(app) {

            val frontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

            "and previous document" in {
              forAll(Gen.choose(0, frontendAppConfig.maxPreviousDocuments - 1)) {
                previous =>
                  val ild = ItemLevelDocuments(
                    previous = previous,
                    support = 0,
                    transport = 0
                  )
                  ild.canAdd(DocumentType.Previous)(frontendAppConfig) mustBe true
              }
            }

            "and supporting document" in {
              forAll(Gen.choose(0, frontendAppConfig.maxSupportingDocuments - 1)) {
                support =>
                  val ild = ItemLevelDocuments(
                    previous = 0,
                    support = support,
                    transport = 0
                  )
                  ild.canAdd(DocumentType.Support)(frontendAppConfig) mustBe true
              }
            }

            "and transport document" in {
              forAll(Gen.choose(0, frontendAppConfig.maxTransportDocuments - 1)) {
                transport =>
                  val ild = ItemLevelDocuments(
                    previous = 0,
                    support = 0,
                    transport = transport
                  )
                  ild.canAdd(DocumentType.Transport)(frontendAppConfig) mustBe true
              }
            }
          }
        }
      }
    }
  }
}
