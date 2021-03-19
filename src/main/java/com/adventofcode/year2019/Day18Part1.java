package com.adventofcode.year2019;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Day18Part1 {
    Logger log = LoggerFactory.getLogger(Day18Part1.class);
    final static String inputFile = "2019/day18_1.txt";

    public static void main(String... args) throws IOException {
        Day18Part1 solution = new Day18Part1();
    }

    char[][] toMap(List<String> strings) {
        char[][] map = new char[strings.size()][strings.get(0).length()];
        for (int i = 0; i < map.length; i++) {
            map[i] = strings.get(i).toCharArray();
        }
        return map;
    }

    @Deprecated
    void printMap(char[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                System.out.print(map[i][j]);
            }
            System.out.println();
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

    int totalKeys(char[][] map) {
        int total = 0;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (Character.isLowerCase(map[y][x]))
                    total++;
            }
        }
        return total;
    }

    int minimumStepToCollectAllKeys(char[][] map) {
        var startPosition = find('@', map);
        var totalKeys = totalKeys(map);
        AtomicInteger minStep = new AtomicInteger(Integer.MAX_VALUE);
        var minStepGetKeySet = new HashMap<String, Integer>();
        map[startPosition.y][startPosition.x] = '.';
        var startState = new State(startPosition, Collections.emptySet(), 0, Set.of(startPosition));

        return minimumStepToCollectAllKeys(startState, map, totalKeys, minStepGetKeySet);
    }

    int minimumStepToCollectAllKeys(State state, char[][] map, int totalKeys, HashMap<String, Integer> minStepGetKeySet) {
        if (state.keys.size() == totalKeys) {
            //if (state.steps < minStep.get()) minStep.set(state.steps);
            return state.steps;
        }
        //if (state.steps >= minStep.get()) return Integer.MAX_VALUE;

        return state.position.allDirections().stream()
                .map(nextPosition -> moveIfValid(state, nextPosition, map, minStepGetKeySet))
                .flatMap(Optional::stream)
                .mapToInt(s -> minimumStepToCollectAllKeys(s, map, totalKeys, minStepGetKeySet))
                .min().orElse(Integer.MAX_VALUE);
    }

    Optional<State> moveIfValid(State state, Position next, char[][] map, HashMap<String, Integer> minStepGetKeySet) {
        if (next.x < 0 || next.y < 0) return Optional.empty();
        if (next.x >= map[0].length || next.y >= map.length) return Optional.empty();
        if (state.visited.contains(next)) return Optional.empty();
        char mapValue = map[next.y][next.x];
        if (mapValue == '#') return Optional.empty();
        if (Character.isUpperCase(mapValue) && !state.keys.contains(Character.toLowerCase(mapValue))) return Optional.empty();

        var keys = state.keys;
        var steps = state.steps + 1;
        Set<Position> visited;
        if (isGetNewKey(state, mapValue)) {
            String keySetString = addKeyToString(state.keys, mapValue);
            if (steps <= minStepGetKeySet.getOrDefault(keySetString, Integer.MAX_VALUE)) {
                minStepGetKeySet.put(keySetString, steps);
            } else {
                return Optional.empty();
            }

            keys = new HashSet<>();
            keys.addAll(state.keys);
            keys.add(mapValue);
            visited = Set.of(next);
        } else {
            visited = new HashSet<>();
            visited.addAll(state.visited);
            visited.add(next);
        }

        return Optional.of(new State(next, keys, steps, visited));
    }

    private String addKeyToString(Set<Character> keys, char mapValue) {
        var chars = new char[keys.size() + 1];
        var i = 0;
        for (char c : keys) {
            chars[i] = c;
            i++;
        }
        chars[i] = mapValue;
        Arrays.sort(chars);
        return new String(chars);
    }

    private boolean isGetNewKey(State state, char mapValue) {
        return Character.isLowerCase(mapValue) && !state.keys.contains(mapValue);
    }

    class State {
        Position position;
        Set<Character> keys;
        int steps;
        Set<Position> visited;

        public State(Position position, Set<Character> keys, int steps, Set<Position> visited) {
            this.position = position;
            this.keys = keys;
            this.steps = steps;
            this.visited = visited;
        }
    }

    class Position {
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
            return new EqualsBuilder()
                    .append(x, position.x)
                    .append(y, position.y)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(x)
                    .append(y)
                    .toHashCode();
        }
    }

    @Test
    public void test() {
        String s = "########################\n" +
                "#f.D.E.e.C.b.A.@.a.B.c.#\n" +
                "######################.#\n" +
                "#d.....................#\n" +
                "########################";
        Assert.assertEquals(86, minimumStepToCollectAllKeys(toMap(List.of(s.split("\\n")))));

        String s1 = "#########\n" +
                "#b.A.@.a#\n" +
                "#########";
        Assert.assertEquals(8, minimumStepToCollectAllKeys(toMap(List.of(s1.split("\\n")))));

        String s2 = "########################\n" +
                "#...............b.C.D.f#\n" +
                "#.######################\n" +
                "#.....@.a.B.c.d.A.e.F.g#\n" +
                "########################";
        Assert.assertEquals(132, minimumStepToCollectAllKeys(toMap(List.of(s2.split("\\n")))));

        String s3 = "#################\n" +
                "#i.G..c...e..H.p#\n" +
                "########.########\n" +
                "#j.A..b...f..D.o#\n" +
                "########@########\n" +
                "#k.E..a...g..B.n#\n" +
                "########.########\n" +
                "#l.F..d...h..C.m#\n" +
                "#################";
        Assert.assertEquals(136, minimumStepToCollectAllKeys(toMap(List.of(s3.split("\\n")))));
    }

}
