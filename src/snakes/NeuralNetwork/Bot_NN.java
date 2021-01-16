package snakes.NeuralNetwork;

import java.util.ArrayList;
import java.util.List;

import snakes.Bot;
import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

public class Bot_NN implements Bot {

    NeuralNetwork neuralNetwork;

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
        Direction best_direction = null;
        double max_score = -Float.MAX_VALUE;

        List<Double> inputData = new ArrayList<>();

        inputData.add(mazeSize.x * 1.0);
        inputData.add(mazeSize.y * 1.0);
        inputData.add(apple.x * 1.0);
        inputData.add(apple.y * 1.0);

        // (all cells - 1(for apple) - 1 (for opponent's head)) * 2 (different inputs for x and y)
        int maxInputBodySize = (mazeSize.x * mazeSize.y - 2) * 2;

        Snake current = snake;
        for (int i = 0; i < 2; i++) {
            for (Coordinate coordinate : current.body) {
                inputData.add(coordinate.x + 1.0);
                inputData.add(coordinate.y + 1.0);
            }

            for (int j = 0; j < maxInputBodySize - current.body.size() * 2; j++) {
                inputData.add(0.0);
            }
            current = opponent;
        }

        List<Double> result = neuralNetwork.calculate(inputData);

        for (int i = 0; i < Direction.values().length; i++) {
            Direction to = Direction.values()[i];
            if (isValid(to, snake, opponent, mazeSize)) {
                double cur = result.get(i);
                if (cur >= max_score) {
                    max_score = cur;
                    best_direction = to;
                }
            }
        }
        if (best_direction == null) { // Function is incorrect OR no valid directions
            return Direction.LEFT;
        }

        return best_direction;
    }
}
