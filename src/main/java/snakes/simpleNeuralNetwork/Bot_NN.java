package snakes.simpleNeuralNetwork;

import java.time.Instant;

import snakes.Bot;
import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

public class Bot_NN implements Bot {

    private NeuralNetwork neuralNetwork;

    public Bot_NN(NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
    }

    private boolean isValid(Direction direction, Snake snake, Snake opponent, Coordinate mazeSize) {
        Coordinate newHead = snake.getHead().moveTo(direction);
        if (!newHead.inBounds(mazeSize)) {
            return false; // Left maze
        }
        if (snake.elements.contains(newHead)) {
            return false; // Collided with itself
        }
        if (opponent.elements.contains(newHead)) {
            return false; // Collided with opponent
        }

        return true;
    }

    @Override
    public Direction chooseDirection(
        Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple
    ) {
        Direction bestDirection = null;
        double maxScore = 0;

        double[] inputData = new double[Population.FIRST_LAYER_NEURONS_NUMBER];
        int inputDataIndex = 0;

        int maxDimension = Math.max(mazeSize.x, mazeSize.y);

        inputData[inputDataIndex++] = apple.x * 1.0 / maxDimension;
        inputData[inputDataIndex++] = apple.y * 1.0 / maxDimension;

        // (all cells - 1(for apple) - 1 (for opponent's head)) * 2 (different inputs for x and y)
        int maxInputBodySize = (mazeSize.x * mazeSize.y - 2) * 2;

        Snake current = snake;
        for (int i = 0; i < 2; i++) {
            for (Coordinate coordinate : current.body) {
                inputData[inputDataIndex++] = (coordinate.x + 1.0) / maxDimension;
                inputData[inputDataIndex++] = (coordinate.y + 1.0) / maxDimension;
            }

            for (int j = 0; j < maxInputBodySize - current.body.size() * 2; j++) {
                inputData[inputDataIndex++] = 0.0;
            }
            current = opponent;
        }

        double[] result = neuralNetwork.calculate(inputData);

        for (int i = 0; i < Direction.values().length; i++) {
            Direction to = Direction.values()[i];
            if (isValid(to, snake, opponent, mazeSize)) {
                double directionScore = result[i];
                if (directionScore >= maxScore) {
                    maxScore = directionScore;
                    bestDirection = to;
                }
            }
        }
        if (bestDirection == null) { // Function is incorrect OR no valid directions
            return Direction.LEFT;
        }

        return bestDirection;
    }
}
