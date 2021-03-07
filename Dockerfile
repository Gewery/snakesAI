FROM amazoncorretto:8
COPY . /tmp
WORKDIR /tmp
RUN find . -name "*.java" > sources.txt
RUN mkdir out
RUN javac @sources.txt
WORKDIR /tmp/src
ENTRYPOINT ["java","snakes/simpleNeuralNetwork/NeuralNetworksMain"]
