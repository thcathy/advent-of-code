package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static com.adventofcode.year2023.Day14Part2.Direction.*;

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
        var result = totalLoadAfterSpin(puzzle, 1000000000);
        log.warn("What is the total load on the north support beams? {}", result);
    }
    
    long totalLoadAfterSpin(Puzzle puzzle, long runCycle) {
        populateCheckPositionsInSequence(puzzle.width, puzzle.height);
        var hashedCycleMap = new HashMap<String, Long>();
        var cycleToPuzzleMap = new HashMap<Long, Puzzle>();

        for (long i = 1; i <= runCycle; i++) {
            puzzle = spin(puzzle);
            String roundRocksHash = puzzle.getRoundRocksHash();

            // pattern repeat
            if (hashedCycleMap.containsKey(roundRocksHash)) {
                var loopStartCycle = hashedCycleMap.get(roundRocksHash);
                var loopSize = i - loopStartCycle;
                var remainingCycleFromTheEnd = (runCycle - loopStartCycle) % loopSize;
                puzzle = cycleToPuzzleMap.get(loopStartCycle + remainingCycleFromTheEnd);
                break;
            }
            hashedCycleMap.put(roundRocksHash, i);
            cycleToPuzzleMap.put(i, puzzle);
        }
        return totalLoad(puzzle.roundRocks, puzzle.height);
    }

    Puzzle spin(Puzzle puzzle) {
        puzzle = slideRocks(puzzle, NORTH);
        puzzle = slideRocks(puzzle, WEST);
        puzzle = slideRocks(puzzle, SOUTH);
        return slideRocks(puzzle, EAST);
    }

    Puzzle slideRocks(Puzzle puzzle, Direction direction) {
        var slidedRocks = new HashSet<Position>();
        for (var position : checkPositionsInSequence.get(direction)) {
            if (puzzle.roundRocks.contains(position)) {
                slidedRocks.add(slide(puzzle, slidedRocks, position, direction));
            }
        }
        return new Puzzle(puzzle.cubeRocks, slidedRocks, puzzle.width, puzzle.height);
    }

    Map<Direction, List<Position>> checkPositionsInSequence = new HashMap<>();
    void populateCheckPositionsInSequence(int width, int height) {
        var positions = new ArrayList<Position>();
        // NORTH
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                positions.add(new Position(x, y));                
            }
        }
        checkPositionsInSequence.put(NORTH, positions);
        checkPositionsInSequence.put(WEST, positions);
        
        // SOUTH
        positions = new ArrayList<Position>();
        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                positions.add(new Position(x, y));                
            }
        }
        checkPositionsInSequence.put(SOUTH, positions);

        // EAST
        positions = new ArrayList<Position>();
        for (int y = 0; y < height; y++) {
            for (int x = width - 1; x >= 0; x--) {
                positions.add(new Position(x, y));                
            }
        }
        checkPositionsInSequence.put(EAST, positions);
    }

    Position slide(Puzzle puzzle, Set<Position> slidedRocks, Position rock, Direction direction) {
        var thisPosition = rock;
        var nextPosition = rock.move(direction);
        while (puzzle.isValid(nextPosition)
            && !puzzle.cubeRocks.contains(nextPosition) 
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

        String getRoundRocksHash() {
            return roundRocks.stream().map(r -> r.x + "," + r.y).reduce("", (s1, s2) -> s1 + ":" + s2);
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
        var height = inputs.size();
        var width = inputs.get(0).length();
        var cubeRocks = new HashSet<Position>();
        var roundRocks = new HashSet<Position>();
        for (int y = 0; y < height; y++) {
            var line = inputs.get(y);
            for (int x = 0; x < width; x++) {
                switch (line.charAt(x)) {
                    case '#' -> cubeRocks.add(new Position(x, y));
                    case 'O' -> roundRocks.add(new Position(x, y));
                }
            }
        }
        return new Puzzle(cubeRocks, roundRocks, width, height);
    }

    //endregion

    @Test
    public void unitTest() throws IOException {        
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day14_test.txt"), Charsets.UTF_8);
        var puzzle = new Day14Part2().parseInput(lines);
        var result = totalLoadAfterSpin(puzzle, 1000000000);
        Assert.assertEquals(64, result);
    }
}
