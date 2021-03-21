package com.adventofcode.year2016;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class Day24Part2 {
    Logger log = LoggerFactory.getLogger(Day24Part2.class);
    final static String inputFile = "2016/day24_1.txt";

    public static final char WALL = '#';

    public static void main(String... args) throws IOException {
        Day24Part2 solution = new Day24Part2();
        solution.secondStar();
    }

    void secondStar() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        int minStep = fewestStepToVisitAndBackToStart(lines);
        log.warn("Second star - What is the fewest number of steps required to start at 0, visit every non-0 number marked on the map at least once, and then return to 0? {}", minStep);
    }

    int fewestStepToVisitAndBackToStart(List<String> inputs) {
        var map = parseMap(inputs);
        var startPosition = find('0', map);
        var locationsToVisit = getLocations(map);
        locationsToVisit.remove(startPosition);

        var distances = distanceBetweenAllLocations(map, startPosition, locationsToVisit);
        return fewestStepToVisitAllLocationsAndBackToStart(startPosition, new Path(startPosition, 0, Collections.emptyList()), locationsToVisit, distances);
    }

    private int fewestStepToVisitAllLocationsAndBackToStart(Position start, Path path, Set<Position> allLocations, Map<Pair<Position, Position>, Integer> distances) {
        if (path.visited.size() == allLocations.size()) {
            return path.steps + distances.get(Pair.of(start, path.lastPosition()));
        }

        return allLocations.stream()
                .filter(location -> !path.visited.contains(location))
                .map(to -> nextPath(path, to, distances.get(Pair.of(path.position, to))))
                .mapToInt(nextPath -> fewestStepToVisitAllLocationsAndBackToStart(start, nextPath, allLocations, distances))
                .min().getAsInt();
    }

    Path nextPath(Path path, Position next, int distance) {
        var visited = new ArrayList<Position>();
        visited.addAll(path.visited);
        visited.add(next);
        return new Path(next, path.steps + distance, visited);
    }

    static class Path {
        int steps;
        Position position;
        List<Position> visited;

        public Path(Position position, int step, List<Position> visited) {
            this.position = position;
            this.steps = step;
            this.visited = visited;
        }

        Position lastPosition() { return visited.get(visited.size()-1); }
    }

    Map<Pair<Position, Position>, Integer> distanceBetweenAllLocations(char[][] map, Position startPosition, Set<Position> locations) {
        var distances = new HashMap<Pair<Position, Position>, Integer>();
        for (Position to: locations) {
            distances.put( Pair.of(startPosition, to) , fewestStepToPosition(startPosition, to, map) );
        }
        for (Position from: locations) {
            for (Position to: locations) {
                distances.put( Pair.of(from, to) , fewestStepToPosition(from, to, map) );
            }
        }
        return distances;
    }

    int fewestStepToPosition(Position from, Position to, char[][] map) {
        var stepToPosition = new HashMap<Position, Integer>();
        var positions = new PriorityQueue<Position>((Comparator.comparingInt(o -> cost(stepToPosition, o, to))));
        stepToPosition.put(from, 0);
        positions.offer(from);

        while (positions.size() > 0) {
            Position position = positions.poll();
            int step = stepToPosition.get(position);
            if (position.equals(to)) {
                return step;
            } else {
                position.allDirections().stream()
                    .filter(next -> isValidPosition(next, map) && !stepToPosition.containsKey(next))
                    .forEach(next -> {
                        stepToPosition.put(next, step + 1);
                        positions.offer(next);
                    });
            }
        }
        return Integer.MAX_VALUE;   // cannot find any path
    }

    int cost(HashMap<Position, Integer> stepToPosition, Position from, Position to) {
        return stepToPosition.get(from) + distance(from, to);
    }

    int distance(Position a, Position b) { return Math.abs(a.x - b.x) + Math.abs(a.y - b.y); }

    boolean isValidPosition(Position next, char[][] map) {
        if (next.x < 0 || next.y < 0) {
            return false;
        } else if (next.x >= map[0].length || next.y >= map.length) {
            return false;
        } else if (map[next.y][next.x] == WALL) {
            return false;
        }
        return true;
    }

    char[][] parseMap(List<String> strings) {
        char[][] map = new char[strings.size()][strings.get(0).length()];
        for (int i = 0; i < map.length; i++) {
            map[i] = strings.get(i).toCharArray();
        }
        return map;
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
        int minStep = fewestStepToVisitAndBackToStart(inputs);
        assertThat(minStep, is(20));
    }
}
