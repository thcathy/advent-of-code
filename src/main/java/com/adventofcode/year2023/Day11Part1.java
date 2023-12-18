package com.adventofcode.year2023;

import com.adventofcode.shared.Position;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class Day11Part1 {
    Logger log = LoggerFactory.getLogger(Day11Part1.class);
    final static String inputFile = "2023/day11.txt";

    public static void main(String... args) throws IOException {
        Day11Part1 solution = new Day11Part1();
        solution.run();
    }
    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var question = new Day11Part1().parseInput(lines);
        var result = question.sumOfExpandedLengths();
        log.warn("What is the sum of these lengths? {}", result);
    }

    int width, height;
    Set<Position> galaxies = new HashSet<>();
    Set<Integer> emptyX = new HashSet<>();
    Set<Integer> emptyY = new HashSet<>();
    Set<Position> expandedGalaxies = new HashSet<>();

    Day11Part1 parseInput(List<String> inputs) {
        height = inputs.size();
        width = inputs.get(0).length();
        for (int y = 0; y < inputs.size(); y++) {
            var line = inputs.get(y);
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == '#') galaxies.add(new Position(x, y));
            }
        }
        return this;
    }

    int sumOfExpandedLengths() {
        expand();

        int sumOfLengths = 0;
        for (var a : expandedGalaxies) {
            for (var b : expandedGalaxies) {
                if (a.equals(b)) continue;
                sumOfLengths += Position.distance(a, b);
            }
        }
        return sumOfLengths / 2;
    }

    void expand() {
        findEmptyX();
        findEmptyY();

        for (var position : galaxies) {
            var newX = position.x() + IntStream.range(0, position.x()).filter(i -> emptyX.contains(i)).count();
            var newY = position.y() + IntStream.range(0, position.y()).filter(i -> emptyY.contains(i)).count();
            expandedGalaxies.add(new Position((int) newX, (int) newY));
        }
    }

    private void findEmptyY() {
        for (int y = 0; y < height; y++) {
            int finalY = y;
            if (IntStream.range(0, width)
                    .mapToObj(x -> new Position(x, finalY))
                    .noneMatch(p -> galaxies.contains(p))) {
                emptyY.add(y);
            }
        }
    }

    private void findEmptyX() {
        for (int x = 0; x < width; x++) {
            int finalX = x;
            if (IntStream.range(0, height)
                    .mapToObj(y -> new Position(finalX, y))
                    .noneMatch(p -> galaxies.contains(p))) {
                emptyX.add(x);
            }
        }
    }

    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day11_test.txt"), Charsets.UTF_8);
        var question = new Day11Part1().parseInput(lines);
        assertEquals(374, question.sumOfExpandedLengths());
    }
}
