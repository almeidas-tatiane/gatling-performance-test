package utils

import java.io.FileInputStream
import java.io.IOException
import java.util.Properties


object UrlProperties {
  private var properties: Properties = null

  // Function to get URLs
  def getUrls: Properties = properties

  // Function to get specific URL by property key
  def getUrlByKey(property: String): String = {
    val env = System.getProperty("env", "default") // Use 'default' if 'env' system property is not defined.

    // Build the property key
    val key = env + ".url." + property
    // Getting the property value
    properties.getProperty(key)
  }

  def main(args: Array[String]): Unit = {
    // Example usage
    System.out.println("All URLs: " + getUrls)
    System.out.println("Specific URL: " + getUrlByKey("api"))
  }

  try
    // Load properties from the file
    try {
      val input = new FileInputStream("src/gatling/resources/application.properties")
      try {
        properties = new Properties
        properties.load(input)
      } catch {
        case e: IOException =>
          throw new RuntimeException("Failed to load application.properties file.", e)
      } finally if (input != null) input.close()
    }

}
