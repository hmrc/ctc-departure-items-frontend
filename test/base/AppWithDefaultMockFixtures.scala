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

package base

import config.{PostTransitionModule, TransitionModule}
import controllers.actions._
import models.{Index, Mode, UserAnswers}
import navigation._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.{BeforeAndAfterEach, TestSuite}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.{GuiceFakeApplicationFactory, GuiceOneAppPerSuite}
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Call
import repositories.SessionRepository
import services.LockService

import scala.concurrent.Future

trait AppWithDefaultMockFixtures extends BeforeAndAfterEach with GuiceOneAppPerSuite with GuiceFakeApplicationFactory with MockitoSugar {
  self: TestSuite with SpecBase =>

  override def beforeEach(): Unit = {
    reset(mockSessionRepository); reset(mockDataRetrievalActionProvider); reset(mockLockService)

    when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)
    when(mockLockService.checkLock(any())(any())).thenReturn(Future.successful(true))
  }

  final val mockSessionRepository: SessionRepository                     = mock[SessionRepository]
  final val mockDataRetrievalActionProvider: DataRetrievalActionProvider = mock[DataRetrievalActionProvider]
  final val mockLockActionProvider: LockActionProvider                   = mock[LockActionProvider]
  final val mockLockService: LockService                                 = mock[LockService]

  final override def fakeApplication(): Application =
    guiceApplicationBuilder()
      .build()

  protected def setExistingUserAnswers(userAnswers: UserAnswers): Unit = setUserAnswers(Some(userAnswers))

  protected def setNoExistingUserAnswers(): Unit = setUserAnswers(None)

  private def setUserAnswers(userAnswers: Option[UserAnswers]): Unit = {
    when(mockLockActionProvider.apply()) thenReturn new FakeLockAction(mockLockService)
    when(mockDataRetrievalActionProvider.apply(any())) thenReturn new FakeDataRetrievalAction(userAnswers)
  }

  protected val onwardRoute: Call = Call("GET", "/foo")

  protected val fakeNavigator: Navigator = new FakeNavigator(onwardRoute)

  protected val fakeItemsNavigatorProvider: ItemsNavigatorProvider =
    (mode: Mode) => new FakeItemsNavigator(onwardRoute, mode)

  protected val fakeItemNavigatorProvider: ItemNavigatorProvider =
    (mode: Mode, index: Index) => new FakeItemNavigator(onwardRoute, mode, index)

  protected val fakeDangerousGoodsNavigatorProvider: DangerousGoodsNavigatorProvider =
    (mode: Mode, itemIndex: Index, dangerousGoodsIndex: Index) => new FakeDangerousGoodsNavigator(onwardRoute, mode, itemIndex, dangerousGoodsIndex)

  protected val fakePackageNavigatorProvider: PackageNavigatorProvider =
    (mode: Mode, itemIndex: Index, packageIndex: Index) => new FakePackageNavigator(onwardRoute, mode, itemIndex, packageIndex)

  protected val fakeSupplyChainActorNavigatorProvider: SupplyChainActorNavigatorProvider =
    (mode: Mode, itemIndex: Index, actorIndex: Index) => new FakeSupplyChainActorNavigator(onwardRoute, mode, itemIndex, actorIndex)

  protected val fakeDocumentNavigatorProvider: DocumentNavigatorProvider =
    (mode: Mode, itemIndex: Index, documentIndex: Index) => new FakeDocumentNavigator(onwardRoute, mode, itemIndex, documentIndex)

  protected val fakeAdditionalReferenceNavigatorProvider: AdditionalReferenceNavigatorProvider =
    (mode: Mode, itemIndex: Index, additionalReferenceIndex: Index) =>
      new FakeAdditionalReferenceNavigator(onwardRoute, mode, itemIndex, additionalReferenceIndex)

  protected val fakeAdditionalInformationNavigatorProvider: AdditionalInformationNavigatorProvider =
    (mode: Mode, itemIndex: Index, additionalInformationIndex: Index) =>
      new FakeAdditionalInformationNavigator(onwardRoute, mode, itemIndex, additionalInformationIndex)

  private def defaultApplicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[LockActionProvider].toInstance(mockLockActionProvider),
        bind[SessionRepository].toInstance(mockSessionRepository),
        bind[DataRetrievalActionProvider].toInstance(mockDataRetrievalActionProvider),
        bind[DependentTasksAction].to[FakeDependentTasksAction],
        bind[LockService].toInstance(mockLockService)
      )

  def guiceApplicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[LockActionProvider].toInstance(mockLockActionProvider),
        bind[SessionRepository].toInstance(mockSessionRepository),
        bind[DataRetrievalActionProvider].toInstance(mockDataRetrievalActionProvider),
        bind[DependentTasksAction].to[FakeDependentTasksAction],
        bind[LockService].toInstance(mockLockService)
      )

  protected def transitionApplicationBuilder(): GuiceApplicationBuilder =
    defaultApplicationBuilder()
      .disable[PostTransitionModule]
      .bindings(new TransitionModule)

  protected def postTransitionApplicationBuilder(): GuiceApplicationBuilder =
    defaultApplicationBuilder()
      .disable[TransitionModule]
      .bindings(new PostTransitionModule)
}
