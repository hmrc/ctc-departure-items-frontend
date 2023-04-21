package pages.item.additionalReference.index

import pages.behaviours.PageBehaviours

class AdditionalReferenceNumberPageSpec extends PageBehaviours {

  "AdditionalReferenceNumberPage" - {

    beRetrievable[String](AdditionalReferenceNumberPage)

    beSettable[String](AdditionalReferenceNumberPage)

    beRemovable[String](AdditionalReferenceNumberPage)
  }
}
