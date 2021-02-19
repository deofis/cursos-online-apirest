FROM adoptopenjdk:11-jre-hotspot
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} deofis-tienda-apirest.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/deofis-tienda-apirest.jar"]