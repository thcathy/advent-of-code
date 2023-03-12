package com.adventofcode.year2022;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.adventofcode.year2022.Day22Part2.Direction.*;
import static org.junit.Assert.assertEquals;

public class Day22Part2 {
    final static String inputFile = "2022/day22.txt";
    static int SIZE_OF_FACE = 50;

    public static void main(String... args) throws IOException {
        Day22Part2 solution = new Day22Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = findPassword(parseInput(lines));
        System.out.println("What is the final password? " + result);
    }

    int findPassword(Puzzle puzzle) {
        var state = puzzle.cube.startState();

        for (String path : puzzle.paths) {
            if (NumberUtils.isDigits(path)) {
                state = walk(state, Integer.parseInt(path), puzzle.cube.map);
            } else {
                state = state.turn(path);
            }
        }

        var originalPosition = puzzle.cube.map.get(state.side).get(state.position).orginalPosition;
        return 1000 * (originalPosition.y) + 4 * (originalPosition.x) + state.direction.value();
    }

    WalkingState walk(WalkingState state, int steps, Map<Integer, Map<Position, Point>> map) {
        while (steps > 0) {
            var next = state.nextValidState();
            if (map.get(next.side).get(next.position).character == '#') return state;
            steps--;
            state = next;
        }
        return state;
    }

