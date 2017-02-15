/*
 * Copyright 2017 HM Revenue & Customs
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

package uk.gov.hmrc.statepension.domain.nps

import org.joda.time.LocalDate
import play.api.libs.json._
import play.api.libs.functional.syntax._


case class NpsSummary(
                       earningsIncludedUpTo: LocalDate,
                       sex: String,
                       qualifyingYears: Int,
                       amounts: NpsStatePensionAmounts
                     )

object NpsSummary {

  implicit val reads: Reads[NpsSummary] = (
    (JsPath \ "earnings_included_upto").read[LocalDate] and
      (JsPath \ "sex").read[String] and
      (JsPath \ "nsp_qualifying_years").read[Int] and
      (JsPath \ "npsSpnam").read[NpsStatePensionAmounts]
    ) (NpsSummary.apply _)

}

case class NpsStatePensionAmounts(
                                   pensionEntitlement: BigDecimal,
                                   startingAmount2016: BigDecimal,
                                   protectedPayment2016: BigDecimal,
                                   additionalPensionAccruedLastTaxYear: BigDecimal,
                                   amountA2016: NpsAmountA2016,
                                   amountB2016: NpsAmountB2016
                                 )

object NpsStatePensionAmounts {

  val readBigDecimal: JsPath => Reads[BigDecimal] =
    jsPath => jsPath.readNullable[BigDecimal].map(_.getOrElse(0))

  implicit val reads: Reads[NpsStatePensionAmounts] = (
    readBigDecimal(JsPath \ "nsp_entitlement") and
      readBigDecimal(JsPath \ "starting_amount") and
      readBigDecimal(JsPath \ "protected_payment_2016") and
      readBigDecimal(JsPath \ "ap_amount") and
      (JsPath \ "npsAmnapr16").read[NpsAmountA2016] and
      (JsPath \ "npsAmnbpr16").read[NpsAmountB2016]
    ) (NpsStatePensionAmounts.apply _)
}

case class NpsAmountA2016(
                           basicPension: BigDecimal,
                           pre97AP: BigDecimal,
                           post97AP: BigDecimal,
                           post02AP: BigDecimal,
                           pre88GMP: BigDecimal,
                           post88GMP: BigDecimal,
                           pre88COD: BigDecimal,
                           post88COD: BigDecimal,
                           grb: BigDecimal
                         ) {
  val totalAP: BigDecimal = (pre97AP - (pre88GMP + post88GMP + pre88COD + post88COD)).max(0) + post97AP + post02AP + grb
  val total: BigDecimal = totalAP + basicPension
}

object NpsAmountA2016 {

  val readBigDecimal: JsPath => Reads[BigDecimal] =
    jsPath => jsPath.readNullable[BigDecimal].map(_.getOrElse(0))

  implicit val reads: Reads[NpsAmountA2016] = (
    readBigDecimal(JsPath \ "ltb_cat_a_cash_value") and
      readBigDecimal(JsPath \ "ltb_pre97_ap_cash_value") and
      readBigDecimal(JsPath \ "ltb_post97_ap_cash_value") and
      readBigDecimal(JsPath \ "ltb_post02_ap_cash_value") and
      readBigDecimal(JsPath \ "pre88_gmp") and
      readBigDecimal(JsPath \ "ltb_pst88_gmp_cash_value") and
      readBigDecimal(JsPath \ "ltb_pre88_cod_cash_value") and
      readBigDecimal(JsPath \ "ltb_post88_cod_cash_value") and
      readBigDecimal(JsPath \ "grb_cash")
    ) (NpsAmountA2016.apply _)

}

case class NpsAmountB2016(mainComponent: BigDecimal, rebateDerivedAmount: BigDecimal)

object NpsAmountB2016 {
  val readBigDecimal: JsPath => Reads[BigDecimal] =
    jsPath => jsPath.readNullable[BigDecimal].map(_.getOrElse(0))

  implicit val reads: Reads[NpsAmountB2016] = (
    readBigDecimal(JsPath \ "main_component") and
      readBigDecimal(JsPath \ "rebate_derived_amount")
    ) (NpsAmountB2016.apply _)
}