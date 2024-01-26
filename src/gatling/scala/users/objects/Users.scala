package users.objects

import io.gatling.core.Predef.{StringBody, exec}
import io.gatling.http.Predef._
import utils.UrlProperties
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
object Users {

  /* ----- TEST DATA ----- */
//  private val testDataDir = "csv/users/"
//  val usersData = csv(testDataDir + "users.csv").queue

  /* ----- HEADERS ----- */
  val sentHeadersLogin = Map(
    "Content-Type" -> "application/json",
    "X-Dynatrace-Test" -> "VU=$VU;SI=GATLING;TSN=LOGIN;LSN=$LSN;LTN=$LTN;PC=$PC"
  )

  val sentHeadersUsers = Map(
    "Content-Type" -> "application/json",
    "X-Dynatrace-Test" -> "VU=$VU;SI=GATLING;TSN=CREATE USER;LSN=$LSN;LTN=$LTN;PC=$PC"
  )

  /* ----- REQUESTS BODY ----- */
  val createUserBody =
    """{
    |  "username": "test1210",
    |  "first_name": "test1210",
    |  "last_name": "test1210",
    |  "email": "test1210"@"test1210".com,
    |  "password": "test123"
    |  }""".stripMargin


  val loginBody =
    """{
      |  "username": "test1210",
      |  "password": "test123"
      |}""".stripMargin

  /* ----- REQUESTS ----- */

  def createNewUser: ChainBuilder = {
    exec(
      http("Create User -> /user/register/")
        .post(UrlProperties.getUrlByKey("api") + "/user/register/")
        .headers(sentHeadersUsers)
        .body(StringBody(createUserBody)).asJson

    )
  }

  def login: ChainBuilder = {
    exec(
      http("Login -> /auth/token/login/")
        .post(UrlProperties.getUrlByKey("api") + "/auth/token/login/")
        .headers(sentHeadersLogin)
        .body(StringBody(loginBody)).asJson
        .check(jsonPath("$.access").saveAs("access_token"))
    )
  }

}
