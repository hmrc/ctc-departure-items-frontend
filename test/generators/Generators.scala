/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package generators

import cats.data.NonEmptyList
import models.DateTime
import org.scalacheck.Arbitrary._
import org.scalacheck.Gen._
import org.scalacheck.{Arbitrary, Gen, Shrink}
import wolfendale.scalacheck.regexp.RegexpGen

import java.time._
import scala.util.matching.Regex

// scalastyle:off number.of.methods
trait Generators extends UserAnswersGenerator with ModelGenerators with DomainModelGenerators with ViewModelGenerators {

  lazy val stringMaxLength = 35

  lazy val maxListLength = 10

  require(stringMaxLength > 1, "Value for `stringMaxLength` must be greater than 1")

  implicit val dontShrink: Shrink[String] = Shrink.shrinkAny

  def genNumberString: Gen[String] = arbitrary[Int].map(_.toString)

  def genIntersperseString(gen: Gen[String], value: String, frequencyV: Int = 1, frequencyN: Int = 10): Gen[String] = {

    val genValue: Gen[Option[String]] = Gen.frequency(frequencyN -> None, frequencyV -> Gen.const(Some(value)))

    for {
      seq1 <- gen
      seq2 <- Gen.listOfN(seq1.length, genValue)
    } yield seq1.toSeq.zip(seq2).foldRight("") {
      case ((n, Some(v)), m) =>
        m + n + v
      case ((n, _), m) =>
        m + n
    }
  }

  def intsInRangeWithCommas(min: Int, max: Int): Gen[String] = {
    val numberGen = choose[Int](min, max)
    genIntersperseString(numberGen.toString, ",")
  }

  def positiveBigDecimals: Gen[BigDecimal] = positiveInts.map(BigDecimal(_))

  def intsLargerThanMaxValue: Gen[BigInt] =
    arbitrary[BigInt] retryUntil (
      x => x > Int.MaxValue
    )

  def intsSmallerThanMinValue: Gen[BigInt] =
    arbitrary[BigInt] retryUntil (
      x => x < Int.MinValue
    )

  def nonNumerics: Gen[String] =
    alphaStr suchThat (_.nonEmpty)

  def decimals: Gen[String] =
    arbitrary[BigDecimal]
      .retryUntil(_.abs < Int.MaxValue)
      .retryUntil(!_.isValidInt)
      .map("%f".format(_))

  def decimalsPositive: Gen[String] =
    arbitrary[BigDecimal]
      .retryUntil(
        x => x.signum >= 0
      )
      .retryUntil(
        x => x.abs <= Int.MaxValue
      )
      .retryUntil(!_.isValidInt)
      .map("%f".format(_))

  def intsBelowValue(value: Int): Gen[Int] =
    arbitrary[Int] retryUntil (_ < value)

  def intsAboveValue(value: Int): Gen[Int] =
    arbitrary[Int] retryUntil (_ > value)

  def doublesBelowValue(value: Double): Gen[Double] =
    arbitrary[Double] retryUntil (_ < value)

  def positiveInts: Gen[Int] = Gen.choose(0, Int.MaxValue)

  def positiveIntsMinMax(min: Int, max: Int): Gen[Int] = Gen.choose(min, max)

  def intsOutsideRange(min: Int, max: Int): Gen[Int] =
    arbitrary[Int] retryUntil (
      x => x < min || x > max
    )

  def intsInsideRange(min: Int, max: Int): Gen[Int] =
    arbitrary[Int] retryUntil (
      x => x > min && x < max
    )

  def nonBooleans: Gen[String] =
    nonEmptyString
      .retryUntil(_ != "true")
      .retryUntil(_ != "false")

  def nonEmptyString: Gen[String] =
    for {
      length <- choose(1, stringMaxLength)
      chars  <- listOfN(length, Gen.alphaNumChar)
    } yield chars.mkString

  def stringsThatMatchRegex(regex: Regex): Gen[String] =
    RegexpGen.from(regex.regex).suchThat(_.nonEmpty)

  def stringsWithLengthInRange(minLength: Int, maxLength: Int, charGen: Gen[Char] = Gen.alphaNumChar): Gen[String] =
    for {
      length <- choose(minLength, maxLength)
      chars  <- listOfN(length, charGen)
    } yield chars.mkString

  def stringsWithMaxLength(maxLength: Int, charGen: Gen[Char] = Gen.alphaNumChar): Gen[String] =
    for {
      length <- choose(1, maxLength)
      chars  <- listOfN(length, charGen)
    } yield chars.mkString

  def stringsWithLength(length: Int, charGen: Gen[Char] = Gen.alphaNumChar): Gen[String] =
    for {
      chars <- listOfN(length, charGen)
    } yield chars.mkString

  def stringsWithLengthNotEqual(length: Int, charGen: Gen[Char] = Gen.alphaNumChar): Gen[String] =
    for {
      len   <- Gen.posNum[Int].filter(_ != length)
      chars <- listOfN(len, charGen)
    } yield chars.mkString

