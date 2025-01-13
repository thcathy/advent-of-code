package com.adventofcode.year2024;

import com.adventofcode.shared.Debug;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class Day15Part2 {
    private static final Logger log = LoggerFactory.getLogger(Day15Part2.class);
    private static final String INPUT_FILE = "2024/day15.txt";

    public static void main(String... args) throws IOException {
        new Day15Part2().run();
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

        var map = new char[mapLines.size()][mapLines.getFirst().length() * 2];
        for (var i = 0; i < mapLines.size(); i++) {
            var originalLine = mapLines.get(i).toCharArray();
            for (var j = 0; j < originalLine.length; j++) {
                char c = originalLine[j];
                switch (c) {
                    case '#' -> {
                        map[i][j * 2] = '#';
                        map[i][j * 2 + 1] = '#';
                    }
                    case 'O' -> {
                        map[i][j * 2] = '[';
                        map[i][j * 2 + 1] = ']';
                    }
                    case '.' -> {
                        map[i][j * 2] = '.';
                        map[i][j * 2 + 1] = '.';
                    }
                    case '@' -> {
                        map[i][j * 2] = '@';
                        map[i][j * 2 + 1] = '.';
                    }
                }
            }
        }

        return new Input(map, movesBuilder.toString());
    }

    void simulateRobot(char[][] map, String moves) {
        var robotPos = findRobot(map);

        for (var direction : moves.toCharArray()) {
            int[] newPos = newPosition(robotPos[0], robotPos[1], direction);
            int newX = newPos[0];
            int newY = newPos[1];

            if (isInsideMap(map, newX, newY)) {
                if (map[newX][newY] == '.') {
                    updateRobotPosition(map, robotPos, newX, newY);
                } else if (isBox(map, newX, newY) && canPushBox(map, newX, newY, direction)) {
                    pushBox(map, newX, newY, direction); // Push the box
                    updateRobotPosition(map, robotPos, newX, newY);
                }
            }
//            System.out.println("After Move: " + move);
//            Debug.printMap(map);
        }
    }

    private boolean isBox(char[][] map, int x, int y) {
        return map[x][y] == '[' || map[x][y] == ']';
    }

    private int[] newPosition(int x, int y, char direction) {
        int newX = x + (direction == '^' ? -1 : direction == 'v' ? 1 : 0);
        int newY = y + (direction == '<' ? -1 : direction == '>' ? 1 : 0);
        return new int[]{newX, newY};
    }

    private void updateRobotPosition(char[][] map, int[] robotPos, int newX, int newY) {
        map[newX][newY] = '@';
        map[robotPos[0]][robotPos[1]] = '.';
        robotPos[0] = newX;
        robotPos[1] = newY;
    }

    private boolean canPushBox(char[][] map, int x, int y, char direction) {
        int[] newPos = newPosition(x, y, direction);
        int box1NewX = newPos[0];
        int box1NewY = newPos[1];

        int box2NewX = box1NewX;
        int box2NewY = box1NewY;
        if (map[x][y] == '[') {
            box2NewY = box1NewY + 1;
        } else if (map[x][y] == ']') {
            box2NewY = box1NewY - 1;
        }

        if (isInsideMap(map, box1NewX, box1NewY) && isInsideMap(map, box2NewX, box2NewY)) {
            // For left or right movement, only check one side of the box
            if (direction == '<' || direction == '>') {
                if (direction == '<') {
                    if (map[box1NewX][box1NewY] == '.') {
                        return true;
                    } else if (isBox(map, box1NewX, box1NewY)) {
                        return canPushBox(map, box1NewX, box1NewY, direction);
                    }
                } else {
                    if (map[box2NewX][box2NewY] == '.') {
                        return true;
                    } else if (isBox(map, box2NewX, box2NewY)) {
                        return canPushBox(map, box2NewX, box2NewY, direction);
                    }
                }
            }
            else if (direction == '^' || direction == 'v') {
                if (map[box1NewX][box1NewY] == '.' && map[box2NewX][box2NewY] == '.') {
                    return true;
                }
                else if (map[box1NewX][box1NewY] == '.') {
                    return canPushBox(map, box2NewX, box2NewY, direction);
                }
                else if (map[box2NewX][box2NewY] == '.') {
                    return canPushBox(map, box1NewX, box1NewY, direction);
                } else
                    return canPushBox(map, box1NewX, box1NewY, direction) && canPushBox(map, box2NewX, box2NewY, direction);
            }
        }

        return false;
    }

    private void pushBox(char[][] map, int x, int y, char direction) {
        int[] newPos = newPosition(x, y, direction);
        int box1NewX = newPos[0];
        int box1NewY = newPos[1];
        int box2NewX = box1NewX;
        int box2NewY = box1NewY;

        if (map[x][y] == '[') {
            box2NewY = box1NewY + 1;
        } else if (map[x][y] == ']') {
            box2NewY = box1NewY - 1;
        }

        if (isBox(map, box1NewX, box1NewY)) {
            pushBox(map, box1NewX, box1NewY, direction);
        }
        if (isBox(map, box2NewX, box2NewY) && box2NewX != x) {
            pushBox(map, box2NewX, box2NewY, direction);
        }

        map[box1NewX][box1NewY] = map[x][y];
        map[box2NewX][box2NewY] = (map[x][y] == '[' ? ']' : '[');
        if (direction == '<' || direction == '>') {
            if (map[x][y] == '[') {
                map[x][y] = '.';
            } else if (map[x][y] == ']') {
                map[x][y] = '.';
            }
        } else {
            if (map[x][y] == '[') {
                map[x][y] = '.';
                map[x][y + 1] = '.';
            } else if (map[x][y] == ']') {
                map[x][y] = '.';
                map[x][y - 1] = '.';
            }
        }
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
        int sum = 0;

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == '[') sum += 100 * i + j;
            }
        }

        return sum;
    }
}
