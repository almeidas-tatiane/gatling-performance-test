package gatlingdemostore

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import sun.security.util.Length

import scala.util.Random

class DemostoreSimulation extends Simulation {

  val domain = "demostore.gatling.io"

  val httpProtocol = http
    .baseUrl("https://" + domain)

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
          .check(css("#grandTotal").is("$$${cartTotal}"))
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

	//VALIDATE THE SCRIPT
  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)


}
