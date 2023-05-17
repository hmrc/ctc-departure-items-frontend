package pages.item

import pages.behaviours.PageBehaviours

class AddSupplyChainActorYesNoPageSpec extends PageBehaviours {

  "AddSupplyChainActorYesNoPage" - {

    beRetrievable[Boolean](AddSupplyChainActorYesNoPage)

    beSettable[Boolean](AddSupplyChainActorYesNoPage)

    beRemovable[Boolean](AddSupplyChainActorYesNoPage)
  }
}
