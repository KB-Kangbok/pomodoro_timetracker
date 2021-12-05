1. Make sure the following are installed
* MySQL 5.7
* Java 8
* Maven
* Spring Boot 2.5.5

2. Setup MySQL Database
* Use SQL commands in DB/dbinit.sql

3. Start MySQL Server

Steps to run Spring boot server :
1. Compile and Build
* `mvn clean install`

2. Start the server
* `mvn spring-boot:run`

Steps to run tests:

* `./gradlew clean build`

(or)

* `./gradlew clean BackendTestsBackend2`


Configuring URL and Ports : 

1. For configuring the port on which the REST server is started
* Changes to the port in which the REST server is started can be made by changing the `server.port` value in the file `src/main/resources/application.properties`. (This value is set to 8080 by default) 

2. For configuring the port on which the tests are running
* The base URL, hostname and port where the test is executing can be configured by changing the values `TEST_BASE_URL`, 
  `TEST_HOST_NAME`, `TEST_BASE_PORT` in the `test.properties` file.
* By default, the host name is set to `localhost` and port is set to `8080`.
* The number of max connections per route can be configured by changing the value `MAX_CONN` (Set to 20 by default). 