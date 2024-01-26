package users.objects

import io.gatling.core.Predef.{StringBody, exec}
import io.gatling.http.Predef._
import utils.UrlProperties
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
object Users {

  /* ----- TEST DATA ----- */
  private val testDataDir = "csv/users/"
  val usersData = csv(testDataDir + "users.csv").queue

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
    feed(usersData)
    .exec(
      http("Create User -> /user/register/")
        .post(UrlProperties.getUrlByKey("api") + "/user/register/")
        .headers(sentHeadersUsers)
        .formParam("username", "${username}")
        .formParam("first_name", "${first_name}")
        .formParam("last_name", "${last_name}")
        .formParam("email", "${email}")
        .formParam("password", "${password}")
    )
  }

  def login: ChainBuilder = {
    feed(usersData)
    .exec(
      http("Login -> /auth/token/login/")
        .post(UrlProperties.getUrlByKey("api") + "/auth/token/login/")
        .headers(sentHeadersLogin)
        .formParam("username", "${username}")
        .formParam("password", "${password}")
        .check(jsonPath("$.access").saveAs("access_token"))
    )
  }

}
