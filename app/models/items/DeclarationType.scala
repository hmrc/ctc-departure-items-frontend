package models.items

import models.{RadioModel, WithName}

sealed trait DeclarationType

object DeclarationType extends RadioModel[DeclarationType] {

  case object Option1 extends WithName("option1") with DeclarationType
  case object Option2 extends WithName("option2") with DeclarationType

  override val messageKeyPrefix: String = "items.declarationType"

  val values: Seq[DeclarationType] = Seq(
    Option1,
    Option2
  )
}
