package pages.items

import models.items.DeclarationType
import pages.behaviours.PageBehaviours

class DeclarationTypePageSpec extends PageBehaviours {

  "DeclarationTypePage" - {

    beRetrievable[DeclarationType](DeclarationTypePage)

    beSettable[DeclarationType](DeclarationTypePage)

    beRemovable[DeclarationType](DeclarationTypePage)
  }
}
