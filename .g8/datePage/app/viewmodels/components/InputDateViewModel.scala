package viewModels.components

import play.twirl.api.Html
import viewmodels.components.AdditionalHtmlViewModel

sealed trait InputDateViewModel

object InputDateViewModel {

  case class OrdinaryDateInput(
    heading: String,
    caption: Option[String] = None
  ) extends InputDateViewModel

  case class DateInputWithAdditionalHtml(
    heading: String,
    caption: Option[String] = None,
    additionalHtml: Html
  ) extends InputDateViewModel
      with AdditionalHtmlViewModel

}
