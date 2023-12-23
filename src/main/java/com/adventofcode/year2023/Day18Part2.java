package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day18Part2 {
    Logger log = LoggerFactory.getLogger(Day18Part2.class);
    final static String inputFile = "2023/day18.txt";

    public static void main(String... args) throws IOException {
        Day18Part2 solution = new Day18Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var puzzle = new Day18Part2().parseInput(lines);
        var result = findLagoonSize(puzzle);
        log.warn("how many cubic meters of lava could it hold? {}", result);
    }

    // used shoelace formula
    long findLagoonSize(List<DigPath> paths) {
        long perimeter = 0; // used for shoelace formula
        var position = new Position(0, 0);
        var trench = new ArrayList<Position>();
        trench.add(position);

        for (var path : paths) {
            position = position.move(path.direction, path.length);
            perimeter += path.length;
            trench.add(position);
        }

        long area = 0;
        for (int i = 0; i < trench.size() - 1; i++) {
            area += area(trench.get(i), trench.get(i+1));
        }
        area += area(trench.getLast(), trench.getFirst());
        area = Math.abs(area);
        return (area + perimeter) / 2 + 1;
    }

    long area(Position a, Position b) { return (long) a.x * b.y - (long) a.y * b.x; }

    //region Data Objects

    record DigPath(Direction direction, int length) {}

    record Position(int x, int y) {
        Position move(Direction direction, int length) {
            return switch (direction) {
                case U -> new Position(x, y-length);
                case D -> new Position(x, y+length);
                case L -> new Position(x-length, y);
                case R -> new Position(x+length, y);
            };
        }
    }

    enum Direction {
        U, D, L, R;
        static Direction valueOf(char value) {
            return switch (value) {
                case '0' -> R;
                case '1' -> D;
                case '2' -> L;
                case '3' -> U;
                default -> throw new IllegalStateException("Unexpected value: " + value);
            };
        }
    }
    
    //endregion

    //region Input Parsing

    List<DigPath> parseInput(List<String> inputs) {
        return inputs.stream().map(s -> {
            var parts = s.split(" ");
            var direction = Direction.valueOf(parts[2].charAt(7));
            var length = Integer.parseInt(parts[2].substring(2, 7), 16);
            return new DigPath(direction, length);
        }).toList();
    }

    //endregion

    @Test
    public void unitTest() throws IOException {        
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day18_test.txt"), Charsets.UTF_8);
        var puzzle = new Day18Part2().parseInput(lines);
        var result = findLagoonSize(puzzle);
        Assert.assertEquals(952408144115L, result);
    }
}
