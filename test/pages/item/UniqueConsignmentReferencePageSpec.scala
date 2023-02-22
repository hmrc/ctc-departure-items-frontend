package pages.item

import pages.behaviours.PageBehaviours

class UniqueConsignmentReferencePageSpec extends PageBehaviours {

  "UniqueConsignmentReferencePage" - {

    beRetrievable[String](UniqueConsignmentReferencePage)

    beSettable[String](UniqueConsignmentReferencePage)

    beRemovable[String](UniqueConsignmentReferencePage)
  }
}
