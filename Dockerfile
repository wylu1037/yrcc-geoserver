FROM gradle:8.2-jdk17 AS builder
WORKDIR /tmp
COPY . /tmp
RUN gradle 'bootJar'
RUN java -Djarmode=layertools -jar /tmp/build/libs/*.jar extract

FROM openjdk:17-jdk-alpine AS runner
WORKDIR /app
COPY --from=builder tmp/dependencies ./
COPY --from=builder tmp/spring-boot-loader ./
COPY --from=builder tmp/snapshot-dependencies ./
COPY --from=builder tmp/application ./

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher", "-Dspring.config.location=/app/application.yml"]
