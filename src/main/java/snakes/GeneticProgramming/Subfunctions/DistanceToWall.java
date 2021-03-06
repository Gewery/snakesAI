package snakes.GeneticProgramming.Subfunctions;

import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

public class DistanceToWall implements Subfunction {
    @Override
    public double value(Direction direction, Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple) {

        Coordinate to = snake.getHead().moveTo(direction);

        return Math.max(Math.max(Math.abs(mazeSize.x - to.x), to.x - 1) / mazeSize.x, Math.max(mazeSize.y - to.y, to.y - 1) / mazeSize.y) * 2 - 1;
    }
}
