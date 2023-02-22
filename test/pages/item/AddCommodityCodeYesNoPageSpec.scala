package pages.item

import pages.behaviours.PageBehaviours

class AddCommodityCodeYesNoPageSpec extends PageBehaviours {

  "AddCommodityCodeYesNoPage" - {

    beRetrievable[Boolean](AddCommodityCodeYesNoPage)

    beSettable[Boolean](AddCommodityCodeYesNoPage)

    beRemovable[Boolean](AddCommodityCodeYesNoPage)
  }
}
