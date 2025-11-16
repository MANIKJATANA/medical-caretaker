# Multi-stage Dockerfile for medical-caretaker
# Builds the application with Maven and runs the Spring Boot jar

ARG MAVEN_IMAGE=maven:3.9.6-eclipse-temurin-21
ARG RUNTIME_IMAGE=eclipse-temurin:21-jre

FROM ${MAVEN_IMAGE} AS builder
WORKDIR /workspace

# copy maven configs first for efficient caching
COPY pom.xml mvnw ./
COPY .mvn .mvn

# copy source
COPY src ./src

# build the application (skip tests for speed; change if you want tests)
RUN mvn -B -DskipTests package

FROM ${RUNTIME_IMAGE} AS runtime
WORKDIR /app

# copy the fat jar produced by spring-boot-maven-plugin
COPY --from=builder /workspace/target/*.jar app.jar

EXPOSE 8080

# default JVM options can be overridden at runtime with JAVA_OPTS
ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
