package users.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import users.objects.Users


class UsersSimulation extends Simulation{
  /* ----- VARIABLES ----- */
  val httpConfiguration = http


  def scnLogin = scenario("Login")
    .repeat(1) {
      exec(Users.login)
    }



  /* ----- VALIDATE TEST ----- */
  setUp(scnLogin.inject(
    atOnceUsers(1))).protocols(httpConfiguration)


}
