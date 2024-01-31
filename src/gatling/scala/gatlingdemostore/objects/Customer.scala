package gatlingdemostore.objects

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Customer {
  val loginFeeder = csv("csv/gatlingdemostore/loginDetails.csv").circular

  def loginPage = {
    feed(loginFeeder)
      .exec { session => println(session); session } // COMMENT THIS LINE WHEN RUN A REAL SCENARIO FOR LOAD TESTING, RESULT SHOULD BE CUSTOMERLOGGEDIN = FALSE
      .exec(
        http("Login Page")
          .post("/login")
          .formParam("_csrf", "${csrfValue}")
          .formParam("username", "${username}")
          .formParam("password", "${password}")
          .check(status.is(200))
      )
      .exec(session => session.set("customerLoggedIn", true))
    //        .exec { session => println(session); session} // COMMENT THIS LINE WHEN RUN A REAL SCENARIO FOR LOAD TESTING, RESULT SHOULD BE CUSTOMERLOGGEDIN = TRUE
  }
}
