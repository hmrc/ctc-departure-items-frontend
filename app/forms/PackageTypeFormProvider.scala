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

package forms

import forms.mappings.Mappings
import models.PackageList
import models.reference.Package
import play.api.data.Form

import javax.inject.Inject

class PackageTypeFormProvider @Inject() extends Mappings {

  def apply(prefix: String, packageList: PackageList): Form[Package] =
    Form(
      "value" -> text(s"$prefix.error.required")
        .verifying(s"$prefix.error.required", value => packageList.packages.exists(_.code == value))
        .transform[Package](value => packageList.getPackageType(value).get, _.code)
    )
}
