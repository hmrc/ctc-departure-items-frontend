package controllers.consignment

import base.{AppWithDefaultMockFixtures, SpecBase}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.consignment.ItemDescriptionView

class ItemDescriptionControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private lazy val itemDescriptionRoute = routes.ItemDescriptionController.onPageLoad(lrn).url

  "ItemDescription Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, itemDescriptionRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[ItemDescriptionView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(lrn)(request, messages).toString
    }
  }
}
