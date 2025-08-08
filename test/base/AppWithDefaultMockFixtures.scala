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

package base

import config.FrontendAppConfig
import controllers.actions.*
import models.{Index, Mode, UserAnswers}
import navigation.*
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.{BeforeAndAfterEach, TestSuite}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.{GuiceFakeApplicationFactory, GuiceOneAppPerSuite}
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{bind, Injector}
import play.api.mvc.{AnyContent, Call}
import play.api.test.FakeRequest
import repositories.SessionRepository

import scala.concurrent.Future

trait AppWithDefaultMockFixtures extends BeforeAndAfterEach with GuiceOneAppPerSuite with GuiceFakeApplicationFactory with MockitoSugar {
  self: TestSuite & SpecBase =>

  def injector: Injector = app.injector

  def fakeRequest: FakeRequest[AnyContent] = FakeRequest("", "")

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  implicit def messages: Messages = messagesApi.preferred(fakeRequest)

  implicit def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  override def beforeEach(): Unit = {
    reset(mockSessionRepository); reset(mockDataRetrievalActionProvider)

    when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))
    when(mockLockActionProvider.apply()).thenReturn(new FakeLockAction())
  }

  final val mockSessionRepository: SessionRepository                             = mock[SessionRepository]
  final private val mockDataRetrievalActionProvider: DataRetrievalActionProvider = mock[DataRetrievalActionProvider]
  final private val mockLockActionProvider: LockActionProvider                   = mock[LockActionProvider]

  final override def fakeApplication(): Application =
    guiceApplicationBuilder()
      .build()

  protected def setExistingUserAnswers(userAnswers: UserAnswers): Unit = setUserAnswers(Some(userAnswers))

  protected def setNoExistingUserAnswers(): Unit = setUserAnswers(None)

  private def setUserAnswers(userAnswers: Option[UserAnswers]): Unit =
    when(mockDataRetrievalActionProvider.apply(any())).thenReturn(new FakeDataRetrievalAction(userAnswers))

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
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[LockActionProvider].toInstance(mockLockActionProvider),
        bind[SessionRepository].toInstance(mockSessionRepository),
        bind[DataRetrievalActionProvider].toInstance(mockDataRetrievalActionProvider),
        bind[DependentTasksAction].to[FakeDependentTasksAction]
      )

  protected def guiceApplicationBuilder(): GuiceApplicationBuilder =
    defaultApplicationBuilder()
}
