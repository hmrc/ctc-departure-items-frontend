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

import config.PhaseConfig
import models.journeyDomain.item.ItemDomain
import models.journeyDomain.item.additionalInformation.AdditionalInformationDomain
import models.journeyDomain.item.additionalReferences.AdditionalReferenceDomain
import models.journeyDomain.item.dangerousGoods.DangerousGoodsDomain
import models.journeyDomain.item.documents.DocumentDomain
import models.journeyDomain.item.packages.PackageDomain
import models.journeyDomain.item.supplyChainActors.SupplyChainActorDomain
import models.journeyDomain.{ItemsDomain, ReaderError, UserAnswersReader}
import models.{EoriNumber, Index, LocalReferenceNumber, RichJsObject, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import config.PhaseConfig

trait UserAnswersGenerator extends UserAnswersEntryGenerators {
  self: Generators =>

  implicit def arbitraryUserAnswers(implicit phaseConfig: PhaseConfig): Arbitrary[UserAnswers] =
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

  def arbitraryItemsAnswers(userAnswers: UserAnswers)(implicit phaseConfig: PhaseConfig): Gen[UserAnswers] =
    buildUserAnswers[ItemsDomain](userAnswers)

  def arbitraryItemAnswers(userAnswers: UserAnswers, index: Index)(implicit phaseConfig: PhaseConfig): Gen[UserAnswers] =
    buildUserAnswers[ItemDomain](userAnswers)(ItemDomain.userAnswersReader(index))

  def arbitraryDangerousGoodsAnswers(userAnswers: UserAnswers, itemIndex: Index, dangerousGoodsIndex: Index): Gen[UserAnswers] =
    buildUserAnswers[DangerousGoodsDomain](userAnswers)(DangerousGoodsDomain.userAnswersReader(itemIndex, dangerousGoodsIndex))

  def arbitraryPackageAnswers(userAnswers: UserAnswers, itemIndex: Index, packageIndex: Index): Gen[UserAnswers] =
    buildUserAnswers[PackageDomain](userAnswers)(PackageDomain.userAnswersReader(itemIndex, packageIndex))

  def arbitrarySupplyChainActorAnswers(userAnswers: UserAnswers, itemIndex: Index, actorIndex: Index): Gen[UserAnswers] =
    buildUserAnswers[SupplyChainActorDomain](userAnswers)(SupplyChainActorDomain.userAnswersReader(itemIndex, actorIndex))

  def arbitraryDocumentAnswers(userAnswers: UserAnswers, itemIndex: Index, documentIndex: Index): Gen[UserAnswers] =
    buildUserAnswers[DocumentDomain](userAnswers)(DocumentDomain.userAnswersReader(itemIndex, documentIndex))

  def arbitraryAdditionalReferenceAnswers(userAnswers: UserAnswers, itemIndex: Index, additionalReferenceIndex: Index): Gen[UserAnswers] =
    buildUserAnswers[AdditionalReferenceDomain](userAnswers)(AdditionalReferenceDomain.userAnswersReader(itemIndex, additionalReferenceIndex))

  def arbitraryAdditionalInformationAnswers(userAnswers: UserAnswers, itemIndex: Index, additionalInformationIndex: Index): Gen[UserAnswers] =
    buildUserAnswers[AdditionalInformationDomain](userAnswers)(AdditionalInformationDomain.userAnswersReader(itemIndex, additionalInformationIndex))

}
