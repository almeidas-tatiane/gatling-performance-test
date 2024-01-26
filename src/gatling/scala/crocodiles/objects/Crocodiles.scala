package crocodiles.objects

import io.gatling.core.Predef.{StringBody, exec}
import io.gatling.http.Predef._
import utils.UrlProperties
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder

object Crocodiles {

  /* ----- TEST DATA ----- */
  private val testDataDir = "csv/crocodiles/"
  val searchCrocodiles = csv(testDataDir + "crocodiles.csv").circular

  /* ----- HEADERS ----- */
  val sentHeadersAll = Map(
    "Content-Type" -> "application/json",
    "X-Dynatrace-Test" -> "VU=$VU;SI=GATLING;TSN=SEARCH-ALL-CROCODILES;LSN=$LSN;LTN=$LTN;PC=$PC"
  )

  /* ----- REQUESTS ----- */
  def getAllCrocodiles: ChainBuilder = {
    feed(searchCrocodiles)
      .exec(
        http("Get All Crocodiles -> /public/crocodiles/")
          .get(UrlProperties.urls("api") + "/public/crocodiles/")
          .headers(sentHeadersAll)
      )
  }



}
