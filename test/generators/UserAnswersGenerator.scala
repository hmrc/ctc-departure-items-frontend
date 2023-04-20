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

package generators

import models.journeyDomain.item.ItemDomain
import models.journeyDomain.item.dangerousGoods.DangerousGoodsDomain
import models.journeyDomain.item.documents.DocumentDomain
import models.journeyDomain.item.packages.PackageDomain
import models.journeyDomain.{ItemsDomain, ReaderError, UserAnswersReader}
import models.{EoriNumber, Index, LocalReferenceNumber, RichJsObject, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait UserAnswersGenerator extends UserAnswersEntryGenerators {
  self: Generators =>

  implicit lazy val arbitraryUserAnswers: Arbitrary[UserAnswers] =
    Arbitrary {
      for {
        lrn        <- arbitrary[LocalReferenceNumber]
        eoriNumber <- arbitrary[EoriNumber]
        answers    <- buildUserAnswers[ItemsDomain](UserAnswers(lrn, eoriNumber))
      } yield answers
    }

  protected def buildUserAnswers[T](
    initialUserAnswers: UserAnswers
  )(implicit userAnswersReader: UserAnswersReader[T]): Gen[UserAnswers] = {

    def rec(userAnswers: UserAnswers): Gen[UserAnswers] =
      userAnswersReader.run(userAnswers) match {
        case Left(ReaderError(page, _)) =>
          generateAnswer
            .apply(page)
            .map {
              value =>
                userAnswers.copy(
                  data = userAnswers.data.setObject(page.path, value).getOrElse(userAnswers.data)
                )
            }
            .flatMap(rec)
        case Right(_) => Gen.const(userAnswers)
      }

    rec(initialUserAnswers)
  }

  def arbitraryItemsAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[ItemsDomain](userAnswers)

  def arbitraryItemAnswers(userAnswers: UserAnswers, index: Index): Gen[UserAnswers] =
    buildUserAnswers[ItemDomain](userAnswers)(ItemDomain.userAnswersReader(index))

  def arbitraryDangerousGoodsAnswers(userAnswers: UserAnswers, itemIndex: Index, dangerousGoods: Index): Gen[UserAnswers] =
    buildUserAnswers[DangerousGoodsDomain](userAnswers)(DangerousGoodsDomain.userAnswersReader(itemIndex, dangerousGoods))

  def arbitraryPackageAnswers(userAnswers: UserAnswers, itemIndex: Index, packageIndex: Index): Gen[UserAnswers] =
    buildUserAnswers[PackageDomain](userAnswers)(PackageDomain.userAnswersReader(itemIndex, packageIndex))

  def arbitraryDocumentAnswers(userAnswers: UserAnswers, itemIndex: Index, documentIndex: Index): Gen[UserAnswers] =
    buildUserAnswers[DocumentDomain](userAnswers)(DocumentDomain.userAnswersReader(itemIndex, documentIndex))
}
