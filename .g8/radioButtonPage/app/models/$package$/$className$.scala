package models.$package$

import models.{EnumerableType, Radioable, WithName}

sealed trait $className$ extends Radioable[$className$] {
  override val messageKeyPrefix: String = $className$.messageKeyPrefix
  override val code: String             = this.toString
}

object $className$ extends EnumerableType[$className$] {

  case object $option1key;format="Camel"$ extends WithName("$option1key;format="decap"$") with $className$
  case object $option2key;format="Camel"$ extends WithName("$option2key;format="decap"$") with $className$

  val messageKeyPrefix: String = "$package$.$className;format="decap"$"

  val values: Seq[$className$] = Seq(
    $option1key;format="Camel"$,
    $option2key;format="Camel"$
  )
}
