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

import config.FrontendAppConfig
import models.{Index, Mode, UserAnswers}
import pages.Page
import play.api.mvc.Call

class FakeNavigator(desiredRoute: Call) extends Navigator {
  override def nextPage(userAnswers: UserAnswers, currentPage: Option[Page]): Call = desiredRoute
}

class FakeItemsNavigator(desiredRoute: Call, mode: Mode)(implicit config: FrontendAppConfig) extends ItemsNavigator(mode) {
  override def nextPage(userAnswers: UserAnswers, currentPage: Option[Page]): Call = desiredRoute
}

class FakeItemNavigator(desiredRoute: Call, mode: Mode, index: Index)(implicit config: FrontendAppConfig) extends ItemNavigator(mode, index) {
  override def nextPage(userAnswers: UserAnswers, currentPage: Option[Page]): Call = desiredRoute
}

class FakeDangerousGoodsNavigator(desiredRoute: Call, mode: Mode, itemIndex: Index, dangerousGoodsIndex: Index)(implicit
  config: FrontendAppConfig
) extends DangerousGoodsNavigator(mode, itemIndex, dangerousGoodsIndex) {
  override def nextPage(userAnswers: UserAnswers, currentPage: Option[Page]): Call = desiredRoute
}

class FakePackageNavigator(desiredRoute: Call, mode: Mode, itemIndex: Index, packageIndex: Index)(implicit config: FrontendAppConfig)
    extends PackageNavigator(mode, itemIndex, packageIndex) {
  override def nextPage(userAnswers: UserAnswers, currentPage: Option[Page]): Call = desiredRoute
}

class FakeSupplyChainActorNavigator(desiredRoute: Call, mode: Mode, itemIndex: Index, actorIndex: Index)(implicit
  config: FrontendAppConfig
) extends PackageNavigator(mode, itemIndex, actorIndex) {
  override def nextPage(userAnswers: UserAnswers, currentPage: Option[Page]): Call = desiredRoute
}

class FakeDocumentNavigator(desiredRoute: Call, mode: Mode, itemIndex: Index, documentIndex: Index)(implicit
  config: FrontendAppConfig
) extends DocumentNavigator(mode, itemIndex, documentIndex) {
  override def nextPage(userAnswers: UserAnswers, currentPage: Option[Page]): Call = desiredRoute
}

class FakeAdditionalReferenceNavigator(desiredRoute: Call, mode: Mode, itemIndex: Index, additionalReferenceIndex: Index)(implicit
  config: FrontendAppConfig
) extends AdditionalReferenceNavigator(mode, itemIndex, additionalReferenceIndex) {
  override def nextPage(userAnswers: UserAnswers, currentPage: Option[Page]): Call = desiredRoute
}

class FakeAdditionalInformationNavigator(desiredRoute: Call, mode: Mode, itemIndex: Index, additionalInformationIndex: Index)(implicit
  config: FrontendAppConfig
) extends AdditionalInformationNavigator(mode, itemIndex, additionalInformationIndex) {
  override def nextPage(userAnswers: UserAnswers, currentPage: Option[Page]): Call = desiredRoute
}
