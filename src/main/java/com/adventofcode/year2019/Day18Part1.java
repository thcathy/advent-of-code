package com.adventofcode.year2019;


import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class Day18Part1 {
    Logger log = LoggerFactory.getLogger(Day18Part1.class);
    static final String inputFile = "2019/day18_1.txt";
    static final char WALL = '#';

    public static void main(String... args) throws IOException {
        Day18Part1 solution = new Day18Part1();
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
        var keyLocations = getKeyLocations(map);
        var paths = allPathsBetweenEveryKey(map, startPosition, keyLocations);
        var routes = new PriorityQueue<>(this::compareRoute);
        var minStepGetKeySet = new HashMap<String, Integer>();
        routes.offer(new Route(startPosition, 0, Collections.emptySet()));

        while (routes.size() > 0) {
            Route route = routes.poll();
            if (route.keys.size() == keyLocations.size()) {
                return route.steps;
            } else {
                keyLocations.stream()
                        .filter(to -> validMove(route, to, paths, map))
                        .map(to -> nextRoute(route, to, paths, map))
                        .filter(r -> r.steps <= minStepGetKeySet.getOrDefault(StringUtils.join(r.keys), Integer.MAX_VALUE))
                        .forEach(r -> {
                            minStepGetKeySet.put(StringUtils.join(r.keys), r.steps);
                            routes.offer(r);
                        });
            }
        }
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

    Map<Path, Path> allPathsBetweenEveryKey(char[][] map, Position startPosition, Set<Position> keyLocations) {
        var paths = new HashMap<Path, Path>();
        for (Position to: keyLocations) {
            var path = findShortestPath(startPosition, to, map);
            paths.put(path, path);
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
