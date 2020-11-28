package snakes.NeuralNetwork;

import java.util.ArrayList;
import java.util.List;

import snakes.Bot;
import snakes.Coordinate;
import snakes.Direction;
import snakes.NeuralNetwork.NNFunctions.DistanceToAnyObject;
import snakes.NeuralNetwork.NNFunctions.ManhattanDistanceToTheApple;
import snakes.NeuralNetwork.NNFunctions.NNFunction;
import snakes.Snake;

public class Bot_NN implements Bot {

    NeuralNetwork neuralNetwork;

    public Bot_NN(NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
    }

    private boolean isValid(Direction direction, Snake snake, Snake opponent, Coordinate mazeSize) {
        Coordinate newHead = snake.getHead().moveTo(direction);
        if (!newHead.inBounds(mazeSize))
            return false; // Left maze
        if (snake.elements.contains(newHead))
            return false; // Collided with itself
        if (opponent.elements.contains(newHead))
            return false; // Collided with opponent

        return true;
    }

    @Override
    public Direction chooseDirection(
        Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple
    ) {
        Direction best_direction = null;
        double max_score = -Float.MAX_VALUE;

        List<Double> inputData = new ArrayList<>();
        NNFunction function = new DistanceToAnyObject();
        NNFunction function2 = new ManhattanDistanceToTheApple();

        System.out.print("inputData: ");
        for (Direction to : Direction.values()) {
            inputData.add(1 - function2.value(to, snake, opponent, mazeSize, apple));
            inputData.add(function.value(to, snake, opponent, mazeSize, apple));
            System.out.print(inputData.get(inputData.size() - 2) + " ");
            System.out.print(inputData.get(inputData.size() - 1) + " ");
        }
        System.out.println();

        List<Double> result = neuralNetwork.calculate(inputData);
        System.out.print("result: ");
        for (Double res : result) {
            System.out.print(res + " ");
        }
        System.out.println('\n');

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
