package snakes.GeneticProgramming.Subfunctions;

import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

import java.util.Arrays;

abstract class ShortestPath extends ManhattanDistance implements Subfunction {
    /**
     * Given game state and direction calculates the distance to object from given direction
     *
     * @param direction direction to calculate goodness for
     * @param snake     information about current snake
     * @param opponent  information about opponent's snake
     * @param mazeSize  boardSize
     * @param apple     apple's coordinate
     * @return distance to object
     */
    @Override
    abstract public double value(Direction direction, Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple);

    /**
     * Calculate shortest distance from source to destination
     *
     * @param source      Starting coordinate
     * @param destination Finish coordinate
     * @param snake       information about current snake
     * @param opponent    information about opponent's snake
     * @param mazeSize    boardSize
     * @param apple       apple's coordinate
     * @return Shortest distance from source to destination
     */
    protected float calculateShortestPath(Coordinate source, Coordinate destination,
                                          Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple) {


        LeeAlgorithm leeAlgorithm = new LeeAlgorithm(mazeSize);

        int[][] board = buildBoard(snake, opponent, mazeSize, apple);

        return leeAlgorithm.calculateShortestPath(board, source, destination);
    }

    /**
     * Constructs new board with obstacles such as snakes and apples to compute distances on it
     *
     * @param snake    information about current snake
     * @param opponent information about opponent's snake
     * @param mazeSize boardSize
     * @param apple    apple's coordinate
     * @return new board with obstacles
     */
    protected int[][] buildBoard(Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple) {
        // 1 - valid path, 0 - wall, obstacle
        int[][] board = new int[mazeSize.x][mazeSize.y];
        int[] row = new int[mazeSize.x];
        Arrays.fill(row, 1);
        Arrays.fill(board, row);

        for (Coordinate bodyCell : snake.body) {
            board[bodyCell.x][bodyCell.y] = 0;
        }

        for (Coordinate bodyCell : opponent.body) {
            board[bodyCell.x][bodyCell.y] = 0;
        }

        board[apple.x][apple.y] = 0;

        return board;
    }
}



