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
  val createCrocodiles = csv(testDataDir + "newcrocodiles.csv").queue

  /* ----- HEADERS ----- */
  val sentHeadersAll = Map(
    "Content-Type" -> "application/json",
    "X-Dynatrace-Test" -> "VU=$VU;SI=GATLING;TSN=SEARCH-ALL-CROCODILES;LSN=$LSN;LTN=$LTN;PC=$PC"
  )

  /* ----- REQUESTS BODY ----- */
  val createNewCrocodileBody =
    """{
      |      "name": "${name}" ,
      |      "sex": "${sex}" ,
      |      "date_of_birth": "${date_of_birth}"
      |    }""".stripMargin


  /* ----- REQUESTS ----- */
  def getAllCrocodiles: ChainBuilder = {
      exec(
        http("Get All Crocodiles -> /public/crocodiles/")
          .get(UrlProperties.getUrlByKey("api") + "/public/crocodiles/")
          .headers(sentHeadersAll)
      )
  }

  def getCrocodilesbyID: ChainBuilder = {
    feed(searchCrocodiles)
      .exec(
        http("Get Crocodiles By ID -> /public/crocodiles/id")
          .get(UrlProperties.getUrlByKey("api") + "/public/crocodiles/${id}")
          .headers(sentHeadersAll)
      )
  }

  def createNewCrocodile: ChainBuilder = {
    feed(createCrocodiles)
      .exec(
        http("Create New Crocodiles -> /my/crocodiles/")
          .post(UrlProperties.getUrlByKey("api") + "/my/crocodiles/")
          .headers(sentHeadersAll)
          .body(StringBody(createNewCrocodileBody)).asJson
      )
  }



}
