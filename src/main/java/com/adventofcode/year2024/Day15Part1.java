package com.adventofcode.year2024;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class Day15Part1 {
    private static final Logger log = LoggerFactory.getLogger(Day15Part1.class);
    private static final String INPUT_FILE = "2024/day15.txt";

    public static void main(String... args) throws IOException {
        new Day15Part1().run();
    }

    private void run() throws IOException {
        var input = parseInput();
        simulateRobot(input.map(), input.moves());
        log.warn("Sum of GPS coordinates after moves: {}", calculateGPS(input.map()));
    }

    private record Input(char[][] map, String moves) {}

    private Input parseInput() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(INPUT_FILE), Charsets.UTF_8);
        var mapLines = new ArrayList<String>();
        var movesBuilder = new StringBuilder();

        for (var line : lines) {
            if (line.startsWith("#")) mapLines.add(line);
            else movesBuilder.append(line.trim());
        }

        var map = new char[mapLines.size()][mapLines.getFirst().length()];
        for (var i = 0; i < mapLines.size(); i++) map[i] = mapLines.get(i).toCharArray();

        return new Input(map, movesBuilder.toString());
    }

    private void simulateRobot(char[][] map, String moves) {
        var robotPos = findRobot(map);

        for (var move : moves.toCharArray()) {
            var newX = robotPos[0] + (move == '^' ? -1 : move == 'v' ? 1 : 0);
            var newY = robotPos[1] + (move == '<' ? -1 : move == '>' ? 1 : 0);

            if (isInsideMap(map, newX, newY)) {
                if (map[newX][newY] == 'O' && pushBox(map, newX, newY, move)) {
                    map[newX][newY] = '@';
                    map[robotPos[0]][robotPos[1]] = '.';
                    robotPos[0] = newX;
                    robotPos[1] = newY;
                } else if (map[newX][newY] == '.') {
                    map[newX][newY] = '@';
                    map[robotPos[0]][robotPos[1]] = '.';
                    robotPos[0] = newX;
                    robotPos[1] = newY;
                }
            }
        }
    }

    private boolean pushBox(char[][] map, int x, int y, char direction) {
        var boxNewX = x + (direction == '^' ? -1 : direction == 'v' ? 1 : 0);
        var boxNewY = y + (direction == '<' ? -1 : direction == '>' ? 1 : 0);

        if (isInsideMap(map, boxNewX, boxNewY)) {
            if (map[boxNewX][boxNewY] == '.') {
                map[boxNewX][boxNewY] = 'O';
                return true;
            } else if (map[boxNewX][boxNewY] == 'O' && pushBox(map, boxNewX, boxNewY, direction)) {
                map[boxNewX][boxNewY] = 'O';
                return true;
            }
        }
        return false;
    }

    private boolean isInsideMap(char[][] map, int x, int y) {
        return x >= 0 && y >= 0 && x < map.length && y < map[0].length && map[x][y] != '#';
    }

    private int[] findRobot(char[][] map) {
        for (var i = 0; i < map.length; i++)
            for (var j = 0; j < map[i].length; j++)
                if (map[i][j] == '@') return new int[]{i, j};
        throw new IllegalStateException("Robot not found on the map");
    }

    private int calculateGPS(char[][] map) {
        var sum = 0;
        for (var i = 0; i < map.length; i++)
            for (var j = 0; j < map[i].length; j++)
                if (map[i][j] == 'O') sum += 100 * i + j;
        return sum;
    }
}
