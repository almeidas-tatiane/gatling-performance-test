package users.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import users.objects.Users


class UsersSimulation extends Simulation{
  /* ----- VARIABLES ----- */
  val httpConfiguration = http


  def scnUser = scenario("User")
    .repeat(1) {
      exec(Users.createNewUser)
      .exec(Users.login)
    }



  /* ----- VALIDATE TEST ----- */
  setUp(scnUser.inject(
    atOnceUsers(1))).protocols(httpConfiguration)


}
