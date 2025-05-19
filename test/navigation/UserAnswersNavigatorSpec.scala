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

package navigation

import base.SpecBase
import models.journeyDomain.OpsError.ReaderError
import models.journeyDomain.Stage.{AccessingJourney, CompletingJourney}
import models.journeyDomain.{JourneyDomainModel, ReaderSuccess, Stage}
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import org.scalacheck.Gen
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call
import uk.gov.hmrc.http.HttpVerbs.GET

class UserAnswersNavigatorSpec extends SpecBase {

  private case object FooPage extends QuestionPage[String] {
    override def path: JsPath = JsPath \ "foo"

    override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] = Some(Call(GET, "/foo"))
  }

  private case object BarPage extends QuestionPage[String] {
    override def path: JsPath = JsPath \ "bar"

    override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] = Some(Call(GET, "/bar"))
  }

  private case object BazPage extends QuestionPage[String] {
    override def path: JsPath = JsPath \ "baz"

    override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] = Some(Call(GET, "/baz"))
  }

  private val userAnswers = emptyUserAnswers

  private val stage = Gen.oneOf(AccessingJourney, CompletingJourney).sample.value

  private case object FakeDomainModel extends JourneyDomainModel {

    override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] =
      Some(Call(GET, "/cya"))
  }

  "UserAnswersNavigator" - {
    "nextPage" - {
      "when in normal mode" - {
        val mode = NormalMode

        "and no pages answered" in {
          val currentPage             = None
          val answeredPages           = Nil
          val userAnswersReaderResult = Left(ReaderError(FooPage, answeredPages))

          val result = UserAnswersNavigator
            .nextPage(currentPage, userAnswersReaderResult, mode)
            .apply(userAnswers, stage)

          result.value.url mustBe "/foo"
        }

        "and on FooPage" - {
          val currentPage = Some(FooPage)

          "must redirect to BarPage" - {
            "when BarPage answered" in {
              val answeredPages           = Seq(FooPage, BarPage)
              val userAnswersReaderResult = Left(ReaderError(BazPage, answeredPages))

              val result = UserAnswersNavigator
                .nextPage(currentPage, userAnswersReaderResult, mode)
                .apply(userAnswers, stage)

              result.value.url mustBe "/bar"
            }

            "when BarPage unanswered" in {
              val answeredPages           = Seq(FooPage)
              val userAnswersReaderResult = Left(ReaderError(BarPage, answeredPages))

              val result = UserAnswersNavigator
                .nextPage(currentPage, userAnswersReaderResult, mode)
                .apply(userAnswers, stage)

              result.value.url mustBe "/bar"
            }
          }
        }

        "and on BarPage" - {
          val currentPage = Some(BarPage)

          "must redirect to BazPage" - {
            "when BazPage answered" in {
              val answeredPages           = Seq(FooPage, BarPage, BazPage)
              val userAnswersReaderResult = Right(ReaderSuccess(FakeDomainModel, answeredPages))

              val result = UserAnswersNavigator
                .nextPage(currentPage, userAnswersReaderResult, mode)
                .apply(userAnswers, stage)

              result.value.url mustBe "/baz"
            }

            "when BazPage unanswered" in {
              val answeredPages           = Seq(FooPage, BarPage)
              val userAnswersReaderResult = Left(ReaderError(BazPage, answeredPages))

              val result = UserAnswersNavigator
                .nextPage(currentPage, userAnswersReaderResult, mode)
                .apply(userAnswers, stage)

              result.value.url mustBe "/baz"
            }
          }
        }
      }

      "when in check mode" - {
        val mode = CheckMode

        "and on FooPage" - {
          val currentPage = Some(FooPage)

          "must redirect to BarPage" - {
            "when BarPage answered" in {
              val answeredPages           = Seq(FooPage, BarPage)
              val userAnswersReaderResult = Left(ReaderError(BazPage, answeredPages))

              val result = UserAnswersNavigator
                .nextPage(currentPage, userAnswersReaderResult, mode)
                .apply(userAnswers, stage)

              result.value.url mustBe "/bar"
            }

            "when BarPage unanswered" in {
              val answeredPages           = Seq(FooPage)
              val userAnswersReaderResult = Left(ReaderError(BarPage, answeredPages))

              val result = UserAnswersNavigator
                .nextPage(currentPage, userAnswersReaderResult, mode)
                .apply(userAnswers, stage)

              result.value.url mustBe "/bar"
            }
          }

          "must redirect to cya" - {
            "when all questions answered" in {
              val answeredPages           = Seq(FooPage, BarPage, BazPage)
              val userAnswersReaderResult = Right(ReaderSuccess(FakeDomainModel, answeredPages))

              val result = UserAnswersNavigator
                .nextPage(currentPage, userAnswersReaderResult, mode)
                .apply(userAnswers, stage)

              result.value.url mustBe "/cya"
            }
          }
        }

        "and on BarPage" - {
          val currentPage = Some(BarPage)

          "must redirect to BazPage" - {
            "when BazPage unanswered" in {
              val answeredPages           = Seq(FooPage, BarPage)
              val userAnswersReaderResult = Left(ReaderError(BazPage, answeredPages))

              val result = UserAnswersNavigator
                .nextPage(currentPage, userAnswersReaderResult, mode)
                .apply(userAnswers, stage)

              result.value.url mustBe "/baz"
            }
          }

          "must redirect to cya" - {
            "when all questions answered" in {
              val answeredPages           = Seq(FooPage, BarPage, BazPage)
              val userAnswersReaderResult = Right(ReaderSuccess(FakeDomainModel, answeredPages))

              val result = UserAnswersNavigator
                .nextPage(currentPage, userAnswersReaderResult, mode)
                .apply(userAnswers, stage)

              result.value.url mustBe "/cya"
            }
          }
        }

        "and on BazPage" - {
          val currentPage = Some(BazPage)

          "must redirect to cya" - {
            "when all questions answered" in {
              val answeredPages           = Seq(FooPage, BarPage, BazPage)
              val userAnswersReaderResult = Right(ReaderSuccess(FakeDomainModel, answeredPages))

              val result = UserAnswersNavigator
                .nextPage(currentPage, userAnswersReaderResult, mode)
                .apply(userAnswers, stage)

              result.value.url mustBe "/cya"
            }
          }
        }
      }
    }
  }
}
