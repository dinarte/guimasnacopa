# ── Build stage ──────────────────────────────────────────────────────────────
FROM eclipse-temurin:11-jdk-alpine AS build

WORKDIR /app

# Copy Gradle wrapper and config files first (layer cache for dependencies)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

# Remove the Windows-only JDK path so Gradle uses the container's JDK
RUN sed -i '/org.gradle.java.home/d' gradle.properties

RUN chmod +x ./gradlew

# Download dependencies (cached layer if source hasn't changed)
RUN ./gradlew dependencies --no-daemon -q || true

# Copy source and build
COPY src src
RUN ./gradlew bootWar --no-daemon -x test

# ── Runtime stage ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:11-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.war app.war

# Render injects PORT at runtime; use a shell form so ${PORT} is expanded
EXPOSE 8080

# Use shell form (not exec form) so the $PORT variable is expanded at startup
ENTRYPOINT ["sh", "-c", \
  "java -Djava.security.egd=file:/dev/./urandom -jar app.war --server.port=${PORT:-8080}"]
