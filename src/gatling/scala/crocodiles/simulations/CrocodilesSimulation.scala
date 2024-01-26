package crocodiles.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import crocodiles.objects.Crocodiles
import scala.concurrent.duration.DurationInt

class CrocodilesSimulation extends Simulation {

  val httpConfiguration = http
    .acceptHeader("application/json")

  def scnGetAllCrocodiles = scenario("Get All Crocodiles")
    .repeat(5) {
      exec(Crocodiles.getAllCrocodiles)
    }

  setUp(scnGetAllCrocodiles.inject(
    atOnceUsers(1))).protocols(httpConfiguration)

}
