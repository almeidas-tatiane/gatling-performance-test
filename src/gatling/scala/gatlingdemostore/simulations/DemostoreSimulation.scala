package gatlingdemostore.simulations

import gatlingdemostore.objects.CsmPages
import gatlingdemostore.objects.Catalog
import gatlingdemostore.objects.Checkout

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
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


  val rnd = new Random()


  def randomString(lenght: Int): String = {
    rnd.alphanumeric.filter(_.isLetter).take(lenght).mkString
  }

  before {
    println(s"Running test with ${userCount} users")
    println(s"Ramping users over ${rampDuration} seconds")
    println(s"Total test duration ${testDuration} seconds")
  }

  after {
    println("Test Completed")
  }

  val initSession = exec(flushCookieJar)
    .exec(session => session.set("randomNumber", rnd.nextInt))
    .exec(session => session.set("customerLoggedIn", false))
    .exec(session => session.set("cartTotal", 0.00))
    .exec(addCookie(Cookie("sessionId", randomString(10)).withDomain(domain)))
//    .exec {session => println(session); session} // COMMENT THIS LINE WHEN RUN A REAL SCENARIO FOR LOAD TESTING


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

  //SETUP TO EXECUTE SEQUENTIAL SCENARIOS -> the key word is .andThen
//  setUp(
//    Scenarios.default
//      .inject(rampUsers(userCount) during (rampDuration.seconds)).protocols(httpProtocol)
//      .andThen(Scenarios.highPurchase.inject(rampUsers(5) during (10.seconds)).protocols(httpProtocol))
//  )

  //SETUP TO EXECUTE PARALLEL SCENARIOS -> in this case we removed the .andThen
  setUp(
    Scenarios.default
      .inject(rampUsers(userCount) during (rampDuration.seconds)).protocols(httpProtocol),
    Scenarios.highPurchase
      .inject(rampUsers(5) during (10.seconds)).protocols(httpProtocol)
  )

}
