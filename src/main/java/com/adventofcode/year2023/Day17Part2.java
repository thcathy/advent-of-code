package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static com.adventofcode.year2023.Day17Part2.Direction.*;

public class Day17Part2 {
    Logger log = LoggerFactory.getLogger(Day17Part2.class);
    final static String inputFile = "2023/day17.txt";

    public static void main(String... args) throws IOException {
        Day17Part2 solution = new Day17Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
         var puzzle = new Day17Part2().parseInput(lines);
         var result = minHeatLoss(puzzle);
         log.warn("What is the least heat loss it can incur? {}", result);
    }

    int minHeatLoss(Puzzle puzzle) {
        var queue = new PriorityQueue<>(
                Comparator.comparingInt(MovementState::cost)
        );
        var visited = new HashSet<MovementState>();
        var minHeatLossMap = new HashMap<String, Integer>();
        queue.add(new MovementState(0, new Position(0, 0), Right, 0));
        queue.add(new MovementState(0, new Position(0, 0), Down, 0));

        while (!queue.isEmpty()) {
            var state = queue.poll();
            minHeatLossMap.put(state.key(), state.cost());
            if (puzzle.isEnding(state.position)) return state.heatLoss;

            Arrays.stream(Direction.values())
                    .filter(d -> d != state.direction.opposite())
                    .map(d -> state.createNextState(puzzle.heatMap, d))
                    .filter(s -> s.sameDirectionStep < 10)
                    .filter(s -> puzzle.isValid(s.position))
                    .filter(s -> s.cost() <= minHeatLossMap.getOrDefault(s.key(), Integer.MAX_VALUE))
                    .filter(visited::add)
                    .forEach(queue::add);
        }
        return 0;
    }

    //region Data Objects

    record MovementState(int heatLoss, Position position, Direction direction, int sameDirectionStep) {
        MovementState createNextState(Map<Position, Integer> map, Direction nextDirection) {
            var isTurned = direction != nextDirection;
            var stepToMove = isTurned ? 4 : 1;

            var nextPosition = position;
            var moreLoss = 0;
            for (int i = 0; i < stepToMove; i++) {
                nextPosition = nextPosition.move(nextDirection);
                moreLoss += map.getOrDefault(nextPosition, 0);
            }

            return new MovementState(heatLoss + moreLoss, nextPosition, nextDirection,
                     isTurned ? 3 : sameDirectionStep + 1);
        }

        String key() { return position.x + "," + position.y + direction + sameDirectionStep;}
        int cost() { return heatLoss + position.x + position.y; }
    }

    record Puzzle(Map<Position, Integer> heatMap, int width, int height) {
        boolean isValid(Position position) {
            return position.x >= 0 && position.x < width && position.y >= 0 && position.y < height;
        }

        boolean isEnding(Position position) {
            return position.x == width - 1 && position.y == height - 1;
        }
    }

    record Position(int x, int y) {
        Position move(Direction direction) {
            return switch (direction) {
                case Up -> new Position(x, y-1);
                case Down -> new Position(x, y+1);
                case Left -> new Position(x-1, y);
                case Right -> new Position(x+1, y);
            };
        }
    }

    enum Direction { Up, Down, Left, Right;
        Direction opposite() {
            return switch (this) {
                case Up -> Down;
                case Down -> Up;
                case Left -> Right;
                case Right -> Left;
            };
        }
    }
    
    //endregion

    //region Input Parsing

    Puzzle parseInput(List<String> inputs) {
        var height = inputs.size();
        var width = inputs.getFirst().length();
        var map = new HashMap<Position, Integer>();
        for (int y = 0; y < height; y++) {
            var line = inputs.get(y);
            for (int x = 0; x < width; x++) {                
                map.put(new Position(x, y), Character.getNumericValue(line.charAt(x)));
            }
        }
        return new Puzzle(map, width, height);
    }

    //endregion

    @Test
    public void unitTest() throws IOException {        
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day17_test.txt"), Charsets.UTF_8);
        var puzzle = new Day17Part2().parseInput(lines);
        var result = minHeatLoss(puzzle);
        Assert.assertEquals(94, result);

        var lines2 = Resources.readLines(ClassLoader.getSystemResource("2023/day17_test2.txt"), Charsets.UTF_8);
        var puzzle2 = new Day17Part2().parseInput(lines2);
        var result2 = minHeatLoss(puzzle2);
        Assert.assertEquals(71, result2);
    }
}
