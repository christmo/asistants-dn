# CHATBOT-FLOW-ALEXA-BACKEND

### Reference Documentation
For further reference, please consider the following sections:
* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/gradle-plugin/reference/html/)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/reference/htmlsingle/#configuration-metadata-annotation-processor)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/reference/htmlsingle/#using-boot-devtools)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/reference/htmlsingle/#boot-features-jpa-and-spring-data)
* [Spring Security](https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/reference/htmlsingle/#boot-features-security)
* [Spring Batch](https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/reference/htmlsingle/#howto-batch-applications)
* [Lombok](https://projectlombok.org/features/all)

### Guides
The following guides illustrate how to use some features concretely:
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Creating a Batch Service](https://spring.io/guides/gs/batch-processing/)

### Additional Links
These additional references should also help you:
* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

## Lombok
### Intellij
#### Enabling Annotation processing
Annotation processing isn't enabled by default, though. So, the first thing for us to do is to enable annotation processing in our project.

We need to go to the Preferences | Build, Execution, Deployment | Compiler | Annotation Processors and make sure of the following:
* Enable annotation processing box is checked
* Obtain processors from project classpath option is selected
#### Install
We need to go to the Preferences | Plugins, open the Marketplace tab, type lombok and choose Lombok Plugin by Michail Plushnikov

After the installation, click the Restart IDE button.

Build Project using the IDE to load all annotated methods and avoid references errors.

### Eclipse IDE
If we're using Eclipse IDE, we need to get the Lombok jar first. The latest version is located on [Maven Central](https://search.maven.org/search?q=g:org.projectlombok%20AND%20a:lombok&core=gav). 
For our example, we're using [lombok-1.18.10.jar](https://search.maven.org/remotecontent?filepath=org/projectlombok/lombok/1.18.10/lombok-1.18.10.jar).

Next, we can run the jar via java -jar command and an installer UI will open. This tries to automatically detect all available Eclipse installations, but it's also possible to specify the location manually.

Once we've selected the installations, then we press the Install/Update button.

If the installation is successful, we can exit the installer.

After installing the plugin, we need to restart the IDE and ensure that Lombok is correctly configured. We can check this in the About dialog.

##Running the project
* Execute  `$./gradlew build -P local -xtest` for local building purposes
* Execute `$  java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8003 -jar -Dspring.profiles.active=local build/libs/chatbot-flow-alexa-backend-0.0.1-SNAPSHOT.war` to run the project.
* Create a debug configurations and run it for debugging purposes.