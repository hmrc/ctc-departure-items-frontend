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

import models.Document
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import play.api.data.FormError
import play.api.mvc.Call
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.Aliases._
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import viewmodels.item.additionalInformation.AddAnotherAdditionalInformationViewModel
import viewmodels.item.additionalReference.AddAnotherAdditionalReferenceViewModel
import viewmodels.item.dangerousGoods.AddAnotherDangerousGoodsViewModel
import viewmodels.item.documents.AddAnotherDocumentViewModel
import viewmodels.item.packages.AddAnotherPackageViewModel
import viewmodels.{Link, ListItem}
import viewmodels.sections.Section

trait ViewModelGenerators {
  self: Generators =>

  private val maxSeqLength = 10

  implicit lazy val arbitraryText: Arbitrary[Text] = Arbitrary {
    for {
      content <- nonEmptyString
    } yield content.toText
  }

  implicit lazy val arbitraryContent: Arbitrary[Content] = Arbitrary {
    arbitrary[Text]
  }

  implicit lazy val arbitraryKey: Arbitrary[Key] = Arbitrary {
    for {
      content <- arbitrary[Content]
      classes <- Gen.alphaNumStr
    } yield Key(content, classes)
  }

  implicit lazy val arbitraryValue: Arbitrary[Value] = Arbitrary {
    for {
      content <- arbitrary[Content]
      classes <- Gen.alphaNumStr
    } yield Value(content, classes)
  }

  implicit lazy val arbitraryActionItem: Arbitrary[ActionItem] = Arbitrary {
    for {
      content            <- arbitrary[Content]
      href               <- nonEmptyString
      visuallyHiddenText <- Gen.option(Gen.alphaNumStr)
      classes            <- Gen.alphaNumStr
      attributes         <- Gen.const(Map.empty[String, String])
    } yield ActionItem(href, content, visuallyHiddenText, classes, attributes)
  }

  implicit lazy val arbitraryActions: Arbitrary[Actions] = Arbitrary {
    for {
      length <- Gen.choose(1, maxSeqLength)
      items  <- Gen.containerOfN[Seq, ActionItem](length, arbitrary[ActionItem])
    } yield Actions(items = items)
  }

  implicit lazy val arbitrarySummaryListRow: Arbitrary[SummaryListRow] = Arbitrary {
    for {
      key     <- arbitrary[Key]
      value   <- arbitrary[Value]
      classes <- Gen.alphaNumStr
      actions <- arbitrary[Option[Actions]]
    } yield SummaryListRow(key, value, classes, actions)
  }

  implicit lazy val arbitrarySection: Arbitrary[Section] = Arbitrary {
    for {
      sectionTitle <- nonEmptyString
      length       <- Gen.choose(1, maxSeqLength)
      rows         <- Gen.containerOfN[Seq, SummaryListRow](length, arbitrary[SummaryListRow])
      link         <- arbitrary[Link]
    } yield Section(sectionTitle, rows, link)
  }

  implicit lazy val arbitrarySections: Arbitrary[List[Section]] = Arbitrary {
    listWithMaxLength[Section]().retryUntil {
      sections =>
        val sectionTitles = sections.map(_.sectionTitle)
        sectionTitles.distinct.size == sectionTitles.size
    }
  }

  implicit lazy val arbitraryLink: Arbitrary[Link] = Arbitrary {
    for {
      id   <- nonEmptyString
      text <- nonEmptyString
      href <- nonEmptyString
    } yield Link(id, text, href)
  }

  implicit lazy val arbitraryFormError: Arbitrary[FormError] = Arbitrary {
    for {
      key     <- nonEmptyString
      message <- nonEmptyString
    } yield FormError(key, message)
  }

  implicit lazy val arbitraryLabel: Arbitrary[Label] = Arbitrary {
    for {
      forAttr       <- Gen.option(nonEmptyString)
      isPageHeading <- arbitrary[Boolean]
      classes       <- Gen.alphaNumStr
      attributes    <- Gen.const(Map.empty[String, String])
      content       <- arbitrary[Content]
    } yield Label(forAttr, isPageHeading, classes, attributes, content)
  }

