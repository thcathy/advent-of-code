package com.adventofcode.year2019;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

import static java.lang.Character.*;
import static org.junit.Assert.assertEquals;

public class Day18Part1b {
    Logger log = LoggerFactory.getLogger(Day18Part1b.class);
    static final String inputFile = "2019/day18_1.txt";
    static final char WALL = '#';

    public static void main(String... args) throws IOException {
        Day18Part1b solution = new Day18Part1b();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = minimumStepToCollectAllKeys(lines);
        log.warn("How many steps is the shortest path that collects all of the keys? {}", result);
    }

    char[][] parseMap(List<String> strings) {
        char[][] map = new char[strings.size()][strings.get(0).length()];
        for (int i = 0; i < map.length; i++) {
            map[i] = strings.get(i).toCharArray();
        }
        return map;
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

    int minimumStepToCollectAllKeys(List<String> inputs) {
        var map = parseMap(inputs);
        var startPosition = find('@', map);
        var totalKeys = findTotalKeys(map);
        var states = new ArrayDeque<State>();
        var visitedState = new HashSet<String>();

        states.offer(new State(startPosition, Collections.emptySet(), 0));

        while (states.size() > 0) {
            var state = states.pop();
            if (!visitedState.add(state.identifier))
                continue;

            if (visitedState.size() % 100000 == 0) System.out.println(visitedState.size());

            if (state.keys.size() == totalKeys) {
                return state.steps;
            }

            for (Position nextPosition : state.position.allDirections()) {
                if (!isValidPosition(nextPosition, map))
                    continue;
                var mapValue = nextPosition.getValueFrom(map);
                if (isUpperCase(mapValue) && !state.keys.contains(toLowerCase(mapValue)))
                    continue;
                var keys = state.keys;
                if (isLowerCase(mapValue)) {
                    keys = new HashSet<>(keys);
                    keys.add(mapValue);
                }
                states.add(new State(nextPosition, keys, state.steps+1));
            }
        }
        throw new RuntimeException("Cannot find route");
    }

    boolean isValidPosition(Position pos, char[][] map) {
        if (pos.x < 0 || pos.y < 0) {
            return false;
        } else if (pos.x >= map[0].length || pos.y >= map.length) {
            return false;
        } else if (map[pos.y][pos.x] == WALL) {
            return false;
        }
        return true;
    }

    int findTotalKeys(char[][] map) {
        int keys = 0;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (Character.isLowerCase(map[y][x]))
                    keys++;
            }
        }
        return keys;
    }

    class State {
        Position position;
        Set<Character> keys;
        int steps;
        String identifier;

        public State(Position position, Set<Character> keys, int steps) {
            this.position = position;
            this.keys = keys;
            this.steps = steps;
            this.identifier = position.toString() + StringUtils.join(keys);
        }
    }

    static class Position {
        int x = 0, y = 0;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        char getValueFrom(char[][] map) { return map[y][x]; }

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

    @Test
    public void test() {
        String s = "########################\n" +
                "#f.D.E.e.C.b.A.@.a.B.c.#\n" +
                "######################.#\n" +
                "#d.....................#\n" +
                "########################";
        assertEquals(86, minimumStepToCollectAllKeys(List.of(s.split("\\n"))));

        String s1 = "#########\n" +
                "#b.A.@.a#\n" +
                "#########";
        assertEquals(8, minimumStepToCollectAllKeys(List.of(s1.split("\\n"))));

        String s2 = "########################\n" +
                "#...............b.C.D.f#\n" +
                "#.######################\n" +
                "#.....@.a.B.c.d.A.e.F.g#\n" +
                "########################";
        assertEquals(132, minimumStepToCollectAllKeys(List.of(s2.split("\\n"))));

        String s4 = "########################\n" +
                "#@..............ac.GI.b#\n" +
                "###d#e#f################\n" +
                "###A#B#C################\n" +
                "###g#h#i################\n" +
                "########################";
        assertEquals(81, minimumStepToCollectAllKeys(List.of(s4.split("\\n"))));

        String s3 = "#################\n" +
                "#i.G..c...e..H.p#\n" +
                "########.########\n" +
                "#j.A..b...f..D.o#\n" +
                "########@########\n" +
                "#k.E..a...g..B.n#\n" +
                "########.########\n" +
                "#l.F..d...h..C.m#\n" +
                "#################";
        assertEquals(136, minimumStepToCollectAllKeys(List.of(s3.split("\\n"))));
    }

}
