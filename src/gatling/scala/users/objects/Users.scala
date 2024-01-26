package users.objects

import io.gatling.core.Predef.{StringBody, exec}
import io.gatling.http.Predef._
import utils.UrlProperties
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
object Users {

  /* ----- TEST DATA ----- */
  private val testDataDir = "csv/users/"
  val loginData = csv(testDataDir + "login.csv").queue

  /* ----- HEADERS ----- */
  val sentHeadersLogin = Map(
    "Content-Type" -> "application/json",
    "X-Dynatrace-Test" -> "VU=$VU;SI=GATLING;TSN=LOGIN;LSN=$LSN;LTN=$LTN;PC=$PC"
  )

  /* ----- REQUESTS BODY ----- */
  val LoginBody =
    """{
      |      "username": "${username}" ,
      |      "password": "${password}"
      |    }""".stripMargin

  /* ----- REQUESTS ----- */
  def login: ChainBuilder = {
    feed(loginData)
    .exec(
      http("Login -> /auth/token/login/")
        .post(UrlProperties.getUrlByKey("api") + "/auth/token/login/")
        .headers(sentHeadersLogin)
        .body(StringBody(LoginBody)).asJson
        .check(jsonPath("$.access").saveAs("access_token"))
    )
  }

}
