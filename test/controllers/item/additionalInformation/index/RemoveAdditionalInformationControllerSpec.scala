package controllers.item.additionalInformation.index

import base.{AppWithDefaultMockFixtures, SpecBase}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.item.additionalInformation.index.RemoveAdditionalInformationView

class RemoveAdditionalInformationControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private lazy val removeAdditionalInformationRoute = routes.RemoveAdditionalInformationController.onPageLoad(lrn).url

  "RemoveAdditionalInformation Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, removeAdditionalInformationRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[RemoveAdditionalInformationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(lrn)(request, messages).toString
    }
  }
}
