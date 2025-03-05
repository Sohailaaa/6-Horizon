FROM openjdk:25-ea-4-jdk-oraclelinux9
WORKDIR /app
COPY ./ /app
# Copy JSON files into the container
COPY src/main/java/com/example/data/*.json /app/config/

ENV spring.application.userDataPath="/app/config/users.json"
ENV spring.application.productDataPath="/app/config/products.json"
ENV spring.application.orderDataPath="/app/config/orders.json"
ENV spring.application.cartDataPath="/app/config/carts.json"

EXPOSE 8085
CMD ["java","-jar","/app/target/mini1.jar"]