FROM eclipse-temurin:19
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} cloudgateway.jar
ENTRYPOINT ["java","-jar","/cloudgateway.jar"]
EXPOSE 9090
