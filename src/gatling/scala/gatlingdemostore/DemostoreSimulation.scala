package gatlingdemostore

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class DemostoreSimulation extends Simulation {

  private val httpProtocol = http
    .baseUrl("https://demostore.gatling.io")
    .inferHtmlResources(AllowList(), DenyList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*\.svg""", """.*detectportal\.firefox\.com.*"""))
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
  
  private val headers_0 = Map(
  		"accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
  		"accept-encoding" -> "gzip, deflate, br",
  		"accept-language" -> "en-US,en;q=0.9,pt-BR;q=0.8,pt;q=0.7",
  		"pragma" -> "no-cache",
  		"sec-ch-ua" -> """Not_A Brand";v="8", "Chromium";v="120", "Google Chrome";v="120""",
  		"sec-ch-ua-mobile" -> "?0",
  		"sec-ch-ua-platform" -> "Windows",
  		"sec-fetch-dest" -> "document",
  		"sec-fetch-mode" -> "navigate",
  		"sec-fetch-site" -> "same-origin",
  		"sec-fetch-user" -> "?1",
  		"upgrade-insecure-requests" -> "1"
  )
  
  private val headers_4 = Map(
  		"accept" -> "*/*",
  		"accept-encoding" -> "gzip, deflate, br",
  		"accept-language" -> "en-US,en;q=0.9,pt-BR;q=0.8,pt;q=0.7",
  		"pragma" -> "no-cache",
  		"sec-ch-ua" -> """Not_A Brand";v="8", "Chromium";v="120", "Google Chrome";v="120""",
  		"sec-ch-ua-mobile" -> "?0",
  		"sec-ch-ua-platform" -> "Windows",
  		"sec-fetch-dest" -> "empty",
  		"sec-fetch-mode" -> "cors",
  		"sec-fetch-site" -> "same-origin",
  		"x-requested-with" -> "XMLHttpRequest"
  )
  
  private val headers_6 = Map(
  		"Upgrade-Insecure-Requests" -> "1",
  		"sec-ch-ua" -> """Not_A Brand";v="8", "Chromium";v="120", "Google Chrome";v="120""",
  		"sec-ch-ua-mobile" -> "?0",
  		"sec-ch-ua-platform" -> "Windows"
  )
  
  private val headers_7 = Map(
  		"accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
  		"accept-encoding" -> "gzip, deflate, br",
  		"accept-language" -> "en-US,en;q=0.9,pt-BR;q=0.8,pt;q=0.7",
  		"origin" -> "https://demostore.gatling.io",
  		"pragma" -> "no-cache",
  		"sec-ch-ua" -> """Not_A Brand";v="8", "Chromium";v="120", "Google Chrome";v="120""",
  		"sec-ch-ua-mobile" -> "?0",
  		"sec-ch-ua-platform" -> "Windows",
  		"sec-fetch-dest" -> "document",
  		"sec-fetch-mode" -> "navigate",
  		"sec-fetch-site" -> "same-origin",
  		"sec-fetch-user" -> "?1",
  		"upgrade-insecure-requests" -> "1"
  )
  
  private val headers_8 = Map(
  		"Content-Type" -> "application/x-www-form-urlencoded",
  		"Origin" -> "https://demostore.gatling.io",
  		"Upgrade-Insecure-Requests" -> "1",
  		"sec-ch-ua" -> """Not_A Brand";v="8", "Chromium";v="120", "Google Chrome";v="120""",
  		"sec-ch-ua-mobile" -> "?0",
  		"sec-ch-ua-platform" -> "Windows"
  )


  private val scn = scenario("DemostoreSimulation")
    .exec(
      http("request_0")
        .get("/")
        .headers(headers_0)
    )
    .pause(4)
    .exec(
      http("request_1")
        .get("/about-us")
        .headers(headers_0)
    )
    .pause(2)
    .exec(
      http("request_2")
        .get("/category/all")
        .headers(headers_0)
    )
    .pause(2)
    .exec(
      http("request_3")
        .get("/product/black-and-red-glasses")
        .headers(headers_0)
    )
    .pause(2)
    .exec(
      http("request_4")
        .get("/cart/add/19")
        .headers(headers_4)
    )
    .pause(1)
    .exec(
      http("request_5")
        .get("/cart/view")
        .headers(headers_0)
        .resources(
          http("request_6")
            .get("/login")
            .headers(headers_6)
        )
    )
    .pause(7)
    .exec(
      http("request_7")
        .post("/login")
        .headers(headers_7)
        .formParam("_csrf", "8fc9f889-87c3-4a68-8e82-3dec9a9dcd93")
        .formParam("username", "admin")
        .formParam("password", "admin")
        .resources(
          http("request_8")
            .get("/")
            .headers(headers_8)
        )
    )
    .pause(6)
    .exec(
      http("request_9")
        .get("/cart/view")
        .headers(headers_0)
    )
    .pause(5)
    .exec(
      http("request_10")
        .get("/cart/checkout")
        .headers(headers_0)
        .resources(
          http("request_11")
            .get("/cart/checkoutConfirmation")
            .headers(headers_6)
        )
    )

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
