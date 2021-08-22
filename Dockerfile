FROM gradle:jdk14 AS build-env
ADD . /app
WORKDIR /app
RUN gradle shadowJar

FROM openjdk:14-jdk-slim
COPY --from=build-env /app/build/libs/speedtest-telegrambot.jar /app/speedtest-telegrambot.jar
WORKDIR /app
RUN touch .env
ENTRYPOINT ["java", "-Xms1G", "-jar", "speedtest-telegrambot.jar"]