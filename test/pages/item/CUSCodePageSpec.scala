package pages.item

import pages.behaviours.PageBehaviours

class CUSCodePageSpec extends PageBehaviours {

  "CUSCodePage" - {

    beRetrievable[Boolean](CUSCodePage)

    beSettable[Boolean](CUSCodePage)

    beRemovable[Boolean](CUSCodePage)
  }
}
