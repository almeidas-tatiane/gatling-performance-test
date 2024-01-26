package utils

import java.util.Properties
import scala.io.Source

object UrlProperties {
  private var properties : Properties = null

//  private val propertiesFile = getClass.getResource("application.properties")

  def urls(property: String): String = {
    val propertiesFile = getClass.getResource("../../resources/application.properties")

    if (propertiesFile != null) {
      val source = Source.fromURL(propertiesFile)

      properties = new Properties()
      properties.load(source.bufferedReader())
    }

    properties.getProperty(System.getProperty("env") + ".url." + property)
  }

}
