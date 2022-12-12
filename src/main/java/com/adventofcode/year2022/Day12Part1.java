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

public class Day12Part1 {
    final static String inputFile = "2022/day12.txt";

    public static void main(String... args) throws IOException {
        Day12Part1 solution = new Day12Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = fewestStepsToEnd(parseState(lines));
        System.out.println("What is the fewest steps required to move from your current position to the location that should get the best signal? " + result);
    }

    int fewestStepsToEnd(State state) {
        Set<Position> visited = new HashSet<>();
        var steps = new PriorityQueue<>(Comparator.comparing((Step s) -> s.step).thenComparing(s -> distance(s.position, state.endPosition)));
        steps.add(new Step(state.startPosition, 0));
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
        return -1;
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
                map[y][x] = line.charAt(x);
                if (line.charAt(x) == 'S') start = new Position(x, y);
                if (line.charAt(x) == 'E') end = new Position(x, y);
            }
        }

        state.map = map;
        state.startPosition = start;
        state.endPosition = end;
        return state;
    }

    record Position(int x, int y) {}

    record Step(Position position, int step) {}

    int distance(Position a, Position b) { return Math.abs(a.x - b.x) + Math.abs(a.y - b.y); }

    static class State {
        char[][] map;
        Position startPosition;
        Position endPosition;
    }

    @Test
    public void unitTest() throws Exception {
        var lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day12_test.txt").toURI()), Charset.defaultCharset());
        var state = parseState(lines);
        assertEquals(new Position(0, 0), state.startPosition);
        assertEquals(new Position(5, 2), state.endPosition);
        assertEquals(31, fewestStepsToEnd(state));
    }
}
