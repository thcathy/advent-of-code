package com.adventofcode.year2022;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day2Part2 {
    Logger log = LoggerFactory.getLogger(Day2Part2.class);
    final static String inputFile = "2022/day2.txt";

    public static void main(String... args) throws IOException {
        Day2Part2 solution = new Day2Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = totalScore(lines);
        log.warn("What would your total score be if everything goes exactly according to your strategy guide? {}", result);
    }

    public int totalScore(List<String> inputs) {
        int score = 0;
        for (String input : inputs) {
            String[] shapes = input.split(" ");
            score += scoreFromOutcome(shapes[1]);
            score += scoreFromShape(shapes[0], shapes[1]);
        }
        return score;
    }

    int scoreFromShape(String opponentShape, String outcome) {
        if ("X".equals(outcome)) {
            return switch (opponentShape) {
                case "A" -> 3;
                case "B" -> 1;
                case "C" -> 2;
                default -> 0;
            };
        } else if ("Y".equals(outcome)) {
            return switch (opponentShape) {
                case "A" -> 1;
                case "B" -> 2;
                case "C" -> 3;
                default -> 0;
            };
        } else if ("Z".equals(outcome)) {
            return switch (opponentShape) {
                case "A" -> 2;
                case "B" -> 3;
                case "C" -> 1;
                default -> 0;
            };
        }
        throw new RuntimeException();
    }

    int scoreFromOutcome(String shape) {
        return switch (shape) {
            case "X" -> 0;
            case "Y" -> 3;
            case "Z" -> 6;
            default -> 0;
        };
    }

    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2022/day2_test.txt"), Charsets.UTF_8);
        assertEquals(12, totalScore(lines));
    }
}
