package com.adventofcode.year2019;


import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

public class Day20Part2 {
    Logger log = LoggerFactory.getLogger(Day20Part2.class);
    static final String inputFile = "2019/day20_1.txt";
    static final char OPEN_PASSAGE = '.';
    static final char SPACE = ' ';
    static final String MAZE_START = "AA";
    static final String MAZE_END = "ZZ";

    public static void main(String... args) throws IOException {
        Day20Part2 solution = new Day20Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = minimumStepToCollectAllKeys(lines);
        log.warn("how many steps does it take to get from the open tile marked AA to the open tile marked ZZ? {}", result);
    }

    char[][] parseMaze(List<String> strings) {
        char[][] map = new char[strings.size()][strings.get(0).length()];
        for (int i = 0; i < map.length; i++) {
            map[i] = strings.get(i).toCharArray();
        }
        return map;
    }

    int[] findInnerEdge(char[][] maze) {
        int minX=Integer.MAX_VALUE, minY=Integer.MAX_VALUE, maxX=0, maxY=0;
        for (int y=3; y<maze.length-3; y++) {
            for (int x=3; x<maze[0].length-3; x++) {
                if (maze[y][x] == SPACE) {
                    minX = Math.min(minX, x);
                    maxX = Math.max(maxX, x);
                    minY = Math.min(minY, y);
                    maxY = Math.max(maxY, y);
                }
            }
        }
        return new int[] { minX, maxX, minY, maxY};
    }

    Map<String, Position> findInnerGates(char[][] maze) {
        var gates = new HashMap<String, Position>();
        var innerEdgePosition = findInnerEdge(maze); // [minX, maxX, minY, maxY]

        for (int x=innerEdgePosition[0]; x<=innerEdgePosition[1]-1; x++) {
            if (Character.isUpperCase(maze[innerEdgePosition[2]][x]) && Character.isUpperCase(maze[innerEdgePosition[2]+1][x])) {
                gates.put("" + maze[innerEdgePosition[2]][x] + maze[innerEdgePosition[2]+1][x], new Position(x, innerEdgePosition[2]-1));
            }

            if (Character.isUpperCase(maze[innerEdgePosition[3]-1][x]) && Character.isUpperCase(maze[innerEdgePosition[3]][x])) {
                gates.put("" + maze[innerEdgePosition[3]-1][x] + maze[innerEdgePosition[3]][x], new Position(x, innerEdgePosition[3]+1));
            }
        }

        for(int y=innerEdgePosition[2]; y<=innerEdgePosition[3]-1; y++) {
            if (Character.isUpperCase(maze[y][innerEdgePosition[0]]) && Character.isUpperCase(maze[y][innerEdgePosition[0]+1])) {
                gates.put("" + maze[y][innerEdgePosition[0]] + maze[y][innerEdgePosition[0]+1], new Position(innerEdgePosition[0]-1, y));
            }

            if (Character.isUpperCase(maze[y][innerEdgePosition[1]-1]) && Character.isUpperCase(maze[y][innerEdgePosition[1]])) {
                gates.put("" + maze[y][innerEdgePosition[1]-1] + maze[y][innerEdgePosition[1]], new Position(innerEdgePosition[1]+1, y));
            }
        }

        return gates;
    }

    Map<String, Position> findOuterGates(char[][] maze) {
        var gates = new HashMap<String, Position>();
        for (int x=2; x<maze[0].length-2; x++) {
            if (Character.isUpperCase(maze[0][x])) {
                gates.put("" + maze[0][x] + maze[1][x], new Position(x, 2));
            }

            if (Character.isUpperCase(maze[maze.length-2][x])) {
                gates.put("" + maze[maze.length-2][x] + maze[maze.length-1][x], new Position(x, maze.length-3));
            }
        }

        for(int y=2; y<maze.length-2; y++) {
            if (Character.isUpperCase(maze[y][0])) {
                gates.put("" + maze[y][0] + maze[y][1], new Position( 2, y));
            }

            if (Character.isUpperCase(maze[y][maze[0].length-2])) {
                gates.put("" + maze[y][maze[0].length-2] + maze[y][maze[0].length-1], new Position(maze[0].length-3, y));
            }
        }

        return gates;
    }

