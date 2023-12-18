package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.LongStream;

import static org.junit.Assert.assertEquals;

public class Day11Part2 {
    Logger log = LoggerFactory.getLogger(Day11Part2.class);
    final static String inputFile = "2023/day11.txt";

    public static void main(String... args) throws IOException {
        Day11Part2 solution = new Day11Part2();
        solution.run();
    }
    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var question = new Day11Part2().parseInput(lines, 1000000);
        var result = question.sumOfExpandedLengths();
        log.warn("What is the sum of these lengths? {}", result);
    }

    long width, height, scale;
    Set<Position> galaxies = new HashSet<>();
    Set<Long> emptyX = new HashSet<>();
    Set<Long> emptyY = new HashSet<>();
    Set<Position> expandedGalaxies = new HashSet<>();

    Day11Part2 parseInput(List<String> inputs, long scale) {
        this.scale = scale;
        height = inputs.size();
        width = inputs.getFirst().length();
        for (int y = 0; y < inputs.size(); y++) {
            var line = inputs.get(y);
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == '#') galaxies.add(new Position(x, y));
            }
        }
        return this;
    }

    long sumOfExpandedLengths() {
        findEmptyX();
        findEmptyY();
        expand();

        long sumOfLengths = 0;
        for (var a : expandedGalaxies) {
            for (var b : expandedGalaxies) {
                if (a.equals(b)) continue;
                sumOfLengths += Position.distance(a, b);
            }
        }
        return sumOfLengths / 2;
    }

    void expand() {
        for (var position : galaxies) {
            var newX = position.x() + LongStream.range(0, position.x).filter(i -> emptyX.contains(i)).count() * (scale - 1);
            var newY = position.y() + LongStream.range(0, position.y).filter(i -> emptyY.contains(i)).count() * (scale - 1);
            expandedGalaxies.add(new Position((int) newX, (int) newY));
        }
    }

    private void findEmptyY() {
        for (long y = 0; y < height; y++) {
            long finalY = y;
            if (LongStream.range(0, width)
                    .mapToObj(x -> new Position(x, finalY))
                    .noneMatch(p -> galaxies.contains(p))) {
                emptyY.add(y);
            }
        }
    }

    private void findEmptyX() {
        for (long x = 0; x < width; x++) {
            long finalX = x;
            if (LongStream.range(0, height)
                    .mapToObj(y -> new Position(finalX, y))
                    .noneMatch(p -> galaxies.contains(p))) {
                emptyX.add(x);
            }
        }
    }

    record Position(long x, long y) {
        static long distance(Position a, Position b) {
            return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
        }
    }

    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day11_test.txt"), Charsets.UTF_8);
        var question = new Day11Part2().parseInput(lines, 100);
        assertEquals(8410, question.sumOfExpandedLengths());
    }
}
