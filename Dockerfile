FROM amazoncorretto:8
COPY ./out/production/snakes /tmp
WORKDIR /tmp
ENTRYPOINT ["java","snakes/NeuralNetwork/NeuralNetworksMain"]
