package pages.item.consignee

import pages.behaviours.PageBehaviours
import models.DynamicAddress

class AddressPageSpec extends PageBehaviours {

  "AddressPage" - {

    beRetrievable[DynamicAddress](AddressPage)

    beSettable[DynamicAddress](AddressPage)

    beRemovable[DynamicAddress](AddressPage)
  }
}
