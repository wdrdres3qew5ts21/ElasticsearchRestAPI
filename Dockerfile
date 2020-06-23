FROM openjdk:11-slim
WORKDIR /app
ADD target/transform-0.0.1-SNAPSHOT.jar .
ADD src/main/resources/config/elasticCert.cer .
ENV elastic.certificatePath=/app/elasticCert.cer
EXPOSE 8080:8080
CMD [ "java","-jar","transform-0.0.1-SNAPSHOT.jar" ]