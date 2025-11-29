# Multi-stage build for Render
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Make mvnw executable
RUN chmod +x ./mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built jar
COPY --from=build /app/target/yaAllah-0.0.1-SNAPSHOT.jar app.jar

# Create uploads directory
RUN mkdir -p uploads

EXPOSE 8080

# Set environment variables for production
ENV SPRING_PROFILES_ACTIVE=prod

CMD ["java", "-jar", "app.jar"]