package snakes.GeneticProgramming.Subfunctions;

import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

import java.util.ArrayList;

public class DensityChange implements Subfunction {

    /**
     * Given game state and direction calculates the change of density of this direction
     *
     * @param direction direction to calculate goodness for
     * @param snake     information about current snake
     * @param opponent  information about opponent's snake
     * @param mazeSize  boardSize
     * @param apple     apple's coordinate
     * @return any float value, the larger the number, the better for the snake this direction
     */
    @Override
    public double value(Direction direction, Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple) {
        int gridX = findLeastDivisor(mazeSize.x);
        int gridY = findLeastDivisor(mazeSize.y);

        Coordinate head = snake.getHead();
        Coordinate headMoved = head.moveTo(direction);

        double densityInitial = calculateDensity(head, snake, opponent, gridX, gridY);
        double densityIFinal = calculateDensity(headMoved, snake, opponent, gridX, gridY);

        // positive - next area with lower density, negative - next area with higher density
        return (densityInitial - densityIFinal) / (gridX * gridY);
    }

    private int findLeastDivisor(int n) {
        for (int i = 3; i <= n; i++)
            if (n % i == 0) {
                return i;
            }

        if (n % 2 == 0) {
            return 2;
        } else {
            return 1;
        }
    }

    private double calculateDensity(Coordinate head, Snake snake, Snake opponent, int gridX, int gridY) {
        int minX = (int) Math.floor((float) head.x / gridX) * gridX;
        int minY = (int) Math.floor((float) head.x / gridX) * gridY;

        int maxX = (int) (Math.ceil((float) head.x / gridX) * gridX);
        int maxY = (int) (Math.ceil((float) head.y / gridY) * gridY);

        ArrayList<Coordinate> coordinatesFromBlock = new ArrayList<>();


        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                coordinatesFromBlock.add(new Coordinate(x, y));
            }
        }

        int counter = 0, hit = 0;

        for (Coordinate bodyPiece : coordinatesFromBlock) {
            if (snake.elements.contains(bodyPiece)) {
                hit += 1;
            } else if (opponent.elements.contains(bodyPiece)) {
                hit += 1;
            }
            counter += 1;
        }

        return 1.0 * hit / counter;
    }
}
