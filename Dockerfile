FROM maven:3.8.4-openjdk-11-slim as build
WORKDIR /app
COPY ./backend/pom.xml .
RUN mvn dependency:go-offline -B
COPY ./backend/src ./src
RUN mvn package -DskipTests

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE ${PORT:-8080}
CMD ["java", "-jar", "app.jar"]
