package pages.item

import pages.behaviours.PageBehaviours

class AddUCRYesNoPageSpec extends PageBehaviours {

  "AddUCRYesNoPage" - {

    beRetrievable[Boolean](AddUCRYesNoPage)

    beSettable[Boolean](AddUCRYesNoPage)

    beRemovable[Boolean](AddUCRYesNoPage)
  }
}
