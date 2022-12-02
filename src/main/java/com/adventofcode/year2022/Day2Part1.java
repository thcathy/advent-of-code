package com.adventofcode.year2022;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day2Part1 {
    Logger log = LoggerFactory.getLogger(Day2Part1.class);
    final static String inputFile = "2022/day2.txt";

    public static void main(String... args) throws IOException {
        Day2Part1 solution = new Day2Part1();
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
            score += scoreFromShape(shapes[1]);
            score += scoreFromOutcome(shapes[0], shapes[1]);
        }
        return score;
    }

    int scoreFromOutcome(String opponentShape, String myShape) {
        if ("A".equals(opponentShape)) {
            return switch (myShape) {
                case "X" -> 3;
                case "Y" -> 6;
                case "Z" -> 0;
                default -> 0;
            };
        } else if ("B".equals(opponentShape)) {
            return switch (myShape) {
                case "X" -> 0;
                case "Y" -> 3;
                case "Z" -> 6;
                default -> 0;
            };
        } else if ("C".equals(opponentShape)) {
            return switch (myShape) {
                case "X" -> 6;
                case "Y" -> 0;
                case "Z" -> 3;
                default -> 0;
            };
        }
        throw new RuntimeException();
    }

    int scoreFromShape(String shape) {
        return switch (shape) {
            case "X" -> 1;
            case "Y" -> 2;
            case "Z" -> 3;
            default -> 0;
        };
    }

    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2022/day2_test.txt"), Charsets.UTF_8);
        assertEquals(15, totalScore(lines));
    }
}
