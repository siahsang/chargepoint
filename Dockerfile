FROM maven:3.9.9-amazoncorretto-21 AS builder
WORKDIR /opt/app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean install -Dmaven.test.skip=true


FROM amazoncorretto:21.0.6-al2023
COPY --from=builder /opt/app/target/*.jar /app.jar
EXPOSE 8080
CMD java -jar /app.jar


