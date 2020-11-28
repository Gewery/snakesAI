package snakes.NeuralNetwork.NNFunctions;

import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

public interface NNFunction {
    double value(Direction direction, Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple);
}
