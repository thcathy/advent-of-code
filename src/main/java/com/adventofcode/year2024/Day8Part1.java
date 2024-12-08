package com.adventofcode.year2024;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class Day8Part1 {
    private static final Logger log = LoggerFactory.getLogger(Day8Part1.class);
    private static final String INPUT_FILE = "2024/day8.txt";

    public static void main(String... args) throws IOException {
        new Day8Part1().run();
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
            for (int i = 0; i < positions.size(); i++) {
                for (int j = i + 1; j < positions.size(); j++) {
                    Position p1 = positions.get(i);
                    Position p2 = positions.get(j);
                    addAntinodePositions(p1, p2, antinodeSet, map);
                }
            }
        }

        return antinodeSet;
    }

    private void addAntinodePositions(Position p1, Position p2, Set<String> antinodeSet, char[][] map) {
        int dx = p2.x - p1.x;
        int dy = p2.y - p1.y;

        Position antinode1 = new Position(p1.x - dx, p1.y - dy);
        if (isWithinBounds(antinode1, map)) {
            antinodeSet.add(antinode1.x + "," + antinode1.y);
        }

        Position antinode2 = new Position(p2.x + dx, p2.y + dy);
        if (isWithinBounds(antinode2, map)) {
            antinodeSet.add(antinode2.x + "," + antinode2.y);
        }
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

    private boolean isWithinBounds(Position p, char[][] map) {
        return p.x >= 0 && p.x < map[0].length && p.y >= 0 && p.y < map.length;
    }

    private record Position(int x, int y) {}
}
