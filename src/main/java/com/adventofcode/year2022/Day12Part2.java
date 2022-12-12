package com.adventofcode.year2022;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class Day12Part2 {
    final static String inputFile = "2022/day12.txt";

    public static void main(String... args) throws IOException {
        Day12Part2 solution = new Day12Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = fewestStepsToEndFromAnyPosition(parseState(lines));
        System.out.println("What is the fewest steps required to move from your current position to the location that should get the best signal? " + result);
    }

    int fewestStepsToEndFromAnyPosition(State state) {
        int fewestSteps = Integer.MAX_VALUE;
        for (Position start : allStartPosition(state))
            fewestSteps = Math.min(fewestSteps, fewestStepsToEnd(state, start));
        return fewestSteps;
    }

    List<Position> allStartPosition(State state) {
        var positions = new ArrayList<Position>();
        var map = state.map;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] == 'a') positions.add(new Position(x, y));
            }
        }
        return positions;
    }

    int fewestStepsToEnd(State state, Position start) {
        Set<Position> visited = new HashSet<>();
        var steps = new PriorityQueue<>(Comparator.comparing((Step s) -> s.step).thenComparing(s -> distance(s.position, state.endPosition)));
        steps.add(new Step(start, 0));
        while (!steps.isEmpty()) {
            var step = steps.poll();
            var height = state.map[step.position.y][step.position.x];
            if (height == 'E') {
                return step.step;
            }

            for (Position next : nextPositions(step.position)) {
                if (isValid(height, next, state.map) && !visited.contains(next)) {
                    steps.add(new Step(next, step.step + 1));
                    visited.add(next);
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    boolean isValid(char height, Position next, char[][] map) {
        if (next.x < 0 || next.y < 0 || next.x >= map[0].length || next.y >= map.length)
            return false;
        var nextHeight = map[next.y][next.x];
        if (nextHeight == 'S') nextHeight = 'a';
        if (nextHeight == 'E') nextHeight = 'z';
        if (height == 'S') height = 'a';
        if (height == 'E') height = 'z';
        return nextHeight <= height + 1;
    }

    List<Position> nextPositions(Position p) {
        return List.of(
                new Position(p.x+1, p.y),
                new Position(p.x-1, p.y),
                new Position(p.x, p.y+1),
                new Position(p.x, p.y-1)
        );
    }

    State parseState(List<String> inputs) {
        char[][] map = new char[inputs.size()][inputs.get(0).length()];

        var state = new State();
        Position start = null, end = null;

        for (int y = 0; y < inputs.size(); y++) {
            var line = inputs.get(y);
            for (int x = 0; x < line.length(); x++) {
                map[y][x] = line.charAt(x) == 'S' ? 'a' : line.charAt(x);
                if (line.charAt(x) == 'E') end = new Position(x, y);
            }
        }

        state.map = map;
        state.endPosition = end;
        return state;
    }

    record Position(int x, int y) {}

    record Step(Position position, int step) {}

    int distance(Position a, Position b) { return Math.abs(a.x - b.x) + Math.abs(a.y - b.y); }

    static class State {
        char[][] map;
        Position endPosition;
    }

    @Test
    public void unitTest() throws Exception {
        var lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day12_test.txt").toURI()), Charset.defaultCharset());
        var state = parseState(lines);
        assertEquals(new Position(5, 2), state.endPosition);
        assertEquals(29, fewestStepsToEndFromAnyPosition(state));
    }
}
