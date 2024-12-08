package com.adventofcode.year2024;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class Day8Part2 {
    private static final Logger log = LoggerFactory.getLogger(Day8Part2.class);
    private static final String INPUT_FILE = "2024/day8.txt";

    public static void main(String... args) throws IOException {
        new Day8Part2().run();
    }

    private void run() throws IOException {
        char[][] map = parseInput();
        Set<String> antinodeLocations = calculateAntinodeLocations(map);
        log.warn("Total unique antinode locations: {}", antinodeLocations.size());
    }

    private char[][] parseInput() throws IOException {
        List<String> lines = Resources.readLines(ClassLoader.getSystemResource(INPUT_FILE), Charsets.UTF_8);
        return lines.stream()
                .map(String::toCharArray)
                .toArray(char[][]::new);
    }

    private Set<String> calculateAntinodeLocations(char[][] map) {
        Map<Character, List<Position>> antennas = collectAntennaPositions(map);
        Set<String> antinodeSet = new HashSet<>();

        for (var positions : antennas.values()) {
            for (Position pos : positions) {
                antinodeSet.add(pos.x + "," + pos.y);
            }

            for (int i = 0; i < positions.size(); i++) {
                for (int j = i + 1; j < positions.size(); j++) {
                    Position p1 = positions.get(i);
                    Position p2 = positions.get(j);
                    addAlignedAntinodePositions(p1, p2, antinodeSet, map);
                }
            }
        }

        return antinodeSet;
    }

    private void addAlignedAntinodePositions(Position p1, Position p2, Set<String> antinodeSet, char[][] map) {
        int dx = p2.x - p1.x;
        int dy = p2.y - p1.y;

        // Extend in the negative direction from p1
        int k = 1;
        while (true) {
            int newX = p1.x - k * dx;
            int newY = p1.y - k * dy;
            if (isInBound(map, newX, newY)) break; // Exit if out of bounds

            antinodeSet.add(newX + "," + newY);
            k++;
        }

        // Extend in the positive direction from p2
        k = 1;
        while (true) {
            int newX = p2.x + k * dx;
            int newY = p2.y + k * dy;
            if (isInBound(map, newX, newY)) break; // Exit if out of bounds

            antinodeSet.add(newX + "," + newY);
            k++;
        }
    }

    private static boolean isInBound(char[][] map, int newX, int newY) {
        return newX < 0 || newX >= map[0].length || newY < 0 || newY >= map.length;
    }

    private Map<Character, List<Position>> collectAntennaPositions(char[][] map) {
        Map<Character, List<Position>> antennas = new HashMap<>();

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                char ch = map[y][x];
                if (Character.isLetterOrDigit(ch)) {
                    antennas.computeIfAbsent(ch, k -> new ArrayList<>()).add(new Position(x, y));
                }
            }
        }

        return antennas;
    }

    private record Position(int x, int y) {}
}