  def stringsLongerThan(minLength: Int, charGen: Gen[Char] = Gen.alphaNumChar): Gen[String] = for {
    maxLength <- (minLength * 2).max(100)
    length    <- Gen.chooseNum(minLength + 1, maxLength)
    chars     <- listOfN(length, charGen)
  } yield chars.mkString

  def stringsExceptSpecificValues(excluded: Seq[String]): Gen[String] =
    nonEmptyString retryUntil (!excluded.contains(_))

  def oneOf[T](xs: Seq[Gen[T]]): Gen[T] =
    if (xs.isEmpty) {
      throw new IllegalArgumentException("oneOf called on empty collection")
    } else {
      val vector = xs.toVector
      choose(0, vector.size - 1).flatMap(vector(_))
    }

  def datesBetween(min: LocalDate, max: LocalDate): Gen[LocalDate] = {

    def toMillis(date: LocalDate): Long =
      date.atStartOfDay.atZone(ZoneOffset.UTC).toInstant.toEpochMilli

    Gen.choose(toMillis(min), toMillis(max)).map {
      millis =>
        Instant.ofEpochMilli(millis).atOffset(ZoneOffset.UTC).toLocalDate
    }
  }

  def dateTimesBetween(min: LocalDateTime, max: LocalDateTime): Gen[LocalDateTime] = {

    def toMillis(date: LocalDateTime): Long =
      date.atZone(ZoneOffset.UTC).toInstant.toEpochMilli

    Gen.choose(toMillis(min), toMillis(max)).map {
      millis =>
        Instant.ofEpochMilli(millis).atOffset(ZoneOffset.UTC).toLocalDateTime
    }
  }

  def nonEmptyListOf[A](maxLength: Int)(implicit a: Arbitrary[A]): Gen[NonEmptyList[A]] =
    listWithMaxLength[A](maxLength).map(NonEmptyList.fromListUnsafe)

  def listWithMaxLength[A](maxLength: Int = maxListLength)(implicit a: Arbitrary[A]): Gen[List[A]] =
    for {
      length <- choose(1, maxLength)
      seq    <- listOfN(length, arbitrary[A])
    } yield seq

  def listWithMaxLength[T](maxSize: Int, gen: Gen[T]): Gen[Seq[T]] =
    for {
      size  <- Gen.choose(0, maxSize)
      items <- Gen.listOfN(size, gen)
    } yield items

  def nonEmptyListWithMaxSize[T](maxSize: Int, gen: Gen[T]): Gen[NonEmptyList[T]] =
    for {
      head     <- gen
      tailSize <- Gen.choose(1, maxSize - 1)
      tail     <- Gen.listOfN(tailSize, gen)
    } yield NonEmptyList(head, tail)

  implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary {
    datesBetween(LocalDate.of(1900, 1, 1), LocalDate.of(2100, 1, 1))
  }

  implicit lazy val arbitraryLocalTime: Arbitrary[LocalTime] = Arbitrary {
    dateTimesBetween(
      LocalDateTime.of(1900, 1, 1, 0, 0, 0),
      LocalDateTime.of(2100, 1, 1, 0, 0, 0)
    ).map(
      result => result.toLocalTime.minusSeconds(result.getSecond).minusNanos(result.getNano)
    )
  }

  implicit lazy val arbitraryLocalDateTime: Arbitrary[LocalDateTime] = Arbitrary {
    dateTimesBetween(
      LocalDateTime.of(1900, 1, 1, 0, 0, 0),
      LocalDateTime.of(2100, 1, 1, 0, 0, 0)
    ).map(
      x => x.withNano(0).withSecond(0)
    )
  }

  implicit lazy val arbitraryDateTime: Arbitrary[DateTime] = Arbitrary {
    dateTimesBetween(
      min = LocalDateTime.of(2000, 1, 1, 23, 55, 0),
      max = LocalDateTime.now(ZoneOffset.UTC)
    ).map {
      localDateTime =>
        val dateTimeWithoutSeconds = localDateTime.minusSeconds(localDateTime.getSecond).minusNanos(localDateTime.getNano)
        DateTime(dateTimeWithoutSeconds)
    }
  }

  lazy val genExemptNationalityCode: Gen[Int] =
    for {
      range1    <- Gen.chooseNum(20, 29)
      range2    <- Gen.chooseNum(50, 59)
      range3    <- Gen.chooseNum(70, 79)
      range4    <- Gen.oneOf(Seq(2, 5, 7))
      pickRange <- Gen.oneOf(range1, range2, range3, range4)
    } yield pickRange

  val localDateGen: Gen[LocalDate] = datesBetween(LocalDate.of(1900, 1, 1), LocalDate.now)

  implicit lazy val arbitraryAny: Arbitrary[Any] = Arbitrary {
    Gen.oneOf[Any](Gen.alphaNumStr, arbitrary[Int])
  }
}
// scalastyle:on number.of.methods
