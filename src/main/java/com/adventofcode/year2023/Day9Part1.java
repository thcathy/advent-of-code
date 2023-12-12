package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Day9Part1 {
    Logger log = LoggerFactory.getLogger(Day9Part1.class);
    final static String inputFile = "2023/day9.txt";

    public static void main(String... args) throws IOException {
        Day9Part1 solution = new Day9Part1();
        solution.run();
    }
    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var puzzle = Puzzle.parse(lines);
        var result = puzzle.sumOfExtrapolatedValues();
        log.warn("What is the sum of these extrapolated values? {}", result);
    }

    record Puzzle(List<List<Integer>> histories) {
        static Puzzle parse(List<String> inputs) {
            var histories = inputs.stream().map(s ->
                                Arrays.stream(s.split(" ")).map(Integer::parseInt).toList())
                            .toList();
            return new Puzzle(histories);
        }
        int sumOfExtrapolatedValues() {
            return histories.stream().mapToInt(this::predictionValue).sum();
        }

        int predictionValue(List<Integer> history) {
            if (history.stream().allMatch(v -> v == 0))
                return 0;

            var differences = new ArrayList<Integer>();
            for (int i = 1; i < history.size(); i++) {
                differences.add(history.get(i) - history.get(i - 1));
            }
            return predictionValue(differences) + history.getLast();
        }
    }
    
    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day9_test.txt"), Charsets.UTF_8);
        var puzzle = Puzzle.parse(lines);
        assertEquals(114, puzzle.sumOfExtrapolatedValues());
    }
}
