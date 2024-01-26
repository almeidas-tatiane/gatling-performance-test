package crocodiles.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import crocodiles.objects.Crocodiles
import scala.concurrent.duration.DurationInt

class CrocodilesSimulation extends Simulation {

  /* ----- VARIABLES ----- */
  val httpConfiguration = http
  val users = System.getProperty("users", "15").toInt
  val rampDuration = System.getProperty("rampDuration", "120").toInt.seconds
  val holdDuration = System.getProperty("holdDuration", "10").toInt.minutes
  val maxDuration = System.getProperty("maxDuration", "130").toInt.minutes

  def scnGetAllCrocodiles = scenario("Get All Crocodiles")
      .exec(Crocodiles.getAllCrocodiles)

  /* ----- VALIDATE TEST ----- */
//  setUp(scnGetAllCrocodiles.inject(
//    atOnceUsers(1))).protocols(httpConfiguration)

  /* ----- LOAD TEST ----- */
  setUp(
    scnGetAllCrocodiles.inject(
      rampUsers(users).during(rampDuration),
      nothingFor(holdDuration),
      rampUsers(users).during(rampDuration),
      nothingFor(holdDuration),
      rampUsers(users).during(rampDuration),
      nothingFor(holdDuration),
      rampUsers(users).during(rampDuration),
      nothingFor(holdDuration * 2),
    ).protocols(httpConfiguration)).maxDuration(maxDuration))


}
