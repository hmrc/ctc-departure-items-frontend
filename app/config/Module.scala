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

package config

import com.google.inject.AbstractModule
import controllers.actions._
import navigation._

import java.time.{Clock, ZoneOffset}

class Module extends AbstractModule {

  override def configure(): Unit = {

    bind(classOf[DataRetrievalActionProvider]).to(classOf[DataRetrievalActionProviderImpl]).asEagerSingleton()
    bind(classOf[DataRequiredAction]).to(classOf[DataRequiredActionImpl]).asEagerSingleton()
    bind(classOf[LockActionProvider]).to(classOf[LockActionProviderImpl]).asEagerSingleton()
    bind(classOf[DependentTasksAction]).to(classOf[DependentTasksActionImpl]).asEagerSingleton()

    bind(classOf[ItemsNavigatorProvider]).to(classOf[ItemsNavigatorProviderImpl])
    bind(classOf[ItemNavigatorProvider]).to(classOf[ItemNavigatorProviderImpl])
    bind(classOf[DangerousGoodsNavigatorProvider]).to(classOf[DangerousGoodsNavigatorProviderImpl])
    bind(classOf[PackageNavigatorProvider]).to(classOf[PackageNavigatorProviderImpl])
    bind(classOf[SupplyChainActorNavigatorProvider]).to(classOf[SupplyChainActorNavigatorProviderImpl])
    bind(classOf[DocumentNavigatorProvider]).to(classOf[DocumentNavigatorProviderImpl])
    bind(classOf[AdditionalReferenceNavigatorProvider]).to(classOf[AdditionalReferenceNavigatorProviderImpl])
    bind(classOf[AdditionalInformationNavigatorProvider]).to(classOf[AdditionalInformationNavigatorProviderImpl])

    // For session based storage instead of cred based, change to SessionIdentifierAction
    bind(classOf[IdentifierAction]).to(classOf[IdentifierActionImpl]).asEagerSingleton()
    bind(classOf[SpecificDataRequiredActionProvider]).to(classOf[SpecificDataRequiredActionImpl]).asEagerSingleton()

    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone.withZone(ZoneOffset.UTC))
  }
}
