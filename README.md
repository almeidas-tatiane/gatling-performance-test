# Gatling
In this repository you will find some projects in Gatling developed during my learning curve.

### **Gatling Installation**
- Gatling Documentation: [https://grafana.com/docs/k6/latest/get-started/installation/](https://gatling.io/docs/gatling/tutorials/installation/)

### 📂**Project structure in IntelliJ**

- In the src/gatling/scala/project package, you'll find the objects and simulations;
- In the src/gatling/scala/project/objects package, you'll find the project class with methods that will be called in the simulations;
- In the src/gatling/scala/project/simulations package, you'll find different types of performance testing;
- In the src/gatling/resources/csv/project folder, you'll find csv files used for script parametrization;
- In the resources folder, you'll find the application.properties file that has all URLs used and it can be multiple environments as: TEST, PROD, DEV etc;
- In the utils package, you'll find the UrlProperties file, it's used to read the application.properties, with the urlProperties file you can pass the variable environment in the command line during script execution;


### **Steps to execute the simulation**
- In your terminal, type: .\gradlew gatlingRun-crocodiles.simulations.CrocodilesSimulation -Denv=test
- Press ENTER key;
- The value test used in the command line came from application.properties file, from the struture **test**.url.api
