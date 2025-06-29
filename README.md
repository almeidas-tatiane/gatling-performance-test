# Gatling
In this repository you will find some projects in Gatling developed during my learning curve.

### **Gatling Installation**
- Gatling Documentation: https://gatling.io/docs/gatling/tutorials/installation/
  

### 📂**Project structure in IntelliJ with Gradle**

- In the package crocodiles, you can check an example that I've applied what I've learned on my career.
  
- In the src/gatling/scala/project package, you'll find the objects and simulations;
- In the src/gatling/scala/project/objects package, you'll find the project class with methods that will be called in the simulations;
- In the src/gatling/scala/project/simulations package, you'll find different types of performance testing;
- In the src/gatling/resources/csv/project folder, you'll find csv files used for script parametrization;
- In the resources folder, you'll find the application.properties file that has all URLs used and it can be used in multiple environments as: TEST, PROD, DEV etc;
- In the utils package, you'll find the UrlProperties file, it's used to read the application.properties, with the urlProperties file you can pass the variable environment in the command line during script execution;

- The package gatlingdemostore, it's regarding the Gatling Academy training.


### **Steps to execute the CrococodilesSimulation**
- In your terminal, type: .\gradlew gatlingRun-crocodiles.simulations.CrocodilesSimulation -Denv=test
- Press ENTER key;
- The value test used in the command line came from application.properties file, from the struture **test**.url.api

### **Steps to execute the DemostoreSimulation**
- In your terminal, type: .\gradlew gatlingRun-gatlingdemostore.simulations.DemostoreSimulation
- Press ENTER key;

## 🙏 Usage and Credit Notice
To help strengthen our community, I would appreciate it if you could mention my [GitHub](https://github.com/almeidas-tatiane) and [LinkedIn](https://www.linkedin.com/in/tatianealmeida/) when using this material.
