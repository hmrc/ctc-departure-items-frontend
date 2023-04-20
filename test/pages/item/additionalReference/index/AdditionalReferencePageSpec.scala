package pages.item.additionalReference.index

import models.reference.AdditionalReference
import pages.behaviours.PageBehaviours

class AdditionalReferencePageSpec extends PageBehaviours {

  "AdditionalReferencePage" - {

    beRetrievable[AdditionalReference](AdditionalReferencePage)

    beSettable[AdditionalReference](AdditionalReferencePage)

    beRemovable[AdditionalReference](AdditionalReferencePage)
  }
}
