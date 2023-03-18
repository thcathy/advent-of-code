package com.adventofcode.year2022;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.adventofcode.year2022.Day24Part2.Direction.*;
import static junit.framework.TestCase.assertEquals;

public class Day24Part2 {
    final static String inputFile = "2022/day24.txt";

    public static void main(String... args) throws IOException {
        Day24Part2 solution = new Day24Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = fewestMinutesToReachGoal(parseInput(lines));
        System.out.println("What is the fewest number of minutes required to avoid the blizzards and reach the goal? " + result);
    }

    int fewestMinutesToReachGoal(Valley valley) {
        var startState = new State(0, valley.startPosition, valley);
        blizzardsPerMinute.put(0, startState.valley.blizzards);
        blizzardsPositionPerMinute.put(0, startState.valley.blizzards.stream().map(Blizzard::position).collect(Collectors.toSet()));

        var state = fewestMinutesToReachGoal(startState, valley.endPosition);
        state = fewestMinutesToReachGoal(state, valley.startPosition);
        state = fewestMinutesToReachGoal(state, valley.endPosition);

        return state.minutes;
    }

    Map<Integer, List<Blizzard>> blizzardsPerMinute = new HashMap<>();
    Map<Integer, Set<Position>> blizzardsPositionPerMinute = new HashMap<>();

    State fewestMinutesToReachGoal(State startState, Position endPosition) {
        var queue = new PriorityQueue<State>(Comparator.comparingInt(s -> s.costToGoal(endPosition)));
        var visited = new HashSet<String>();
        queue.add(startState);

        while (true) {
            var state = queue.poll();

            if (state.position.equals(endPosition)) return state;

            var nextMinute = state.minutes + 1;
            if (!blizzardsPerMinute.containsKey(nextMinute)) {
                var nextBlizzards = blizzardsPerMinute.get(state.minutes).stream().map(b -> b.move(state.valley.maxX, state.valley.maxY)).toList();
                blizzardsPerMinute.put(nextMinute, nextBlizzards);
                blizzardsPositionPerMinute.put(nextMinute, nextBlizzards.stream().map(Blizzard::position).collect(Collectors.toSet()));
            }

            for (Direction moveDirection : Direction.values()) {
                var next = state.position.move(moveDirection);
                var stateKey = stateKey(nextMinute, next.x, next.y);
                if (state.valley.valid(next)
                        && !visited.contains(stateKey)
                        && !blizzardsPositionPerMinute.get(nextMinute).contains(next)) {
                    visited.add(stateKey);
                    queue.add(new State(state.minutes + 1, next, state.valley));
                }
            }
        }
    }

    static String stateKey(int minute, int x, int y) { return "%d-%d,%d".formatted(minute, x, y); }

    static int distance(Position a, Position b) { return Math.abs(a.x - b.x) + Math.abs(a.y - b.y); }

    Valley parseInput(List<String> inputs) {
        var blizzards = new ArrayList<Blizzard>();
        for (int y = 0; y < inputs.size(); y++) {
            var line = inputs.get(y);
            for (int x = 0; x < line.length(); x++) {
                var direction = parseDirection(line.charAt(x));
                if (direction.isPresent()) {
                    blizzards.add(new Blizzard(new Position(x, y), direction.get()));
                }
            }
        }
        Position startPosition = null, endPosition = null;
        var topLine = inputs.get(0);
        var bottomLine = inputs.get(inputs.size() - 1);
        for (int x = 0; x < topLine.length(); x++) {
            if (topLine.charAt(x) == '.') startPosition = new Position(x, 0);
            if (bottomLine.charAt(x) == '.') endPosition = new Position(x, inputs.size() - 1);
        }
        return new Valley(blizzards, inputs.get(0).length() - 1, inputs.size() - 1, startPosition, endPosition);
    }

    Optional<Direction> parseDirection(char c) {
        return switch (c) {
            case '^' -> Optional.of(NORTH);
            case 'v' -> Optional.of(SOUTH);
            case '>' -> Optional.of(EAST);
            case '<' -> Optional.of(WEST);
            default -> Optional.empty();
        };
    }

    record State(int minutes, Position position, Valley valley) {
        int costToGoal(Position goal) { return minutes + distance(goal, position); }
    }

    record Valley(List<Blizzard> blizzards, int maxX, int maxY, Position startPosition, Position endPosition) {
        public boolean valid(Position position) {
            return position.equals(endPosition)
                    || position.equals(startPosition)
                    || (position.x > 0 && position.y > 0 && position.x < maxX && position.y < maxY);
        }
    }

    record Blizzard(Position position, Direction direction) {
        Blizzard move(int maxX, int maxY) {
            var next = position.move(direction);
            if (next.x <= 0) {
                next = new Position(maxX - 1, next.y);
            } else if (next.y <= 0) {
                next = new Position(next.x, maxY - 1);
            } else if (next.x >= maxX) {
                next = new Position(1, next.y);
            } else if (next.y >= maxY) {
                next = new Position(next.x, 1);
            }
            return new Blizzard(next, direction);
        }
    }

    enum Direction { NORTH, SOUTH, WEST, EAST, NONE }

    record Position(int x, int y) {
        public Position move(Direction direction) {
            return switch (direction) {
                case NORTH -> new Position(x, y - 1);
                case SOUTH -> new Position(x, y + 1);
                case WEST -> new Position(x - 1, y);
                case EAST -> new Position(x + 1, y);
                case NONE -> this;
            };
        }
    }

    @Test
    public void unitTest() throws Exception {
        var lines1 = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day24_test.txt").toURI()));
        var valley = parseInput(lines1);

        assertEquals(new Position(1, 0), valley.startPosition);
        assertEquals(new Position(6, 5), valley.endPosition);
        assertEquals(19, valley.blizzards.size());
        assertEquals(new Blizzard(new Position(6, 4), EAST), valley.blizzards.get(18));
        assertEquals(7, valley.maxX);
        assertEquals(5, valley.maxY);

        assertEquals(54, fewestMinutesToReachGoal(valley));
    }

}
