package gatlingdemostore

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import sun.security.util.Length

import scala.language.postfixOps
import scala.util.Random

class DemostoreSimulation extends Simulation {

  val domain = "demostore.gatling.io"

  val httpProtocol = http
    .baseUrl("https://" + domain)

  def userCount: Int = getProperty("USERS", "5").toInt
  def rampDuration: Int = getProperty("RAMP_DURATION", "10").toInt
  def testDuration: Int = getProperty("DURATION", "60").toInt


  private def getProperty(propertyName: String, defaultValue: String) = {
    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }

  val categoryFeeder = csv("csv/gatlingdemostore/categoryDetails.csv").random
  val jsonFeederProducts = jsonFile("json/gatlingdemostore/productDetails.json").random
  val loginFeeder = csv("csv/gatlingdemostore/loginDetails.csv").circular

  val rnd = new Random()

  def randomString(lenght: Int): String = {
    rnd.alphanumeric.filter(_.isLetter).take(lenght).mkString
  }

  val initSession = exec(flushCookieJar)
    .exec(session => session.set("randomNumber", rnd.nextInt))
    .exec(session => session.set("customerLoggedIn", false))
    .exec(session => session.set("cartTotal", 0.00))
    .exec(addCookie(Cookie("sessionId", randomString(10)).withDomain(domain)))
//    .exec {session => println(session); session} // COMMENT THIS LINE WHEN RUN A REAL SCENARIO FOR LOAD TESTING

  object CsmPages {
    def homePage = {
      exec(
        http("Home Page")
          .get("/")
          .check(status.is(200))
          .check(regex("<title>Gatling Demo-Store</title>").exists)
          .check(css("#_csrf", "content").saveAs("csrfValue"))
      )
    }

    def aboutUs = {
      exec(
        http("About Us Page")
          .get("/about-us")
          .check(status.is(200))
          .check(substring("About Us"))
      )
    }
  }

  object Catalog {
    object Category {
      def categoriesPage = {
        feed(categoryFeeder)
          .exec(
            http("Load Categories Page - ${categoryName}")
              .get("/category/${categorySlug}")
              .check(status.is(200))
          )
      }
    }

    object Product {
      def loadProductPage = {
        feed(jsonFeederProducts)
          .exec(
            http("Load Product Page - ${name}")
              .get("/product/${slug}")
              .check(status.is(200))
              .check(css("#ProductDescription").is("${description}"))

          )
      }

      def addProductToCart = {
        exec(loadProductPage)
          .exec(
            http("Add Product to Cart")
              .get("/cart/add/${id}")
              .check(status.is(200))
              .check(substring("items in your cart"))
          )
          .exec(session => {
            val currentCartTotal = session("cartTotal").as[Double]
            val itemPrice = session("price").as[Double]
            session.set("cartTotal", (currentCartTotal + itemPrice))
            })
      }
    }
  }

