package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import java.util.concurrent.ThreadLocalRandom

/**
 * This sample is based on our official tutorials:
 *
 *   - [[https://gatling.io/docs/gatling/tutorials/quickstart Gatling quickstart tutorial]]
 *   - [[https://gatling.io/docs/gatling/tutorials/advanced Gatling advanced tutorial]]
 */
class ComputerDatabaseSimulation extends Simulation {

  val httpProtocol =
    http.baseUrl("https://computer-database.gatling.io")
      .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
      .acceptLanguageHeader("en-US,en;q=0.5")
      .acceptEncodingHeader("gzip, deflate")
      .userAgentHeader(
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/119.0"
      )

  val searchFeeder = csv("csv/computerdatabase/search.csv").random
  val computerFeeder = csv("csv/computerdatabase/computer.csv").circular

  val search =
    exec(
      http("Home")
        .get("/")
    )
      .pause(1)
      .feed(searchFeeder)
      .exec(
        http("Search")
          .get("/computers?f=#{searchCriterion}")
          .check(
            css("a:contains('#{searchComputerName}')", "href").saveAs("computerUrl")
          )
      )
      .pause(1)
      .exec(
        http("Select")
          .get("#{computerUrl}")
          .check(status.is(200))
      )
      .pause(1)

  // repeat is a loop resolved at RUNTIME
  val browse =
  // Note how we force the counter name, so we can reuse it
    repeat(4, "i") {
      exec(
        http("Page #{i}").get("/computers?p=#{i}")
      ).pause(1)
    }

  // Note we should be using a feeder here
  // let's demonstrate how we can retry: let's make the request fail randomly and retry a given
  // number of times

  val edit =
  // let's try at max 2 times
    tryMax(2) {
      exec(
        http("Form")
          .get("/computers/new")
      )
        .pause(1)
        .feed(computerFeeder)
        .exec(
          http("Post")
            .post("/computers")
            .formParam("name", "${computerName}")
            .formParam("introduced", "${introduced}")
            .formParam("discontinued", "${discontinued}")
            .formParam("company", "${companyId}")
            .check(
              status.is { session =>
                // we do a check on a condition that's been customized with
                // a lambda. It will be evaluated every time a user executes
                // the request
                200 + ThreadLocalRandom.current().nextInt(2)
              }
            )
        )
    }
      // if the chain didn't finally succeed, have the user exit the whole scenario
      .exitHereIfFailed



  val users = scenario("Users").exec(search, browse)
  val admins = scenario("Admins").exec(search, browse, edit)

  setUp(admins.inject(atOnceUsers(5)),
    users.inject(
      nothingFor(5), // OPEN MODEL
      atOnceUsers(1),
      rampUsers(5) during (10),
      constantUsersPerSec(20) during (20)
    ))

    .protocols(httpProtocol)
}
