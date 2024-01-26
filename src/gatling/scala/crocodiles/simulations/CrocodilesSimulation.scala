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

  def scnCrocodiles = scenario("Crocodiles")
    .repeat(5) {
      exec(Crocodiles.getAllCrocodiles)
      .exec(Crocodiles.getCrocodilesbyID)
        .exec(Crocodiles.createNewCrocodile)
    }



  /* ----- VALIDATE TEST ----- */
  setUp(scnCrocodiles.inject(
    atOnceUsers(1))).protocols(httpConfiguration)

  /* ----- LOAD TEST ----- */
//  setUp(
//    scnCrocodiles.inject(
//      rampUsers(users).during(rampDuration),
//      nothingFor(holdDuration),
//    ).protocols(httpConfiguration)).maxDuration(maxDuration)


}
