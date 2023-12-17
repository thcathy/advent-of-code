package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class Day10Part1 {
    Logger log = LoggerFactory.getLogger(Day10Part1.class);
    final static String inputFile = "2023/day10.txt";

    public static void main(String... args) throws IOException {
        Day10Part1 solution = new Day10Part1();
        solution.run();
    }
    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var puzzle = Puzzle.parse(lines);
        var result = puzzle.stepsToFarthest(puzzle.startStates());
        log.warn("How many steps along the loop does it take to get from the starting position to the point farthest from the starting position? {}", result);
    }

    record Puzzle(Map<Position, Character> pipes) {
        static Puzzle parse(List<String> inputs) {
            var pipes = new HashMap<Position, Character>();
            for (int y = 0; y < inputs.size(); y++) {
                var line = inputs.get(y);
                for (int x = 0; x < line.length(); x++) {
                    pipes.put(new Position(x, y), line.charAt(x));
                }
            }
            return new Puzzle(pipes);
        }

        Position startPosition() {
            return pipes.entrySet().stream()
                    .filter(e -> e.getValue() == 'S')
                    .map(Map.Entry::getKey)
                    .findFirst().get();
        }

        List<State> startStates() {
            var startPosition = startPosition();
            return Arrays.stream(Direction.values())
                            .map(d -> new State(startPosition, 0, d))
                                    .toList();
        }

        int stepsToFarthest(List<State> states) {
            var nextStates = new ArrayList<State>();
            var nextPositions = new HashSet<Position>();
            for (var state : states) {
                var optionalNextState = nextState(state);
                if (optionalNextState.isEmpty()) continue;
                var nextState = optionalNextState.get();
                if (nextPositions.contains(nextState.position))
                    return nextState.steps;
                nextPositions.add(nextState.position);
                nextStates.add(nextState);
            }
            return stepsToFarthest(nextStates);
        }

        private Optional<State> nextState(State state) {
            var nextPosition = state.position.move(state.direction);
            if (isValid(nextPosition, state.direction)) {
                var nextDirection = turn(state.direction, pipes.get(nextPosition));
                return Optional.of(new State(nextPosition, state.steps + 1, nextDirection));
            }
            return Optional.empty();
        }

        Direction turn(Direction direction, Character pipe) {
            return switch (pipe) {
                case 'L' -> direction == Direction.SOUTH ? Direction.EAST : Direction.NORTH;
                case 'J' -> direction == Direction.SOUTH ? Direction.WEST : Direction.NORTH;
                case '7' -> direction == Direction.NORTH ? Direction.WEST : Direction.SOUTH;
                case 'F' -> direction == Direction.NORTH ? Direction.EAST : Direction.SOUTH;
                default -> direction;
            };
        }

        boolean isValid(Position position, Direction direction) {
            var pipe = pipes.getOrDefault(position, '.');
            return switch (direction) {
                case NORTH -> pipe == '|' || pipe == '7' || pipe == 'F';
                case SOUTH -> pipe == '|' || pipe == 'L' || pipe == 'J';
                case EAST -> pipe == '-' || pipe == 'J' || pipe == '7';
                case WEST -> pipe == '-' || pipe == 'L' || pipe == 'F';
            };
        }
    }

    record State(Position position, int steps, Direction direction) {}

    record Position(int x, int y) {
        public Position move(Direction direction) {
            return switch (direction) {
                case NORTH -> new Position(x, y - 1);
                case SOUTH -> new Position(x, y + 1);
                case WEST -> new Position(x - 1, y);
                case EAST -> new Position(x + 1, y);
            };
        }
    }

    enum Direction { NORTH, SOUTH, WEST, EAST }
    
    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day10_test.txt"), Charsets.UTF_8);
        var puzzle = Puzzle.parse(lines);
        assertEquals(4, puzzle.stepsToFarthest(puzzle.startStates()));

        lines = Resources.readLines(ClassLoader.getSystemResource("2023/day10_test2.txt"), Charsets.UTF_8);
        puzzle = Puzzle.parse(lines);
        assertEquals(8, puzzle.stepsToFarthest(puzzle.startStates()));
    }
}
