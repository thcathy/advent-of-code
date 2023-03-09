package com.adventofcode.year2022;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.adventofcode.year2022.Day22Part1.Direction.Right;
import static org.junit.Assert.assertEquals;

public class Day22Part1 {
    final static String inputFile = "2022/day22.txt";

    public static void main(String... args) throws IOException {
        Day22Part1 solution = new Day22Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = findPassword(parseInput(lines));
        System.out.println("What is the final password? " + result);
    }

    int findPassword(Puzzle puzzle) {
        Direction direction = Right;
        var position = startPosition(puzzle.map);

        for (String path : puzzle.paths) {
            if (NumberUtils.isDigits(path)) {
                position = walk(position, direction, Integer.parseInt(path), puzzle.map);
            } else {
                direction = ("R".equals(path)) ? direction.turnClockwise() : direction.turnAntiClockwise();
            }
        }

        return 1000 * (position.y + 1) + 4 * (position.x + 1) + direction.value();
    }

    Position walk(Position position, Direction direction, int steps, char[][] map) {
        while (steps > 0) {
            var next = nextValidPosition(position, direction,map);
            if (map[next.y][next.x] == '#') return position;
            position = next;
            steps--;
        }
        return position;
    }

    Position nextValidPosition(Position position, Direction direction, char[][] map) {
        position = position.next(direction);
        if (position.isValid(map)) return position;

        return switch (direction) {
            case Up -> {
                for (int y = map.length - 1; y >= 0; y--) {
                    if (map[y][position.x] != ' ') yield new Position(position.x, y);
                }
                throw new RuntimeException();
            }
            case Down -> {
                for (int y = 0; y < map[0].length; y++) {
                    if (map[y][position.x] != ' ') yield new Position(position.x, y);
                }
                throw new RuntimeException();
            }
            case Left -> {
                for (int x = map[0].length - 1; x >= 0; x--) {
                    if (map[position.y][x] != ' ') yield new Position(x, position.y);
                }
                throw new RuntimeException();
            }
            case Right -> {
                for (int x = 0; x < map[0].length; x++) {
                    if (map[position.y][x] != ' ') yield new Position(x, position.y);
                }
                throw new RuntimeException();
            }
        };
    }

    Puzzle parseInput(List<String> inputs) {
        var mapInput = inputs.subList(0, inputs.size() - 2);
        var width = mapInput.stream().mapToInt(String::length).max().orElseThrow();
        var map = new char[mapInput.size()][width];
        for (int y = 0; y < mapInput.size(); y++) {
            var line = mapInput.get(y);
            for (int x = 0; x < width; x++) {
                map[y][x] = (x >= line.length()) ? ' ' : line.charAt(x);
            }
        }

        Pattern pattern = Pattern.compile("\\d+|R|L");
        Matcher matcher = pattern.matcher(inputs.get(inputs.size() - 1));
        var path = new ArrayList<String>();
        while(matcher.find()) {
            path.add(matcher.group());
        }

        return new Puzzle(map, path);
    }

    Position startPosition(char[][] map) {
        for (int x = 0; x < map[0].length; x++) {
            if (map[0][x] == '.') return new Position(x, 0);
        }
        throw new RuntimeException();
    }

    record Puzzle(char[][] map, List<String> paths) {}

    record Position(int x, int y) {
        Position next(Direction direction) {
            return switch (direction) {
                case Up -> new Position(x, y - 1);
                case Down -> new Position(x, y + 1);
                case Left -> new Position(x - 1, y);
                case Right -> new Position(x + 1, y);
            };
        }

        boolean isValid(char[][] map) {
            return y > -1 && x > -1 && map.length > y && map[y].length > x && map[y][x] != ' ';
        }
    }

    enum Direction {
        Up, Down, Left, Right;

        Direction turnClockwise() {
            return switch (this) {
                case Down -> Left;
                case Left -> Up;
                case Right -> Down;
                case Up -> Right;
            };
        }

        Direction turnAntiClockwise() {
            return switch (this) {
                case Down -> Right;
                case Left -> Down;
                case Right -> Up;
                case Up -> Left;
            };
        }

        int value() {
            return switch (this) {
                case Right -> 0;
                case Down -> 1;
                case Left -> 2;
                case Up -> 3;
            };
        }
    }

    @Test
    public void unitTest() throws Exception {
        var lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day22_test.txt").toURI()));
        var puzzle = parseInput(lines);

        assertEquals(16, puzzle.map[0].length);
        assertEquals(12, puzzle.map.length);
        assertEquals('#', puzzle.map[0][11]);
        assertEquals(13, puzzle.paths.size());
        assertEquals("5", puzzle.paths.get(12));
        assertEquals(new Position(8, 0), startPosition(puzzle.map));

        assertEquals(6032, findPassword(puzzle));
    }

}
