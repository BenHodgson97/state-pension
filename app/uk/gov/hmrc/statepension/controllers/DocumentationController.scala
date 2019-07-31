/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.statepension.controllers

import com.google.inject.Inject
import play.api.http.HttpErrorHandler
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.statepension.config.{APIAccessConfig, AppContext}
import uk.gov.hmrc.statepension.domain.APIAccess
import uk.gov.hmrc.statepension.views._

class DocumentationController @Inject()(errorHandler: HttpErrorHandler, appContext: AppContext)
  extends uk.gov.hmrc.api.controllers.DocumentationController(errorHandler = errorHandler) {

  override def definition(): Action[AnyContent] = Action {
    Ok(txt.definition(buildAccess(), buildStatus())).as("application/json")
  }

  private def buildAccess(): APIAccess = {
    val access = APIAccessConfig(appContext.access)
    APIAccess(access.accessType, access.whiteListedApplicationIds)
  }

  private def buildStatus(): String = appContext.status.getOrElse("BETA")
}