    Puzzle parseInput(List<String> inputs) {
        var mapInput = inputs.subList(0, inputs.size() - 2);
        var map = new HashMap<Integer, Map<Position, Point>>();

        int side = 1;
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 6; x++) {
                var startX = x * SIZE_OF_FACE;
                var startY = y * SIZE_OF_FACE;
                var line = startY < mapInput.size() ? mapInput.get(startY) : "";
                if (startY < mapInput.size() && startX < line.length() && line.charAt(startX) != ' ') {
                    parseSingleSide(mapInput, map, side, startX, startY);
                    side++;
                }
            }
        }

        Pattern pattern = Pattern.compile("\\d+|R|L");
        Matcher matcher = pattern.matcher(inputs.get(inputs.size() - 1));
        var path = new ArrayList<String>();
        while(matcher.find()) {
            path.add(matcher.group());
        }

        return new Puzzle(new Cube(map), path);
    }
    private static void parseSingleSide(List<String> inputs, HashMap<Integer, Map<Position, Point>> map, int side, int startX, int startY) {
        map.put(side, new HashMap<>());
        for (int j = 0; j < SIZE_OF_FACE; j++) {
            for (int i = 0; i < SIZE_OF_FACE; i++) {
                var point = new Point(new Position(startX + i + 1, startY + j + 1), inputs.get(startY + j).charAt(startX + i));
                map.get(side).put(new Position(i, j), point);
            }
        }
    }

    record Puzzle(Cube cube, List<String> paths) {}
    
    record WalkingState(Position position, int side, Direction direction) {
        WalkingState nextValidState() {
            var next = position.next(direction);
            if (next.isValid()) return new WalkingState(next, side, direction);

            var x = position.x; var y = position.y;
            return switch (side) {
                case 1 -> switch (direction) {
                    case Up -> new WalkingState(new Position(SIZE_OF_FACE - 1 - x, 0), 2, Down);
                    case Down -> new WalkingState(new Position(x, 0), 4, Down);
                    case Left -> new WalkingState(new Position(y, 0), 3, Down);
                    case Right -> new WalkingState(new Position(SIZE_OF_FACE - 1, SIZE_OF_FACE - 1 - y), 6, Left);
                };
                case 2 -> switch (direction) {
                    case Up -> new WalkingState(new Position(SIZE_OF_FACE - 1 - x, 0), 1, Down);
                    case Down -> new WalkingState(new Position(SIZE_OF_FACE - 1 - x, SIZE_OF_FACE - 1), 5, Up);
                    case Left -> new WalkingState(new Position(SIZE_OF_FACE - 1 - y, SIZE_OF_FACE - 1), 6, Up);
                    case Right -> new WalkingState(new Position(0, y), 3, Right);
                };
                case 3 -> switch (direction) {
                    case Up -> new WalkingState(new Position(0, x), 1, Right);
                    case Down -> new WalkingState(new Position(0, SIZE_OF_FACE - 1 - x), 5, Right);
                    case Left -> new WalkingState(new Position(SIZE_OF_FACE - 1, y), 2, Left);
                    case Right -> new WalkingState(new Position(0, y), 4, Right);
                };
                case 4 -> switch (direction) {
                    case Up -> new WalkingState(new Position(x, SIZE_OF_FACE - 1), 1, Up);
                    case Down -> new WalkingState(new Position(x, 0), 5, Down);
                    case Left -> new WalkingState(new Position(SIZE_OF_FACE - 1, y), 3, Left);
                    case Right -> new WalkingState(new Position(SIZE_OF_FACE - 1 - y, 0), 6, Down);
                };
                case 5 -> switch (direction) {
                    case Up -> new WalkingState(new Position(x, SIZE_OF_FACE - 1), 4, Up);
                    case Down -> new WalkingState(new Position(SIZE_OF_FACE - 1 - x, SIZE_OF_FACE - 1), 2, Up);
                    case Left -> new WalkingState(new Position(SIZE_OF_FACE - 1 - y, SIZE_OF_FACE - 1), 3, Up);
                    case Right -> new WalkingState(new Position(0, y), 6, Right);
                };
                case 6 -> switch (direction) {
                    case Up -> new WalkingState(new Position(SIZE_OF_FACE - 1, SIZE_OF_FACE - 1 - x), 4, Left);
                    case Down -> new WalkingState(new Position(0, SIZE_OF_FACE - 1 - x), 2, Right);
                    case Left -> new WalkingState(new Position(SIZE_OF_FACE - 1, y), 5, Left);
                    case Right -> new WalkingState(new Position(SIZE_OF_FACE - 1, SIZE_OF_FACE - 1 - y), 1, Left);
                };
                default -> throw new IllegalStateException("Unexpected value: " + side);
            };
        }

        public WalkingState turn(String path) {
            return new WalkingState(position, side, ("R".equals(path)) ? direction.turnClockwise() : direction.turnAntiClockwise());
        }
    }

    record Cube(Map<Integer, Map<Position, Point>> map) {
        WalkingState startState() {
            for (int x = 0; x < SIZE_OF_FACE; x++) {
                var pos = new Position(x, 0);
                if (map.get(1).get(pos).character == '.') return new WalkingState(pos, 1, Right);
            }
            throw new RuntimeException();
        }
    }

    record Point(Position orginalPosition, char character) {}

    record Position(int x, int y) {
        Position next(Direction direction) {
            return switch (direction) {
                case Up -> new Position(x, y - 1);
                case Down -> new Position(x, y + 1);
                case Left -> new Position(x - 1, y);
                case Right -> new Position(x + 1, y);
            };
        }

        boolean isValid() {
            return y > -1 && x > -1 && SIZE_OF_FACE > y && SIZE_OF_FACE > x;
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
        SIZE_OF_FACE = 4;
        var puzzle = parseInput(lines);

        assertEquals(6, puzzle.cube.map.size());
        assertEquals('#', puzzle.cube.map.get(1).get(new Position(3, 0)).character);
        assertEquals(new Position(12, 1), puzzle.cube.map.get(1).get(new Position(3, 0)).orginalPosition);
        assertEquals(13, puzzle.paths.size());
        assertEquals("5", puzzle.paths.get(12));
        assertEquals(new WalkingState(new Position(0, 0), 1, Right), puzzle.cube.startState());

        assertEquals(new WalkingState(new Position(2, 0), 2, Down), walk(new WalkingState(new Position(1, 0), 1, Up), 1, puzzle.cube.map));
        assertEquals(new WalkingState(new Position(0, 0), 4, Down), walk(new WalkingState(new Position(0, 3), 1, Down), 1, puzzle.cube.map));
        assertEquals(new WalkingState(new Position(1, 0), 3, Down), walk(new WalkingState(new Position(0, 1), 1, Left), 1, puzzle.cube.map));
        assertEquals(new WalkingState(new Position(3, 3), 6, Left), walk(new WalkingState(new Position(3, 0), 1, Right), 1, puzzle.cube.map));

        assertEquals(new WalkingState(new Position(2, 0), 1, Down), walk(new WalkingState(new Position(1, 0), 2, Up), 1, puzzle.cube.map));
        assertEquals(new WalkingState(new Position(3, 3), 5, Up), walk(new WalkingState(new Position(0, 3), 2, Down), 1, puzzle.cube.map));
        assertEquals(new WalkingState(new Position(1, 3), 6, Up), walk(new WalkingState(new Position(0, 2), 2, Left), 1, puzzle.cube.map));
        assertEquals(new WalkingState(new Position(0, 0), 3, Right), walk(new WalkingState(new Position(3, 0), 2, Right), 1, puzzle.cube.map));

        assertEquals(new WalkingState(new Position(0, 1), 1, Right), walk(new WalkingState(new Position(1, 0), 3, Up), 1, puzzle.cube.map));
        assertEquals(new WalkingState(new Position(0, 3), 5, Right), walk(new WalkingState(new Position(0, 3), 3, Down), 1, puzzle.cube.map));
        assertEquals(new WalkingState(new Position(3, 2), 2, Left), walk(new WalkingState(new Position(0, 2), 3, Left), 1, puzzle.cube.map));
        assertEquals(new WalkingState(new Position(0, 0), 4, Right), walk(new WalkingState(new Position(3, 0), 3, Right), 1, puzzle.cube.map));

        assertEquals(new WalkingState(new Position(1, 3), 1, Up), walk(new WalkingState(new Position(1, 0), 4, Up), 1, puzzle.cube.map));
        assertEquals(new WalkingState(new Position(0, 0), 5, Down), walk(new WalkingState(new Position(0, 3), 4, Down), 1, puzzle.cube.map));
        assertEquals(new WalkingState(new Position(3, 1), 3, Left), walk(new WalkingState(new Position(0, 1), 4, Left), 1, puzzle.cube.map));
        assertEquals(new WalkingState(new Position(3, 0), 6, Down), walk(new WalkingState(new Position(3, 0), 4, Right), 1, puzzle.cube.map));

        assertEquals(new WalkingState(new Position(1, 3), 4, Up), walk(new WalkingState(new Position(1, 0), 5, Up), 1, puzzle.cube.map));
        assertEquals(new WalkingState(new Position(3, 3), 2, Up), walk(new WalkingState(new Position(0, 3), 5, Down), 1, puzzle.cube.map));
        assertEquals(new WalkingState(new Position(2, 3), 3, Up), walk(new WalkingState(new Position(0, 1), 5, Left), 1, puzzle.cube.map));
        assertEquals(new WalkingState(new Position(0, 0), 6, Right), walk(new WalkingState(new Position(3, 0), 5, Right), 1, puzzle.cube.map));

        assertEquals(new WalkingState(new Position(3, 2), 4, Left), walk(new WalkingState(new Position(1, 0), 6, Up), 1, puzzle.cube.map));
        assertEquals(new WalkingState(new Position(0, 3), 2, Right), walk(new WalkingState(new Position(0, 3), 6, Down), 1, puzzle.cube.map));
        assertEquals(new WalkingState(new Position(3, 1), 5, Left), walk(new WalkingState(new Position(0, 1), 6, Left), 1, puzzle.cube.map));
        assertEquals(new WalkingState(new Position(3, 3), 1, Left), walk(new WalkingState(new Position(3, 0), 6, Right), 1, puzzle.cube.map));

        assertEquals(5031, findPassword(puzzle));
    }

}
