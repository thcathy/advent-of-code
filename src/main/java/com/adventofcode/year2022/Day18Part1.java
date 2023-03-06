package com.adventofcode.year2022;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class Day18Part1 {
    final static String inputFile = "2022/day18.txt";

    public static void main(String... args) throws IOException {
        Day18Part1 solution = new Day18Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = totalExposedSides(parseInput(lines));
        System.out.println("What is the surface area of your scanned lava droplet? " + result);
    }

    int totalExposedSides(Set<Position> cubes) {
        return cubes.stream().mapToInt(c -> exposedSides(c, cubes)).sum();
    }

    int exposedSides(Position cube, Set<Position> cubes) {
        int exposedSides = 0;
        if (!cubes.contains(new Position(cube.x + 1, cube.y, cube.z))) exposedSides++;
        if (!cubes.contains(new Position(cube.x - 1, cube.y, cube.z))) exposedSides++;
        if (!cubes.contains(new Position(cube.x, cube.y + 1, cube.z))) exposedSides++;
        if (!cubes.contains(new Position(cube.x, cube.y - 1, cube.z))) exposedSides++;
        if (!cubes.contains(new Position(cube.x, cube.y, cube.z + 1))) exposedSides++;
        if (!cubes.contains(new Position(cube.x, cube.y, cube.z - 1))) exposedSides++;
        return exposedSides;
    }

    Set<Position> parseInput(List<String> inputs) {
        return inputs.stream().map(i -> {
            var pos = i.split(",");
            return new Position(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]), Integer.parseInt(pos[2]));
        }).collect(Collectors.toSet());
    }

    record Position(int x, int y, int z) {}

    @Test
    public void unitTest() throws Exception {
        var lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day18_test.txt").toURI()));
        assertEquals(64, totalExposedSides(parseInput(lines)));
    }

}
