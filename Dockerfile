FROM gradle:8.0.2-jdk17 AS mbuilder 
COPY ./delivery-service /usr/src/
WORKDIR /usr/src/
RUN gradle wrapper --gradle-version 8.0.2
RUN ./gradlew build 

FROM amazoncorretto:17-alpine-jdk
COPY --from=mbuilder /usr/src/build/libs/delivery-service-0.0.1-SNAPSHOT.jar /usr/src/
CMD ["java","-jar","/usr/src/delivery-service-0.0.1-SNAPSHOT.jar"] 
