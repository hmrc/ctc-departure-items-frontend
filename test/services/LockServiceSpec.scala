package services

import base.{AppWithDefaultMockFixtures, SpecBase}
import connectors.CacheConnector
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.Future

class LockServiceSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks {

  private val mockConnector = mock[CacheConnector]

  private val service = new LockService(mockConnector)

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[CacheConnector]).toInstance(mockConnector))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockConnector)
  }

  "Lock service" - {

    "when checkLock" - {
      "must call checkLock in connector" in {
        forAll(arbitrary[Boolean]) {
          response =>
            beforeEach()

            val userAnswers = emptyUserAnswers
            when(mockConnector.checkLock(any())(any())).thenReturn(Future.successful(response))
            val result = service.checkLock(userAnswers)
            result.futureValue mustBe response
            verify(mockConnector).checkLock(eqTo(userAnswers))(any())
        }
      }
    }

    "when deleteLock" - {
      "must call deleteLock in connector" in {
        forAll(arbitrary[Boolean]) {
          response =>
            beforeEach()

            val userAnswers = emptyUserAnswers
            when(mockConnector.deleteLock(any())(any())).thenReturn(Future.successful(response))
            val result = service.deleteLock(userAnswers)
            result.futureValue mustBe response
            verify(mockConnector).deleteLock(eqTo(userAnswers))(any())
        }
      }
    }
  }
}
