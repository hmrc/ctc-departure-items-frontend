package pages.items

import models.reference.Country
import pages.behaviours.PageBehaviours

class ItemCountryOfDestinationPageSpec extends PageBehaviours {

  "ItemCountryOfDestinationPage" - {

    beRetrievable[Country](ItemCountryOfDestinationPage)

    beSettable[Country](ItemCountryOfDestinationPage)

    beRemovable[Country](ItemCountryOfDestinationPage)
  }
}
