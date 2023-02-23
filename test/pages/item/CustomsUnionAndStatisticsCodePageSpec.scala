package pages.item

import pages.behaviours.PageBehaviours

class CustomsUnionAndStatisticsCodePageSpec extends PageBehaviours {

  "CustomsUnionAndStatisticsCodePage" - {

    beRetrievable[String](CustomsUnionAndStatisticsCodePage)

    beSettable[String](CustomsUnionAndStatisticsCodePage)

    beRemovable[String](CustomsUnionAndStatisticsCodePage)
  }
}
