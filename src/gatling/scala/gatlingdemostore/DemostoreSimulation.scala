package gatlingdemostore

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class DemostoreSimulation extends Simulation {

  val domain = "demostore.gatling.io"

  val httpProtocol = http
    .baseUrl("https://" + domain)

  val categoryFeeder = csv("csv/gatlingdemostore/categoryDetails.csv").random
  val jsonFeederProducts = jsonFile("json/gatlingdemostore/productDetails.json").random

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
    }



    def loadProductPage = {
      exec(
        http("Load Product Page")
          .get("/product/black-and-red-glasses")
      )
    }

    def addProductToCart ={
      exec(
        http("Add Product to Cart")
          .get("/cart/add/19")
      )
    }

    def viewCart = {
      exec(
        http("View Cart")
          .get("/cart/view")
      )
    }

    def loginPage = {
      exec(
        http("Login Page")
          .post("/login")
          .formParam("_csrf", "${csrfValue}")
          .formParam("username", "admin")
          .formParam("password", "admin")
      )
    }

    def checkout = {
      exec(
        http("Checkout")
          .get("/cart/checkout")
      )
    }


  val scn = scenario("DemostoreSimulation")
    .exec(CsmPages.homePage)
    .pause(2)
    .exec(CsmPages.aboutUs)
    .pause(2)
    .exec(Catalog.Category.categoriesPage)
    .pause(2)
    .exec(CsmPages.loadProductPage)
    .pause(2)
    .exec(CsmPages.addProductToCart)
    .pause(2)
    .exec(CsmPages.viewCart)
    .pause(2)
    .exec(CsmPages.loginPage)
    .pause(2)
    .exec(CsmPages.checkout)

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
