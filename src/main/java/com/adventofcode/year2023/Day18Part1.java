package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Day18Part1 {
    Logger log = LoggerFactory.getLogger(Day18Part1.class);
    final static String inputFile = "2023/day18.txt";

    public static void main(String... args) throws IOException {
        Day18Part1 solution = new Day18Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var puzzle = new Day18Part1().parseInput(lines);
        var result = findLagoonSize(puzzle);
        log.warn("how many cubic meters of lava could it hold? {}", result);
    }

    int findLagoonSize(Puzzle puzzle) {
        var enclosed = new HashSet<Position>();
        var queue = new ArrayDeque<Position>();
        queue.add(new Position(1, 1));
        while (!queue.isEmpty()) {
            var position = queue.poll();
            position.neighbors()
                    .filter(p -> !enclosed.contains(p) && !puzzle.trench.contains(p))
                    .peek(enclosed::add)
                    .forEach(queue::add);
        }
        return enclosed.size() + puzzle.trench.size();
    }

    //region Data Objects

    record Puzzle(Set<Position> trench, int width, int height) {}

    record DigPath(Direction direction, int length) {}

    record Position(int x, int y) {
        Position move(Direction direction) {
            return switch (direction) {
                case U -> new Position(x, y-1);
                case D -> new Position(x, y+1);
                case L -> new Position(x-1, y);
                case R -> new Position(x+1, y);
            };
        }

        Stream<Position> neighbors() {
            return Stream.of(
                    new Position(x-1, y),
                    new Position(x+1, y),
                    new Position(x, y-1),
                    new Position(x, y+1)
            );
        }

    }

    enum Direction { U, D, L, R }
    
    //endregion

    //region Input Parsing

    Puzzle parseInput(List<String> inputs) {
        var digPaths = inputs.stream().map(s -> {
            var parts = s.split(" ");
            return new DigPath(Direction.valueOf(parts[0]), Integer.parseInt(parts[1]));
        }).toList();
        var trench = findTrench(digPaths);
        var width = trench.stream().mapToInt(p -> p.x).max().getAsInt() + 1;
        var height = trench.stream().mapToInt(p -> p.y).max().getAsInt() + 1;
        return new Puzzle(trench, width, height);
    }

    Set<Position> findTrench(List<DigPath> digPaths) {
        Set<Position> trench = new HashSet<>();
        Position position = new Position(0, 0);
        trench.add(position);
        for (var digPath : digPaths) {
            for (int i = 0; i < digPath.length; i++) {
                position = position.move(digPath.direction);
                trench.add(position);
            }
        }
        return trench;
    }

    //endregion

    @Test
    public void unitTest() throws IOException {        
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day18_test.txt"), Charsets.UTF_8);
        var puzzle = new Day18Part1().parseInput(lines);
        var result = findLagoonSize(puzzle);
        Assert.assertEquals(62, result);
    }
}
