package snakes.computerVisionNeuralNetwork;

import snakes.Bot;
import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;
import snakes.SnakesUIMain;

public class Bot_NN implements Bot {

    public static final double TAIL_OPACITY = 0.3;

    private final NeuralNetwork neuralNetwork;

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

        Snake current = snake;
        for (int k = 0; k < 2; k++) {
            double opacityDelt = (1 - TAIL_OPACITY) / (current.body.size() - 1);
            double opacity = 1.0;
            for (Coordinate coord : current.body) {
                inputData[inputDataIndex + coord.x * SnakesUIMain.MAZE_SIZE.x + coord.y] = opacity;
                opacity -= opacityDelt;
            }
            inputDataIndex += SnakesUIMain.MAZE_SIZE.x * SnakesUIMain.MAZE_SIZE.y;
            current = opponent;
        }

        inputData[inputDataIndex + apple.x * SnakesUIMain.MAZE_SIZE.x + apple.y] = 1;

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
