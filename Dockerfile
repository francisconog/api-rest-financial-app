FROM openjdk:8-alpine

COPY target/uberjar/fin-app.jar /fin-app/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/fin-app/app.jar"]
