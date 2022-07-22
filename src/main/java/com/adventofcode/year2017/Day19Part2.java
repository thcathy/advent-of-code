package com.adventofcode.year2017;


import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day19Part2 {
    final static String inputFile = "2017/day19_1.txt";
    
    public static void main(String... args) throws IOException {
        Day19Part2 solution = new Day19Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = travelSteps(parseMap(lines));
        System.out.println("How many steps does the packet need to go? " + result);
    }

    int travelSteps(char[][] map) {
        int steps = 0;
        Direction direction = Direction.Down;
        int x = 0, y = findStartingPoint(map);
        while (isValidPoint(map, x, y)) {
            steps++;
            char c = map[x][y];            
            Direction nextDirection = direction;
            Point next = nextPoint(x, y, nextDirection);            
            if (!isValidPoint(map, next.x, next.y)) {                
                nextDirection = direction.turnClockwise();
                next = nextPoint(x, y, nextDirection);
                if (!isValidPoint(map, next.x, next.y)) {
                    nextDirection = direction.turnAntiClockwise();
                    next = nextPoint(x, y, nextDirection);
                }
            }

            x = next.x;
            y = next.y;
            direction = nextDirection;
        }
        return steps;
    }

    boolean isValidPoint(char[][] map, int x, int y) {
        return x < map.length && y < map[x].length && map[x][y] != ' ';
    }
    
    Point nextPoint(int x, int y, Direction d) {
        return switch (d) {
            case Down -> new Point(x+1, y);
            case Left -> new Point(x, y-1);                
            case Right -> new Point(x, y+1);                
            case Up -> new Point(x-1, y);
            default -> throw new IllegalArgumentException("unexpected value: " + d);
        };
    }

    char[][] parseMap(List<String> strings) {
        char[][] map = new char[strings.size()][strings.get(0).length()];
        for (int i = 0; i < map.length; i++) {
            map[i] = strings.get(i).toCharArray();
        }
        return map;
    }

    int findStartingPoint(char[][] map) {
        for (int i=0; i<map[0].length; i++) {
            if (map[0][i] == '|')
                return i;
        }
        throw new RuntimeException("Cannot find starting point");
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
    }

    record Point(int x, int y) {
    }
    
    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2017/day19_test.txt"), Charsets.UTF_8);
        char[][] map = parseMap(lines);
        assertEquals(5, findStartingPoint(map));
        assertEquals(38, travelSteps(map));
    }
}
