/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.actions

import base.{AppWithDefaultMockFixtures, SpecBase}
import models.UserAnswers
import models.requests.DataRequest
import pages.sections.Section
import play.api.libs.json.{JsObject, JsPath, Json}
import play.api.mvc.{AnyContent, Call, Result, Results}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class IndexRequiredActionSpec extends SpecBase with AppWithDefaultMockFixtures {

  private case object FooSection extends Section[JsObject] {
    override def path: JsPath = JsPath \ "foo"
  }

  private val addAnotherPage = Call("GET", "url")

  def harness(userAnswers: UserAnswers): Result = {
    val dataRequest: DataRequest[AnyContent] = DataRequest(fakeRequest, eoriNumber, userAnswers)
    val actionProvider                       = new IndexRequiredActionProviderImpl()
    actionProvider(FooSection, addAnotherPage)
      .invokeBlock(
        dataRequest,
        (_: DataRequest[AnyContent]) => Future.successful(Results.Ok)
      )
      .futureValue
  }

  "Index Required Action" - {

    "must return Ok when index exists in user answers" in {
      val userAnswers = emptyUserAnswers.setValue(FooSection, Json.obj("foo" -> "bar"))
      harness(userAnswers) mustBe Results.Ok
    }

    "must redirect to add-another page when index does not exist in user answers" in {
      harness(emptyUserAnswers) mustBe Results.SeeOther(addAnotherPage.url)
    }
  }

}
