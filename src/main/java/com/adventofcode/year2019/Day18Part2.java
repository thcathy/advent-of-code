package com.adventofcode.year2019;


import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.assertEquals;

public class Day18Part2 {
    Logger log = LoggerFactory.getLogger(Day18Part2.class);
    static final String inputFile = "2019/day18_2.txt";
    static final char WALL = '#';

    public static void main(String... args) throws IOException {
        Day18Part2 solution = new Day18Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = minimumStepToCollectAllKeys(lines);
        log.warn("what is the fewest steps necessary to collect all of the keys? {}", result);
    }

    char[][] parseMap(List<String> strings) {
        char[][] map = new char[strings.size()][strings.get(0).length()];
        for (int i = 0; i < map.length; i++) {
            map[i] = strings.get(i).toCharArray();
        }
        return map;
    }

    List<Position> findAll(char c, char[][] map) {
        var positions = new ArrayList<Position>();
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
        var robotStartPositions = findAll('@', map);
        var keyLocations = getKeyLocations(map);
        var pathsPerRegion = allPathsBetweenEveryKey(map, robotStartPositions, keyLocations);
        var keyLocationsPerRegion = keyLocationsPerRegion(pathsPerRegion);
        var routes = new PriorityQueue<Route>(Comparator.comparingInt(r -> r.steps));
        var minStepOnRoute = new HashMap<String, Integer>();
        routes.offer(new Route(robotStartPositions, 0, Collections.emptySet()));
        int totalProcessedRoute = 0;

        while (routes.size() > 0) {
            Route route = routes.poll();
            totalProcessedRoute++;
            if (totalProcessedRoute%10000 == 0) log.debug("Processed route: {}", totalProcessedRoute);

            if (route.keys.size() == keyLocations.size()) {
                return route.steps;
            }

            for (int region=0; region < robotStartPositions.size(); region++) {
                var robotPosition = route.robotPositions.get(region);
                var pathsForRobot = pathsPerRegion.get(region);
                for (Position nextPosition : keyLocationsPerRegion.get(region)) {
                    if (!validMove(route, route.robotPositions.get(region), nextPosition, pathsForRobot,map))
                        continue;
                    var nextRoute = nextRoute(route, region, nextPosition, pathsForRobot, map);
                    if (nextRoute.steps >= minStepOnRoute.getOrDefault(nextRoute.identifier, Integer.MAX_VALUE))
                        continue;
                    minStepOnRoute.put(nextRoute.identifier, nextRoute.steps);
                    routes.offer(nextRoute);
                }
            }
        }
        throw new RuntimeException("Cannot find route");
    }

    private List<List<Position>> keyLocationsPerRegion(List<Map<Path, Path>> pathsPerRegion) {
        return pathsPerRegion.stream()
                .map(paths ->
                        paths.keySet().stream()
                                .map(p -> p.to)
                                .collect(Collectors.toList())
                ).collect(Collectors.toList());
    }

    boolean validMove(Route route, Position from, Position next, Map<Path, Path> paths, char[][] map) {
        var path = paths.get(new Path(from, next));
        if (path == null) return false;

        var value = next.getValueFrom(map);
        if (route.keys.contains(value)) return false;

        for (char door: path.doors) {
            if (!route.keys.contains(Character.toLowerCase(door)))
                return false;
        }
        return true;
    }

    Route nextRoute(Route route, int region, Position nextRobotPosition, Map<Path, Path> paths, char[][] map) {
        var steps = paths.get(new Path(route.robotPositions.get(region), nextRobotPosition)).steps;
        var nextRobotPositions = new ArrayList<>(route.robotPositions);
        nextRobotPositions.set(region, nextRobotPosition);
        var keys = new HashSet<>(route.keys);
        keys.add(nextRobotPosition.getValueFrom(map));
        return new Route(nextRobotPositions, route.steps + steps, keys);
    }

    static class Route {
        int steps;
        List<Position> robotPositions;
        Set<Character> keys;
        String identifier;

        public Route(List<Position> robotPositions, int step, Set<Character> keys) {
            this.robotPositions = robotPositions;
            this.steps = step;
            this.keys = keys;
            this.identifier = robotPositions.toString() + keys.toString();
        }

        @Override
        public String toString() {
            return identifier + " - steps: " + steps;
        }
    }

    List<Map<Path, Path>> allPathsBetweenEveryKey(char[][] map, List<Position> startPositions, Set<Position> keyLocations) {
        var allPaths = new ArrayList<Map<Path, Path>>();
        for (Position startPosition : startPositions) {
            var paths = new HashMap<Path, Path>();

            for (Position to: keyLocations) {
                findShortestPath(startPosition, to, map).ifPresent(p -> paths.put(p, p));
            }

            var regionalKeyLocations = paths.keySet().stream().map(p -> p.to).collect(Collectors.toList());
            for (Position from : regionalKeyLocations) {
                for (Position to: keyLocations) {
                    if (Objects.equals(from, to)) continue;
                    findShortestPath(from, to, map).ifPresent(p -> paths.put(p, p));
                }
            }
            allPaths.add(paths);
        }
        return allPaths;
    }

    Optional<Path> findShortestPath(Position from, Position to, char[][] map) {
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
                return Optional.of(shortestPath);
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
        return Optional.empty();
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

        @Override
        public String toString() {
            return new StringJoiner(", ", Path.class.getSimpleName() + "[", "]")
                    .add("from=" + from).add("to=" + to).add("steps=" + steps).add("doors=" + doors).toString();
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
        String s = "#######\n" +
                "#a.#Cd#\n" +
                "##@#@##\n" +
                "#######\n" +
                "##@#@##\n" +
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