    int minimumStepToCollectAllKeys(List<String> inputs) {
        var maze = parseMaze(inputs);
        var outerGates = findOuterGates(maze);
        var innerGates = findInnerGates(maze);
        var startPosition = outerGates.get(MAZE_START);
        var endPosition = outerGates.get(MAZE_END);
        var paths = findAllPaths(maze, outerGates, innerGates);
        var routes = new PriorityQueue<Route>(Comparator.comparingInt(r -> r.steps));

        routes.offer(new Route(startPosition, 0, 0));
        var visitedRoute = new HashSet<String>();

        while (routes.size() > 0) {
            Route route = routes.poll();
            if (Objects.equals(endPosition, route.position)) return route.steps;

            if (route.level < 0 || !visitedRoute.add(route.identifier())) continue;

            for (Path nextPath : paths.get(route.position)) {
                boolean notOutermost = (route.level != 0);
                if (notOutermost && (Objects.equals(nextPath.to, startPosition) || Objects.equals(nextPath.to, endPosition))) continue;
                var nextRoute = new Route(nextPath.to, route.steps + nextPath.steps, route.level + nextPath.deltaLevel);
                routes.offer(nextRoute);
            }
        }
        throw new RuntimeException("Cannot find route");
    }

    private Map<Position, List<Path>> findAllPaths(char[][] maze, Map<String,Position> outerGates, Map<String,Position> innerGates) {
        var pathsPerPosition = new HashMap<Position, List<Path>>();
        // portals paths
        for (var outerEntry : outerGates.entrySet()) {
            if (innerGates.containsKey(outerEntry.getKey())) {
                var innerPosition = innerGates.get(outerEntry.getKey());
                var pathsFromOuter = pathsPerPosition.computeIfAbsent(outerEntry.getValue(), (k) -> new ArrayList<>());
                pathsFromOuter.add(new Path(outerEntry.getValue(), innerPosition, 1, -1));

                var pathsFromInner = pathsPerPosition.computeIfAbsent(innerPosition, (k) -> new ArrayList<>());
                pathsFromInner.add(new Path(innerPosition, outerEntry.getValue(), 1, 1));
            }
        }

        // direct paths
        var allGates = Iterables.unmodifiableIterable(Iterables.concat(outerGates.values(), innerGates.values()));
        for (var gate : allGates) {
            pathsPerPosition.computeIfAbsent(gate, (k) -> new ArrayList<>())
                    .addAll(findValidPathsFromPosition(maze, gate, allGates));
        }
        return pathsPerPosition;
    }

    static class Route {
        int steps;
        Position position;
        int level;

        public Route(Position position, int step, int level) {
            this.position = position;
            this.steps = step;
            this.level = level;
        }

        public String identifier() { return position + Integer.toString(level); }
    }

    List<Path> findValidPathsFromPosition(char[][] map, Position from, Iterable<Position> allLocations) {
        var paths = new ArrayList<Path>();
        for (Position to: allLocations) {
            if (Objects.equals(from, to)) continue;
            findShortestPath(from, to, map).ifPresent(paths::add);
        }
        return paths;
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
                return Optional.of(new Path(from, to, step, 0));
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

    int cost(HashMap<Position, Integer> stepToPosition, Position from, Position to) {
        return stepToPosition.get(from) + distance(from, to);
    }

    int distance(Position a, Position b) { return Math.abs(a.x - b.x) + Math.abs(a.y - b.y); }

    boolean isValidPosition(Position pos, char[][] map) {
        if (pos.x < 0 || pos.y < 0) {
            return false;
        } else if (pos.x >= map[0].length || pos.y >= map.length) {
            return false;
        }
        return map[pos.y][pos.x] == OPEN_PASSAGE;
    }

    static class Path {
        Position from;
        Position to;
        int steps;
        int deltaLevel;

        public Path(Position from, Position to, int steps, int deltaLevel) {
            this.from = from;
            this.to = to;
            this.steps = steps;
            this.deltaLevel = deltaLevel;
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
                    .add("from=" + from).add("to=" + to).add("steps=" + steps).add("deltaLevel=" + deltaLevel).toString();
        }
    }

    static class Position {
        int x = 0, y = 0;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        List<Position> allDirections() {
            return List.of(new Position(x+1,y), new Position(x-1,y), new Position(x,y+1),new Position(x,y-1));
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
        public int hashCode() { return Objects.hash(x, y); }
    }

}
