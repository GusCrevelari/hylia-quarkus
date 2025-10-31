# ---- Stage 1: build ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache deps
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Copy sources and build
COPY src ./src
RUN mvn -q clean package -DskipTests

# ---- Stage 2: runtime ----
FROM eclipse-temurin:21-jre
WORKDIR /work

COPY --from=build /app/target/quarkus-app/lib/ /work/lib/
COPY --from=build /app/target/quarkus-app/*.jar /work/
COPY --from=build /app/target/quarkus-app/app/ /work/app/
COPY --from=build /app/target/quarkus-app/quarkus/ /work/quarkus/

ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["sh","-c","java -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=${PORT} -jar /work/quarkus-run.jar"]
