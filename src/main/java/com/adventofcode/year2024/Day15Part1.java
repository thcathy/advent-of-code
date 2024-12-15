package com.adventofcode.year2024;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class Day15Part1 {
    private static final Logger log = LoggerFactory.getLogger(Day15Part1.class);
    private static final String INPUT_FILE = "2024/day15.txt";

    public static void main(String... args) throws IOException {
        new Day15Part1().run();
    }

    private void run() throws IOException {
        var input = parseInput();
        var map = input.getKey();
        var moves = input.getValue();

        simulateRobot(map, moves);
        int sumGPS = calculateGPS(map);

        log.warn("Sum of GPS coordinates after moves: {}", sumGPS);
    }

    private Pair<char[][], String> parseInput() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(INPUT_FILE), Charsets.UTF_8);
        List<String> mapLines = new ArrayList<>();
        StringBuilder movesBuilder = new StringBuilder();

        for (String line : lines) {
            if (line.startsWith("#")) {
                mapLines.add(line);
            } else {
                movesBuilder.append(line.trim());
            }
        }

        char[][] map = new char[mapLines.size()][mapLines.get(0).length()];
        for (int i = 0; i < mapLines.size(); i++) {
            map[i] = mapLines.get(i).toCharArray();
        }

        return new Pair<>(map, movesBuilder.toString());
    }

    private void simulateRobot(char[][] map, String moves) {
        int[] robotPos = findRobot(map); // Find initial robot position

        for (char move : moves.toCharArray()) {
            int newX = robotPos[0];
            int newY = robotPos[1];

            // Calculate new position based on move direction
            switch (move) {
                case '^': newX--; break;
                case 'v': newX++; break;
                case '<': newY--; break;
                case '>': newY++; break;
            }

            if (isInsideMap(map, newX, newY)) {
                if (map[newX][newY] == 'O') { // Box in the way
                    int boxNewX = newX + (newX - robotPos[0]);
                    int boxNewY = newY + (newY - robotPos[1]);

                    // Check if the box can be moved
                    if (isInsideMap(map, boxNewX, boxNewY) && map[boxNewX][boxNewY] == '.') {
                        // Move box
                        map[boxNewX][boxNewY] = 'O';
                        map[newX][newY] = '@';
                        map[robotPos[0]][robotPos[1]] = '.';
                        robotPos[0] = newX;
                        robotPos[1] = newY;
                    }
                } else if (map[newX][newY] == '.') { // Empty space
                    map[newX][newY] = '@';
                    map[robotPos[0]][robotPos[1]] = '.';
                    robotPos[0] = newX;
                    robotPos[1] = newY;
                }
            }
        }
    }

    private boolean isInsideMap(char[][] map, int x, int y) {
        return x >= 0 && y >= 0 && x < map.length && y < map[0].length && map[x][y] != '#';
    }

    private boolean canMove(char[][] map, int fromX, int fromY, int toX, int toY) {
        return toX >= 0 && toY >= 0 && toX < map.length && toY < map[0].length && map[toX][toY] != '#';
    }

    private int[] findRobot(char[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == '@') {
                    return new int[]{i, j};
                }
            }
        }
        throw new IllegalStateException("Robot not found on the map");
    }

    private int calculateGPS(char[][] map) {
        int sum = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == 'O') {
                    sum += 100 * i + j;
                }
            }
        }
        return sum;
    }

    private static final class Pair<K, V> {
        private final K key;
        private final V value;

        Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        K getKey() {
            return key;
        }

        V getValue() {
            return value;
        }
    }
}
