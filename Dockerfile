FROM maven:3.6.3-amazoncorretto-11 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean compile assembly:single

FROM amazoncorretto:11
COPY --from=build /home/app/target/snakes-1.0-SNAPSHOT-jar-with-dependencies.jar /usr/local/lib/snakes-1.0-SNAPSHOT.jar
RUN yum install -y git
RUN git clone https://github.com/Gewery/snakesAI.git
ENTRYPOINT ["java", "-jar", "/usr/local/lib/snakes-1.0-SNAPSHOT.jar"]
