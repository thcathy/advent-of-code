package com.adventofcode.year2023;

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
    final static String inputFile = "2023/day2.txt";

    public static void main(String... args) throws IOException {
        Day2Part2 solution = new Day2Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = sumOfPowerOfSets(lines);
        log.warn("What is the sum of the power of these sets? {}", result);
    }

    long sumOfPowerOfSets(List<String> inputs) {
        return inputs.stream()
                .map(this::parseGame)
                .mapToLong(g -> g.cubes.red * g.cubes.green * g.cubes.blue)
                .sum();
    }

    record Game(int id, Cubes cubes) {}

    record Cubes(int red, int green, int blue) {}
    
    Game parseGame(String input) {
        var part1 = input.split(": ");
        var id = Integer.parseInt(part1[0].replace("Game ", ""));
        int maxRed = 0, maxGreen = 0, maxBlue = 0;

        for (String inputs : part1[1].split(";")) {
            for (String cubes : inputs.split(",")) {
                var cubeArray = cubes.trim().split(" ");
                var cube = Integer.parseInt(cubeArray[0].trim());
                switch (cubeArray[1]) {
                    case "red":
                        maxRed = Math.max(maxRed, cube);
                        break;
                    case "green":
                        maxGreen = Math.max(maxGreen, cube);
                        break;
                    case "blue":
                        maxBlue = Math.max(maxBlue, cube);
                        break;
                }
            }
        }
        return new Game(id, new Cubes(maxRed, maxGreen, maxBlue));
    }
    
    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day2_test.txt"), Charsets.UTF_8);
        assertEquals(2286, sumOfPowerOfSets(lines));
    }
}
