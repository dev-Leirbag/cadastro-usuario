FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/cad-funcionario-0.0.1-SNAPSHOT.jar /app/cad-funcionario.jar

EXPOSE 9090

CMD ["java", "-jar", "/app/cad-funcionario.jar"]