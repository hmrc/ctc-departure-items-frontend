/*
 * Copyright 2023 HM Revenue & Customs
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

package models

import base.SpecBase
import generators.Generators
import models.reference.{Country, CountryCode}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.Json

class CountryListSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  "getCountry" - {
    "return a country if it exists" in {
      forAll(nonEmptyListOf[Country](10)) {
        countries =>
          val countryList = CountryList(countries.toList)

          val countryCode = countries.head.code

          countryList.getCountry(countryCode).value mustEqual countries.head
      }
    }

    "return a None if it does not exists" in {
      val countries = Seq(
        Country(CountryCode("AA"), "country1"),
        Country(CountryCode("BB"), "country2"),
        Country(CountryCode("CC"), "country3")
      )

      val countryList = CountryList(countries)

      val countryCode = CountryCode("DD")

      countryList.getCountry(countryCode) mustBe None

    }
  }

  "equals" - {
    "returns true if both CountryLists are the same" in {
      val c1 = CountryList(Seq(Country(CountryCode("a"), "a")))
      val c2 = CountryList(Seq(Country(CountryCode("a"), "a")))
      c1 == c2 mustEqual true
    }

    "returns false if the rhs is not a CountryList" in {
      CountryList(Seq()) == 1 mustEqual false
    }

    "returns false if the rhs has a different list of countries" in {
      val c1 = CountryList(Seq(Country(CountryCode("a"), "a")))
      val c2 = CountryList(Seq(Country(CountryCode("b"), "b")))
      c1 == c2 mustEqual false
    }

    "returns false if the rhs has a different list of countries with duplicates" in {
      val c1 = CountryList(Seq(Country(CountryCode("a"), "a"), Country(CountryCode("a"), "a")))
      val c2 = CountryList(Seq(Country(CountryCode("a"), "a")))
      c1 == c2 mustEqual false
    }
  }

  "countriesOfRoutingReads" - {
    "must read countries of routing as CountryList" in {
      val json = Json.parse("""
          |[
          |  {
          |    "countryOfRouting": {
          |      "code": "IT",
          |      "description": "Italy"
          |    }
          |  },
          |  {
          |    "countryOfRouting": {
          |      "code": "FR",
          |      "description": "France"
          |    }
          |  }
          |]
          |""".stripMargin)

      val result = json.as[CountryList](CountryList.countriesOfRoutingReads)

      result mustBe CountryList(
        Seq(
          Country(CountryCode("IT"), "Italy"),
          Country(CountryCode("FR"), "France")
        )
      )
    }
  }
}
