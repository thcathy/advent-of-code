package com.adventofcode.year2023;

import static com.adventofcode.year2023.Day14Part2.Direction.NORTH;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day14Part2 {
    Logger log = LoggerFactory.getLogger(Day14Part2.class);
    final static String inputFile = "2023/day14.txt";

    public static void main(String... args) throws IOException {
        Day14Part2 solution = new Day14Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var puzzle = new Day14Part2().parseInput(lines);
        var result = totalLoad(allRockSlideToNorth(puzzle), puzzle.height);
        log.warn("What is the total load on the north support beams? {}", result);
    }

    Set<Position> allRockSlideToNorth(Puzzle puzzle) {
        var slidedRocks = new HashSet<Position>();
        for (int y = 0; y < puzzle.height; y++) {
            for (int x = 0; x < puzzle.width; x++) {
                var position = new Position(x, y);
                if (puzzle.roundRocks.contains(position)) {
                    slidedRocks.add(slide(puzzle, slidedRocks, position, NORTH));
                }
            }
        }

        System.out.println(">>> debug");
        for (int y = 0 ; y < puzzle.height; y++) {
            for (int x = 0; x < puzzle.width; x++) {
                var c = slidedRocks.contains(new Position(x, y)) ? 'X' : '.';
                System.out.print(c);
            }
            System.out.println();
        }
        System.out.println(">>>>>>>>>");

        return slidedRocks;
    }

    Position slide(Puzzle puzzle, Set<Position> slidedRocks, Position rock, Direction direction) {
        var thisPosition = rock;
        var nextPosition = rock.move(direction);
        while (puzzle.isValid(nextPosition)
            && !puzzle.roundRocks.contains(nextPosition) 
            && !slidedRocks.contains(nextPosition)) {
            thisPosition = nextPosition;
            nextPosition = thisPosition.move(direction);
        }
        return thisPosition;
    }
    
    long totalLoad(Set<Position> rocks, int height) {
        return rocks.stream().mapToLong(r -> (long) height - r.y).sum();
    }

    //region Data Objects

    record Puzzle(Set<Position> cubeRocks, Set<Position> roundRocks, int width, int height) {
        boolean isValid(Position position) {
            return position.x >= 0 && position.x < width && position.y >= 0 && position.y < height;
        }
    }

    record Position(int x, int y) {
        Position move(Direction direction) {
            return switch (direction) {
                case NORTH -> new Position(x, y - 1);
                case SOUTH -> new Position(x, y + 1);
                case WEST -> new Position(x - 1, y);
                case EAST -> new Position(x + 1, y);
            };
        }
    }

    enum Direction { NORTH, SOUTH, WEST, EAST }
    
    //endregion

    //region Input Parsing

    Puzzle parseInput(List<String> inputs) {
        var heigth = inputs.size();
        var width = inputs.get(0).length();
        var cubeRocks = new HashSet<Position>();
        var roundRocks = new HashSet<Position>();
        for (int y = 0; y < heigth; y++) {
            var line = inputs.get(y);
            for (int x = 0; x < width; x++) {
                switch (line.charAt(x)) {
                    case '#' -> cubeRocks.add(new Position(x, y));
                    case 'O' -> roundRocks.add(new Position(x, y));
                }
            }
        }
        return new Puzzle(cubeRocks, roundRocks, width, heigth);
    }

    //endregion

    @Test
    public void unitTest() throws IOException {        
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day14_test.txt"), Charsets.UTF_8);
        var puzzle = new Day14Part2().parseInput(lines);
        var result = totalLoad(allRockSlideToNorth(puzzle), puzzle.height);
        Assert.assertEquals(136, result);
    }
}
