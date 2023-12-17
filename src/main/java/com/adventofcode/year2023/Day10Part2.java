package com.adventofcode.year2023;

import com.adventofcode.shared.Direction;
import com.adventofcode.shared.Position;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class Day10Part2 {
    Logger log = LoggerFactory.getLogger(Day10Part2.class);
    final static String inputFile = "2023/day10.txt";

    public static void main(String... args) throws IOException {
        Day10Part2 solution = new Day10Part2();
        solution.run();
    }
    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var puzzle = Puzzle.parse(lines);
        var result = puzzle.totalTilesEnclosed(puzzle.findLoop(puzzle.startStates()));
        log.warn("How many tiles are enclosed by the loop? {}", result);
    }

    record Puzzle(Map<Position, Character> pipes, int width, int height) {
        static Puzzle parse(List<String> inputs) {
            var pipes = new HashMap<Position, Character>();
            for (int y = 0; y < inputs.size(); y++) {
                var line = inputs.get(y);
                for (int x = 0; x < line.length(); x++) {
                    pipes.put(new Position(x, y), line.charAt(x));
                }
            }
            return new Puzzle(pipes, inputs.get(0).length(), inputs.size());
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
                    .map(d -> new State(startPosition, 0, d, new HashSet<>(Collections.singleton(startPosition))))
                    .toList();
        }

        Set<Position> findLoop(List<State> states) {
            var nextStates = new ArrayList<State>();
            var nextPositionsAndState = new HashMap<Position, State>();
            for (var state : states) {
                var optionalNextState = nextState(state);
                if (optionalNextState.isEmpty()) continue;
                var nextState = optionalNextState.get();

                var anotherStateAtSamePosition = nextPositionsAndState.get(nextState.position);
                if (anotherStateAtSamePosition != null) {
                    nextState.pipes.addAll(anotherStateAtSamePosition.pipes);
                    return nextState.pipes;
                }

                nextPositionsAndState.put(nextState.position, nextState);
                nextStates.add(nextState);
            }
            return findLoop(nextStates);
        }

        int totalTilesEnclosed(Set<Position> loop) {
            int count = 0;
            for (int y = 0 ; y < height; y++) {
                boolean insideLoop = false;
                char lastCurve = '.';
                for (int x = 0; x < width; x++) {
                    var pos = new Position(x, y);
                    var value = loop.contains(pos) ? pipes.get(new Position(x, y)) : '.';
                    switch (value) {
                        case '.' -> {
                            if (insideLoop) count++;
                        }
                        case '|' -> insideLoop = !insideLoop;
                        case 'L', 'F' -> lastCurve = value;
                        case 'J' -> {
                            if (lastCurve == 'F') insideLoop = !insideLoop;
                        }
                        case '7' -> {
                            if (lastCurve == 'L') insideLoop = !insideLoop;
                        }
                    }
                }
            }
            return count;
        }

        private Optional<State> nextState(State state) {
            var nextPosition = state.position.move(state.direction);
            if (isValid(nextPosition, state.direction)) {
                var nextDirection = turn(state.direction, pipes.get(nextPosition));
                var pipes = new HashSet<>(state.pipes);
                pipes.add(nextPosition);
                return Optional.of(new State(nextPosition, state.steps + 1, nextDirection, pipes));
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

    record State(Position position, int steps, Direction direction, Set<Position> pipes) {}
    
    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day10_test3.txt"), Charsets.UTF_8);
        var puzzle = Puzzle.parse(lines);
        assertEquals(10, puzzle.totalTilesEnclosed(puzzle.findLoop(puzzle.startStates())));

        lines = Resources.readLines(ClassLoader.getSystemResource("2023/day10_test4.txt"), Charsets.UTF_8);
        puzzle = Puzzle.parse(lines);
        assertEquals(4, puzzle.totalTilesEnclosed(puzzle.findLoop(puzzle.startStates())));
    }
}
