package com.adventofcode.year2019;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class Day18Part2 {
    Logger log = LoggerFactory.getLogger(Day18Part2.class);
    static final String inputFile = "2019/day18_1.txt";
    static final char WALL = '#';

    public static void main(String... args) throws IOException {
        Day18Part2 solution = new Day18Part2();
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

    Set<Position> find(char c, char[][] map) {
        var positions = new HashSet<Position>();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == c)
                    positions.add(new Position(x, y));
            }
        }
        return positions;
    }

    int minimumStepToCollectAllKeys(List<String> inputs) {
        var map = parseMap(inputs);
        var startPosition = find('@', map);
        var keyLocations = getKeyLocations(map);
        var paths = allPathsBetweenEveryKey(map, startPosition, keyLocations);
        var routes = new PriorityQueue<>(this::compareRoute);
        var minStepGetKeySet = new HashMap<String, Integer>();

        throw new RuntimeException("Cannot find route");
    }

    private int compareRoute(Route r1, Route r2) {
        if (r1.steps == r2.steps)
            return r2.keys.size() - r1.keys.size();
        else
            return r1.steps - r2.steps;
    }

    boolean validMove(Route route, Position next, Map<Path, Path> paths, char[][] map) {
        var path = paths.get(new Path(route.position, next));
        if (path == null) return false;

        var value = next.getValueFrom(map);
        if (route.keys.contains(value)) return false;

        for (char door: path.doors) {
            if (!route.keys.contains(Character.toLowerCase(door)))
                return false;
        }
        return true;
    }

    Route nextRoute(Route route, Position next, Map<Path, Path> paths, char[][] map) {
        var steps = paths.get(new Path(route.position, next)).steps;
        var keys = new HashSet<>(route.keys);
        keys.add(next.getValueFrom(map));
        return new Route(next, route.steps + steps, keys);
    }

    static class Route {
        int steps;
        Position position;
        Set<Character> keys;

        public Route(Position position, int step, Set<Character> keys) {
            this.position = position;
            this.steps = step;
            this.keys = keys;
        }
    }

    Map<Path, Path> allPathsBetweenEveryKey(char[][] map, Set<Position> startPositions, Set<Position> keyLocations) {
        var paths = new HashMap<Path, Path>();
        for (Position start : startPositions) {
            for (Position to: keyLocations) {
                var path = findShortestPath(start, to, map);
                paths.put(path, path);
            }
        }
        for (Position from: keyLocations) {
            for (Position to: keyLocations) {
                if (Objects.equals(from, to)) continue;
                var path = findShortestPath(from, to, map);
                paths.put(path, path);
            }
        }
        return paths;
    }

    Path findShortestPath(Position from, Position to, char[][] map) {
        var stepToPosition = new HashMap<Position, Integer>();
        var comeFromPosition = new HashMap<Position, Position>();
        var positions = new PriorityQueue<Position>((Comparator.comparingInt(pos -> cost(stepToPosition, pos, to))));
        stepToPosition.put(from, 0);
        positions.offer(from);

        while (positions.size() > 0) {
            Position pos = positions.poll();
            int step = stepToPosition.get(pos);
            if (Objects.equals(pos, to)) {
                var shortestPath = new Path(from, to, step);
                shortestPath.doors = passthoughDoors(pos, comeFromPosition, map);
                return shortestPath;
            } else {
                pos.allDirections().stream()
                        .filter(next -> isValidPosition(next, map) && !stepToPosition.containsKey(next))
                        .forEach(next -> {
                            stepToPosition.put(next, step + 1);
                            comeFromPosition.put(next, pos);
                            positions.offer(next);
                        });
            }
        }
        throw new RuntimeException("cannot find any path");
    }

    Set<Character> passthoughDoors(Position pos, HashMap<Position, Position> comeFromPosition, char[][] map) {
        var requiredKeys = new HashSet<Character>();
        while (comeFromPosition.containsKey(pos)) {
            pos = comeFromPosition.get(pos);
            char value = pos.getValueFrom(map);
            if (Character.isUpperCase(value)) requiredKeys.add(value);
        }
        return requiredKeys;
    }

    int cost(HashMap<Position, Integer> stepToPosition, Position from, Position to) {
        return stepToPosition.get(from) + distance(from, to);
    }

    int distance(Position a, Position b) { return Math.abs(a.x - b.x) + Math.abs(a.y - b.y); }

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

    Set<Position> getKeyLocations(char[][] map) {
        var locations = new HashSet<Position>();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (Character.isLowerCase(map[y][x]))
                    locations.add(new Position(x,y));
            }
        }
        return locations;
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

    static class Path {
        Position from;
        Position to;
        int steps;
        Set<Character> doors = new HashSet<>();

        public Path(Position from, Position to) {
            this.from = from;
            this.to = to;
        }

        public Path(Position from, Position to, int steps) {
            this(from, to);
            this.steps = steps;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Path)) return false;
            Path path = (Path) o;
            return Objects.equals(from, path.from) && Objects.equals(to, path.to);
        }

        @Override
        public int hashCode() { return Objects.hash(from, to); }
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
    public void test2() {
        String s = "########################\n" +
                "#f.D.E.e.C.b.A.@.a.B.c.#\n" +
                "######################.#\n" +
                "#d.....................#\n" +
                "########################";
        var map = parseMap(List.of(s.split("\\n")));
        var startPos = find('@', map);
        var keys = getKeyLocations(map);
        var paths = allPathsBetweenEveryKey(map, startPos, keys);
    }

    @Test
    public void test() {
        String s = "#######\n" +
                "#a.#Cd#\n" +
                "##...##\n" +
                "##.@.##\n" +
                "##...##\n" +
                "#cB#Ab#\n" +
                "#######";
        assertEquals(8, minimumStepToCollectAllKeys(List.of(s.split("\\n"))));

        String s2 = "###############\n" +
                "#d.ABC.#.....a#\n" +
                "######@#@######\n" +
                "###############\n" +
                "######@#@######\n" +
                "#b.....#.....c#\n" +
                "###############";
        assertEquals(24, minimumStepToCollectAllKeys(List.of(s2.split("\\n"))));

        String s3 = "#############\n" +
                "#DcBa.#.GhKl#\n" +
                "#.###@#@#I###\n" +
                "#e#d#####j#k#\n" +
                "###C#@#@###J#\n" +
                "#fEbA.#.FgHi#\n" +
                "#############";
        assertEquals(32, minimumStepToCollectAllKeys(List.of(s3.split("\\n"))));
    }

}