  object Customer {
    def loginPage = {
      feed(loginFeeder)
        .exec { session => println(session); session} // COMMENT THIS LINE WHEN RUN A REAL SCENARIO FOR LOAD TESTING, RESULT SHOULD BE CUSTOMERLOGGEDIN = FALSE
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

  object Checkout {
    def viewCart = {
      doIf(session => !session("customerLoggedIn").as[Boolean]){
        exec(Customer.loginPage)
      }
      .exec(
        http("View Cart")
          .get("/cart/view")
          .check(status.is(200))
//          .check(css("grandTotal").is("$$${cartTotal}"))
      )
    }

    def completeCheckout = {
      exec(
        http("Checkout Cart")
          .get("/cart/checkout")
          .check(status.is(200))
          .check(substring("Thanks for your order! See you soon!"))
      )
    }
  }


  val scn = scenario("DemostoreSimulation")
    .exec(initSession)
    .exec(CsmPages.homePage)
    .pause(2)
    .exec(CsmPages.aboutUs)
    .pause(2)
    .exec(Catalog.Category.categoriesPage)
    .pause(2)
    .exec(Catalog.Product.addProductToCart)
    .pause(2)
    .exec(Checkout.viewCart)
    .pause(2)
    .exec(Checkout.completeCheckout)

  //USER JOURNEYS
  object UserJourneys {
    def minPause = 100 milliseconds
      def maxPause = 500 milliseconds

    def browseStore = {
      exec(initSession)
      .exec(CsmPages.homePage)
        .pause(maxPause)
        .exec(CsmPages.aboutUs)
        .pause(minPause, maxPause)
        .repeat(5){
          exec(Catalog.Category.categoriesPage)
            .pause(minPause, maxPause)
            .exec(Catalog.Product.loadProductPage)
        }
    }
    def abandonCard = {
      exec(initSession)
        .exec(CsmPages.homePage)
        .pause(maxPause)
        .exec(Catalog.Category.categoriesPage)
        .pause(minPause, maxPause)
        .exec(Catalog.Product.loadProductPage)
        .pause(minPause, maxPause)
        .exec(Catalog.Product.addProductToCart)
    }

    def completePurchase = {
      exec(initSession)
        .exec(CsmPages.homePage)
        .pause(maxPause)
        .exec(Catalog.Category.categoriesPage)
        .pause(minPause, maxPause)
        .exec(Catalog.Product.loadProductPage)
        .pause(minPause, maxPause)
        .exec(Catalog.Product.addProductToCart)
        .pause(minPause, maxPause)
        .exec(Checkout.viewCart)
        .pause(minPause, maxPause)
        .exec(Checkout.completeCheckout)

    }
  }

  object Scenarios {
    def default = scenario ("Default Load Test")
      .during(testDuration seconds){
        randomSwitch(
          75d -> exec(UserJourneys.browseStore),
          15d -> exec(UserJourneys.abandonCard),
          10d -> exec(UserJourneys.completePurchase)
        )
      }
    def highPurchase = scenario("High Purchase Load Test")
      .during(testDuration seconds){
        randomSwitch(
          25d -> exec(UserJourneys.browseStore),
          25d -> exec(UserJourneys.abandonCard),
          50d -> exec(UserJourneys.completePurchase)
        )
      }
  }

	//VALIDATE THE SCRIPT
//  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)

  //OPEN MODEL = where you control the arrival rate of users. Example: WebPages in general
//  setUp(
//    scn.inject(
//      atOnceUsers(3),
//      nothingFor(5.seconds),
//      rampUsers(10) during (20.seconds),
//      nothingFor(10.seconds),
//      constantUsersPerSec(1) during(20.seconds)
//    ).protocols(httpProtocol)
//  )

  //CLOSED MODEL = where you control the concurrent number of users. Example: Call Centers
//  setUp(
//    scn.inject(
//      constantConcurrentUsers(10) during (20.seconds),
//      rampConcurrentUsers(10) to (20) during (20.seconds)
//    )
//  ).protocols(httpProtocol)


//THROTTLING = used when you need to implement a test in terms of request per second and not in terms of concurrent users, similar to Throughput Shaping Timer in JMeter
//Throttling Key Points
//1. It only throttles to the upper RPS limits.
//2. Throttled traffic goes into a queue, be aware of the increase of memory it might cause.
//3.Not balanced by request type, for example if you have 10 transactions and want to run 10% of each of them, throttling may not do it correctly.
//    setUp(
//      scn.inject(
//        constantUsersPerSec(1) during(3.minutes)
//      )
//    ).protocols(httpProtocol).throttle(
//      reachRps(10) in (30.seconds),
//      holdFor(60.seconds),
//      jumpToRps(20),
//      holdFor(60.seconds)
//    ).maxDuration(3.minutes)

  //SETUP TO BE USED WITH USER JOURNEY
//  setUp(
//    Scenarios.default.inject(
//      rampUsers(userCount) during (rampDuration seconds)
//    )
//  ).protocols(httpProtocol)

  //SETUP TO EXECUTE SEQUENTIAL SCENARIOS
  setUp(
    Scenarios.default
      .inject(rampUsers(userCount) during (rampDuration.seconds)).protocols(httpProtocol),
    Scenarios.highPurchase
      .inject(rampUsers(5) during (10.seconds)).protocols(httpProtocol)
  )

}
