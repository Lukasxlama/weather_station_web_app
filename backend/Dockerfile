# ---------- Build Stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline -B
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---------- Run Stage ----------
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN useradd --system --create-home --shell /usr/sbin/nologin appuser \
    && mkdir -p /var/lib/weather \
    && chown -R appuser:appuser /var/lib/weather /app
COPY --from=build --chown=appuser:appuser /app/target/*.jar /app/app.jar
USER appuser
EXPOSE 5000
ENTRYPOINT ["java", "-jar", "app.jar"]