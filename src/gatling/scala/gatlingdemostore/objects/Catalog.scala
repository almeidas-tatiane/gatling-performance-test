package gatlingdemostore.objects

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Catalog {
  val categoryFeeder = csv("csv/gatlingdemostore/categoryDetails.csv").random
  val jsonFeederProducts = jsonFile("json/gatlingdemostore/productDetails.json").random

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
