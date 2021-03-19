package com.adventofcode.year2016;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adventofcode.year2019.Day18Part1;

public class Day24 {
    Logger log = LoggerFactory.getLogger(Day24.class);
    final static String inputFile = "2016/day24_1.txt";

    public static void main(String... args) throws IOException {
        Day24 solution = new Day24();
        solution.firstStar();
        solution.secondStar();
    }

    void firstStar() throws IOException {
//        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
//        var code = startingCode();
//        code.put('a', 7);
//        runInstructions(code, lines.stream().map(s -> s.split(" ")).collect(Collectors.toList()));
//
//        log.warn("First star - what value is left in register a? {}", code.get('a'));
    }

    void secondStar() throws IOException {

    }

    int fewestStepToVisit(List<String> inputs) {
        var map = parseMap(inputs);
        var startPosition = find('0', map);
        var locations = totalKeys(map);
        AtomicInteger minStep = new AtomicInteger(Integer.MAX_VALUE);
        var minStepGetKeySet = new HashMap<String, Integer>();
        map[startPosition.y][startPosition.x] = '.';
        var startState = new Day18Part1.State(startPosition, Collections.emptySet(), 0, Set.of(startPosition));

        return fewestStepToVisit(startState, map, totalKeys, minStepGetKeySet);
    }

    char[][] parseMap(List<String> strings) {
        char[][] map = new char[strings.size()][strings.get(0).length()];
        for (int i = 0; i < map.length; i++) {
            map[i] = strings.get(i).toCharArray();
        }
        return map;
    }

    class State {
        Position position;
        Set<Character> locations;
        int steps;
        Set<Position> visited;

        public State(Position position, Set<Character> locations, int steps, Set<Position> visited) {
            this.position = position;
            this.locations = locations;
            this.steps = steps;
            this.visited = visited;
        }
    }

    static class Position {
        int x = 0, y = 0;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        List<Position> allDirections() {
            return List.of(
                    new Position(x+1,y),
                    new Position(x-1,y),
                    new Position(x,y+1),
                    new Position(x,y-1));
        }

        @Override
        public String toString() { return MessageFormat.format("({0},{1})", x, y); }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return x == position.x && y == position.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    Position find(char c, char[][] map) {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == c)
                    return new Position(x, y);
            }
        }
        throw new RuntimeException("cannot find " + c);
    }

    Set<Position> getLocations(char[][] map) {
        var locations = new HashSet<Position>();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (Character.isDigit(map[y][x]))
                    locations.add(new Position(x,y));
            }
        }
        return locations;
    }

    @Test
    public void test() {
        var inputs = List.of(
                "###########",
                "#0.1.....2#",
                "#.#######.#",
                "#4.......3#",
                "###########");
        var map = parseMap(inputs);
        Position startPos = find('0', map);
        System.out.println(startPos);
        Set<Position> locations = getLocations(map);
        System.out.println(locations);
    }
}