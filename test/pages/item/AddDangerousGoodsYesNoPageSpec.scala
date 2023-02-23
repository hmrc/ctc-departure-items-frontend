package pages.item

import pages.behaviours.PageBehaviours

class AddDangerousGoodsYesNoPageSpec extends PageBehaviours {

  "AddDangerousGoodsYesNoPage" - {

    beRetrievable[Boolean](AddDangerousGoodsYesNoPage)

    beSettable[Boolean](AddDangerousGoodsYesNoPage)

    beRemovable[Boolean](AddDangerousGoodsYesNoPage)
  }
}
