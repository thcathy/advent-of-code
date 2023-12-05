package com.adventofcode.year2023;

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
    final static String inputFile = "2023/day2.txt";

    public static void main(String... args) throws IOException {
        Day2Part1 solution = new Day2Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = sumOfPossibleIds(lines, new Cubes(12, 13, 14));
        log.warn("What is the sum of the IDs of those games? {}", result);
    }

    int sumOfPossibleIds(List<String> inputs, Cubes maxCubes) {
        return inputs.stream()
                .map(this::parseGame)
                .filter(g -> lessThanMax(g.cubes, maxCubes))
                .mapToInt(g -> g.id)
                .sum();
    }

    record Game(int id, Cubes cubes) {}

    record Cubes(int red, int green, int blue) {}
    
    boolean lessThanMax(Cubes c, Cubes max) { 
        return c.green <= max.green && c.red <= max.red && c.blue <= max.blue;
    }

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
        assertEquals(8, sumOfPossibleIds(lines, new Cubes(12, 13, 14)));
    }
}
