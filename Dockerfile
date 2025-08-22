FROM maven:3.9-eclipse-temurin-24 AS build
WORKDIR /workspace

# Copy only what we need for dependency resolution first to leverage Docker cache
COPY pom.xml ./
COPY src ./src

# Build the application JAR (skip tests for CI speed; CI can run tests separately)
RUN mvn -B -DskipTests package

FROM eclipse-temurin:24-jre
WORKDIR /app

# Copy built jar from the build stage
COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]


