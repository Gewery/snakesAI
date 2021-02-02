FROM amazoncorretto:8
COPY . /tmp
WORKDIR /tmp
RUN find . -name "*.java" > sources.txt
RUN mkdir out
RUN javac @sources.txt
WORKDIR /tmp/src
ENV JAVA_OPTS="-Xmx4G"
ENTRYPOINT ["java","snakes/NeuralNetwork/NeuralNetworksMain"]
