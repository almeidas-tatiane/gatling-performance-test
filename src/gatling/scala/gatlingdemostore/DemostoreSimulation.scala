package gatlingdemostore

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class DemostoreSimulation extends Simulation {

  val domain = "demostore.gatling.io"

  val httpProtocol = http
    .baseUrl("https://" + domain)

  val scn = scenario("DemostoreSimulation")
    .exec(
      http("Home Page")
        .get("/")
        .check(regex("<title>Gatling Demo-Store</title>").exists)
        .check(css("#_csrf", "content").saveAs("csrfValue"))
    )
    .pause(2)
    .exec(
      http("About Us Page")
        .get("/about-us")
    )
    .pause(2)
    .exec(
      http("Categories Page")
        .get("/category/all")
    )
    .pause(2)
    .exec(
      http("Load Product Page")
        .get("/product/black-and-red-glasses")
     )
    .pause(2)
    .exec(
      http("Add Product to Cart")
        .get("/cart/add/19")
    )
    .pause(2)
    .exec(
      http("View Cart")
        .get("/cart/view")
//         .resources(
//          http("request_6")
//            .get("/login")
//        )
    )
    .pause(2)
    .exec(
      http("Login Page")
        .post("/login")
        .formParam("_csrf", "${csrfValue}")
        .formParam("username", "admin")
        .formParam("password", "admin")
//        .resources(
//          http("request_8")
//            .get("/")
//        )
    )
//    .pause(6)
//    .exec(
//      http("request_9")
//        .get("/cart/view")
//    )
    .pause(2)
    .exec(
      http("Checkout")
        .get("/cart/checkout")
//        .resources(
//          http("request_11")
//            .get("/cart/checkoutConfirmation")
//        )
    )

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
