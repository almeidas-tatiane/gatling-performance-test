package utils

import java.util.Properties
import scala.io.Source

object UrlProperties {
  private var properties : Properties = null

  private val propertiesFile = getClass.getResource("application.properties")
  if (propertiesFile != null){
    val source = Source.fromURL(propertiesFile)

    properties = new Properties()
    properties.load(source.bufferedReader())
  }

  def urls(property: String): String = {
    properties.getProperty(System.getProperty("env") + ".url." + property)
  }

}
