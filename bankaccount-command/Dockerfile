FROM gradle:5.4.1-jre11-slim as builder
COPY --chown=gradle:gradle . /home/gradle/webapp
WORKDIR /home/gradle/webapp
RUN gradle clean :bankaccount-command:bootJar --stacktrace

FROM azul/zulu-openjdk-alpine:11.0.3
COPY --from=builder /home/gradle/webapp/bankaccount-command/build/libs/application.jar application.jar

ENTRYPOINT ["java", "-jar", "application.jar"]