package com.adventofcode.year2022;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class Day18Part2 {
    final static String inputFile = "2022/day18.txt";

    public static void main(String... args) throws IOException {
        Day18Part2 solution = new Day18Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = exteriorSides(parseInput(lines));
        System.out.println("What is the exterior surface area of your scanned lava droplet? " + result);
    }

    int exteriorSides(Set<Position> cubes) {
        int exteriorSides = 0;
        var seen = new HashSet<Position>();
        var min = min(cubes);
        var max = max(cubes);
        var queue = new LinkedList<Position>();
        queue.add(max);
        while (!queue.isEmpty()) {
            var pos = queue.poll();
            if (cubes.contains(pos)) {
                exteriorSides++;
            } else if (!seen.contains(pos)) {
                seen.add(pos);
                pos.getNeighbors()
                        .filter(p -> min.x <= p.x && p.x <= max.x
                                && min.y <= p.y && p.y <= max.y
                                && min.z <= p.z && p.z <= max.z)
                        .forEach(queue::add);
            }
        }
        return exteriorSides;
    }

    Position max(Set<Position> cubes) {
        int maxX = 0, maxY = 0, maxZ = 0;
        for (Position pos : cubes) {
            maxX = Math.max(maxX, pos.x);
            maxY = Math.max(maxY, pos.y);
            maxZ = Math.max(maxZ, pos.z);
        }
        return new Position(maxX + 1, maxY + 1, maxZ + 1);
    }

    Position min(Set<Position> cubes) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        for (Position pos : cubes) {
            minX = Math.min(minX, pos.x);
            minY = Math.min(minY, pos.y);
            minZ = Math.min(minZ, pos.z);
        }
        return new Position(minX - 1, minY - 1, minZ - 1);
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

    record Position(int x, int y, int z) {
        Stream<Position> getNeighbors() {
            return Stream.of(
                    new Position(x + 1, y, z), new Position(x, y + 1, z), new Position(x, y, z + 1),
                    new Position(x - 1, y, z), new Position(x, y - 1, z), new Position(x, y, z - 1)
            );
        }
    }

    @Test
    public void unitTest() throws Exception {
        var lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day18_test.txt").toURI()));
        assertEquals(64, totalExposedSides(parseInput(lines)));
        assertEquals(58, exteriorSides(parseInput(lines)));
    }

}
