package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class Day21Part1 {
    Logger log = LoggerFactory.getLogger(Day21Part1.class);
    final static String inputFile = "2023/day21.txt";

    public static void main(String... args) throws IOException {
        Day21Part1 solution = new Day21Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var puzzle = new Day21Part1().parseInput(lines);
        var result = possibleReach(puzzle, 64);
        log.warn("how many garden plots could the Elf reach in exactly 64 steps? {}", result);
    }

    long possibleReach(Puzzle puzzle, int maxStep) {
        var knownSteps = new HashMap<String, Set<Position>>();
        return findMaxSteps(puzzle, puzzle.start, 0, maxStep, knownSteps).size();
    }

    Set<Position> findMaxSteps(Puzzle puzzle, Position position, int step, int maxStep, HashMap<String, Set<Position>> knownSteps) {
        var key = getHashKey(position, step);
        if (knownSteps.containsKey(key))
            return knownSteps.get(key);

        Set<Position> result = new HashSet<>();
        if (step == maxStep) {
            result = Set.of(position);
        } else {
            position.neighbors()
                    .filter(puzzle::isValid)
                    .map(p -> findMaxSteps(puzzle, p, step + 1, maxStep, knownSteps))
                    .forEach(result::addAll);
        }
        knownSteps.put(key, result);
        return result;
    }

    String getHashKey(Position position, int step) {
        return position.x + "," + position.y + "," + step;
    }

    //region Data Objects

    record Puzzle(Map<Position, Character> map, int width, int height, Position start) {
        boolean isValid(Position p) {
            return p.x >= 0 && p.y >= 0 &&
                    p.x < width && p.y < height &&
                    map.getOrDefault(p, '.') != '#';
        }
    }

    record Position(int x, int y) {
        Stream<Position> neighbors() {
            return Stream.of(
                    new Position(x-1, y),
                    new Position(x+1, y),
                    new Position(x, y-1),
                    new Position(x, y+1)
            );
        }
    }
    
    //endregion

    //region Input Parsing

    Puzzle parseInput(List<String> inputs) {
        var height = inputs.size();
        var width = inputs.getFirst().length();
        var map = new HashMap<Position, Character>();
        for (int y = 0; y < height; y++) {
            var line = inputs.get(y);
            for (int x = 0; x < width; x++) {
                map.put(new Position(x, y), line.charAt(x));
            }
        }

        var start = map.entrySet().stream().filter(e -> e.getValue() == 'S').findFirst().get().getKey();

        return new Puzzle(map, width, height, start);
    }

    //endregion

    @Test
    public void unitTest() throws IOException {        
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day21_test.txt"), Charsets.UTF_8);
        var puzzle = new Day21Part1().parseInput(lines);
        var result = possibleReach(puzzle, 6);
        Assert.assertEquals(16, result);
    }
}
