FROM openjdk:17
WORKDIR /app
COPY ./target/bridle-gateway-0.0.5.jar  .
EXPOSE 8080
ENTRYPOINT ["java","-jar","bridle-gateway-0.0.5.jar"]