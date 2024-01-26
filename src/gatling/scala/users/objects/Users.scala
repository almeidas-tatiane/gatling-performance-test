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

  /* ----- REQUESTS ----- */

  def createNewUser: ChainBuilder = {
//    feed(usersData)
    exec(
      http("Create User -> /user/register/")
        .post(UrlProperties.getUrlByKey("api") + "/user/register/")
        .headers(sentHeadersUsers)
        .formParam("username", "test524")
        .formParam("first_name", "Test524")
        .formParam("last_name", "Crocodile524")
        .formParam("email", "test524.crocodile@test.com")
        .formParam("password", "test123")
    )
  }

  def login: ChainBuilder = {
//    feed(usersData)
    exec(
      http("Login -> /auth/token/login/")
        .post(UrlProperties.getUrlByKey("api") + "/auth/token/login/")
        .headers(sentHeadersLogin)
        .formParam("username", "test524")
        .formParam("password", "test123")
        .check(jsonPath("$.access").saveAs("access_token"))
    )
  }

}
