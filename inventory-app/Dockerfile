FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY inventory-service/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
