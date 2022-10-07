FROM eclipse-temurin

EXPOSE 8080

RUN mkdir code && cd code

WORKDIR code

COPY ./target/employee-agg-service-0.0.1-SNAPSHOT.jar .

ENTRYPOINT [ "java", "-jar", "employee-agg-service-0.0.1-SNAPSHOT.jar" ]
