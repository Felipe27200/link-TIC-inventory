# ==============================
# 1 Build Stage
# ==============================
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy the parent POM and module POMs for caching
COPY pom.xml ./
COPY product-commons/pom.xml product-commons/
COPY product-service/pom.xml product-service/

# Pre-download dependencies (this step is cached)
RUN mvn dependency:go-offline -B

# Copy all sources
COPY . .

# Package only the product-service module (skip tests)
RUN mvn clean package -pl product-service -am -DskipTests

# ==============================
# 2 Runtime Stage
# ==============================
FROM eclipse-temurin:21-jre-jammy AS runtime

WORKDIR /app

# Copy the generated jar from builder stage
COPY --from=builder /app/product-service/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