  implicit lazy val arbitraryHint: Arbitrary[Hint] = Arbitrary {
    for {
      id         <- Gen.option(nonEmptyString)
      classes    <- Gen.alphaNumStr
      attributes <- Gen.const(Map.empty[String, String])
      content    <- arbitrary[Content]
    } yield Hint(id, classes, attributes, content)
  }

  implicit lazy val arbitraryHtml: Arbitrary[Html] = Arbitrary {
    for {
      text <- nonEmptyString
    } yield Html(text)
  }

  implicit lazy val arbitraryRadioItem: Arbitrary[RadioItem] = Arbitrary {
    for {
      content         <- arbitrary[Content]
      id              <- Gen.option(nonEmptyString)
      value           <- Gen.option(nonEmptyString)
      label           <- Gen.option(arbitrary[Label])
      hint            <- Gen.option(arbitrary[Hint])
      divider         <- Gen.option(nonEmptyString)
      checked         <- arbitrary[Boolean]
      conditionalHtml <- Gen.option(arbitrary[Html])
      disabled        <- arbitrary[Boolean]
      attributes      <- Gen.const(Map.empty[String, String])
    } yield RadioItem(content, id, value, label, hint, divider, checked, conditionalHtml, disabled, attributes)
  }

  implicit lazy val arbitraryRadioItems: Arbitrary[List[RadioItem]] = Arbitrary {
    for {
      radioItems   <- listWithMaxLength[RadioItem]()
      checkedIndex <- Gen.choose(0, radioItems.length - 1)
    } yield radioItems.zipWithIndex.map {
      case (radioItem, index) => radioItem.copy(checked = index == checkedIndex)
    }
  }

  implicit lazy val arbitraryListItem: Arbitrary[ListItem] = Arbitrary {
    for {
      name      <- nonEmptyString
      changeUrl <- nonEmptyString
      removeUrl <- Gen.option(nonEmptyString)
    } yield ListItem(name, changeUrl, removeUrl)
  }

  implicit lazy val arbitraryAddAnotherDangerousGoodsViewModel: Arbitrary[AddAnotherDangerousGoodsViewModel] = Arbitrary {
    for {
      listItems    <- arbitrary[Seq[ListItem]]
      onSubmitCall <- arbitrary[Call]
    } yield AddAnotherDangerousGoodsViewModel(listItems, onSubmitCall)
  }

  implicit lazy val arbitraryAddAnotherPackageViewModel: Arbitrary[AddAnotherPackageViewModel] = Arbitrary {
    for {
      listItems    <- arbitrary[Seq[ListItem]]
      onSubmitCall <- arbitrary[Call]
    } yield AddAnotherPackageViewModel(listItems, onSubmitCall)
  }

  implicit lazy val arbitraryAddAnotherDocumentViewModel: Arbitrary[AddAnotherDocumentViewModel] = Arbitrary {
    for {
      listItems    <- arbitrary[Seq[ListItem]]
      onSubmitCall <- arbitrary[Call]
      documents    <- listWithMaxLength[Document]()
    } yield AddAnotherDocumentViewModel(listItems, onSubmitCall, documents)
  }

  implicit lazy val arbitraryAddAnotherAdditionalReferenceViewModel: Arbitrary[AddAnotherAdditionalReferenceViewModel] = Arbitrary {
    for {
      listItems    <- arbitrary[Seq[ListItem]]
      onSubmitCall <- arbitrary[Call]
    } yield AddAnotherAdditionalReferenceViewModel(listItems, onSubmitCall)
  }

  implicit lazy val arbitraryAddAnotherAdditionalInformationViewModel: Arbitrary[AddAnotherAdditionalInformationViewModel] = Arbitrary {
    for {
      listItems    <- arbitrary[Seq[ListItem]]
      onSubmitCall <- arbitrary[Call]
    } yield AddAnotherAdditionalInformationViewModel(listItems, onSubmitCall)
  }
}
