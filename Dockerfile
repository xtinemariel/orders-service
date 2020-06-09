FROM openjdk:8-jre-alpine
COPY build/libs/orders-service-0.0.1-SNAPSHOT.jar orders-service.jar
ENTRYPOINT ["java","-jar","orders-service.jar"]