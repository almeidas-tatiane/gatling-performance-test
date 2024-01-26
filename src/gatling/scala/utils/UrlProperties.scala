package utils

import java.util.Properties
import scala.io.Source

object UrlProperties {
  private var properties : Properties = _

  private val propertiesFile = getClass.getResource("application.properties")
  if (propertiesFile != null){
    val source = Source.fromURL(propertiesFile)

    properties = new Properties()
    properties.load(source.bufferedReader())
  } else {
    // Handle the case where propertiesFile is null (e.g., log a message or throw an exception)
    throw new RuntimeException("Failed to load application.properties file.")
  }

  def urls(property: String): String = {
    properties.getProperty(System.getProperty("env") + ".url." + property)
  }

}
