package com.adventofcode.year2019;


import java.io.IOException;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.assertEquals;

public class Day10Part2 {
    Logger log = LoggerFactory.getLogger(Day10Part2.class);
    final static String inputFile = "2019/day10_1.txt";

    public static void main(String... args) throws IOException {
        Day10Part2 solution = new Day10Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var asteroids = getAsteroids(lines);
        var target200th = sortedTarget(bestMonitoringLocation(asteroids).position, asteroids).get(199);
        log.warn("what do you get if you multiply its X coordinate by 100 and then add its Y coordinate? {}", target200th.position.x * 100 + target200th.position.y);
    }

    List<Position> getAsteroids(List<String> inputs) {
        int width = inputs.get(0).length();
        var asteroids = new LinkedList<Position>();
        for (int y = 0; y < inputs.size(); y++) {
            for (int x = 0; x < width; x++) {
                if (inputs.get(y).charAt(x) == '#')
                    asteroids.add(new Position(x, y));
            }
        }
        return asteroids;
    }

    long asteroidsCanDetect(Position from, List<Position> allAsteroids) {
        return allAsteroids.stream()
                .map(asteroid -> from.angleTo(asteroid))
                .distinct().count();
    }

    Asteroid bestMonitoringLocation(List<Position> allAsteroidsPosition) {
        return allAsteroidsPosition.stream()
                .map(position -> new Asteroid(position, asteroidsCanDetect(position, allAsteroidsPosition)))
                .max((a1, a2) -> (int) (a1.seenAsteroid - a2.seenAsteroid)).get();
    }

    Target buildTarget(Position from, Position target) {
        return new Target(target, from.angleTo(target), distanceOf(from, target));
    }

    int distanceOf(Position a, Position b) { return Math.abs(a.x - b.x) + Math.abs(a.y - b.y); }

    List<Target> sortedTarget(Position from, List<Position> allTargets) {
        var sortByDegreeDistance = sortTargetByDegreeDistance(from, allTargets);

        int pointer = 0;
        String laserDegree = "";
        var finalSortedTarget = new LinkedList<Target>();
        while (sortByDegreeDistance.size() > 0) {
            if (!laserDegree.equals(sortByDegreeDistance.get(pointer).degree)) {
                var target = sortByDegreeDistance.remove(pointer);
                laserDegree = target.degree;
                finalSortedTarget.add(target);
            } else {
                pointer++;
            }
            if (pointer >= sortByDegreeDistance.size()) {
                pointer = 0;
                laserDegree = "";
            }
        }
        return finalSortedTarget;
    }

    List<Target> sortTargetByDegreeDistance(Position from, List<Position> allTargets) {
        return allTargets.stream()
                .filter(t -> !t.equals(from))
                .map(t -> buildTarget(from, t))
                .sorted((a, b) -> {
                    if (a.degree.equals(b.degree)) return a.distance - b.distance;
                    else return Double.compare(Double.valueOf(a.degree), Double.valueOf(b.degree));
                }).collect(Collectors.toList());
    }

    @Test
    public void angleTo_testcases() {
        var point = new Position(3, 4);
        assertEquals(point.angleTo(new Position(1, 0)), point.angleTo(new Position(2, 2)));
        assertEquals("45.0", point.angleTo(new Position(4,3)));

        var center = new Position(0, 0);
        assertEquals(center.angleTo(new Position(4, 3)), center.angleTo(new Position(8, 6)));
    }

    @Test
    public void bestMonitoringLocation_testcases() {
        var input = testInput();
        assertEquals(new Position(11, 13), bestMonitoringLocation(getAsteroids(input)).position);
    }

    @Test
    public void sortedTarget_testcases() {
        var input = testInput();
        var sortedTargets = sortedTarget(new Position(11, 13), getAsteroids(input));
        assertEquals(new Position(11, 12), sortedTargets.get(0).position);
        assertEquals(new Position(12, 1), sortedTargets.get(1).position);
        assertEquals(new Position(12, 2), sortedTargets.get(2).position);
        assertEquals(new Position(12, 8), sortedTargets.get(9).position);
        assertEquals(new Position(16, 0), sortedTargets.get(19).position);
        assertEquals(new Position(16, 9), sortedTargets.get(49).position);
        assertEquals(new Position(10, 16), sortedTargets.get(99).position);
        assertEquals(new Position(8, 2), sortedTargets.get(199).position);
    }

    List<String> testInput() {
        return List.of(
                ".#..##.###...#######",
                "##.############..##.",
                ".#.######.########.#",
                ".###.#######.####.#.",
                "#####.##.#.##.###.##",
                "..#####..#.#########",
                "####################",
                "#.####....###.#.#.##",
                "##.#################",
                "#####.##.###..####..",
                "..######..##.#######",
                "####.##.####...##..#",
                ".#####..#.######.###",
                "##...#.##########...",
                "#.##########.#######",
                ".####.#.###.###.#.##",
                "....##.##.###..#####",
                ".#.#.###########.###",
                "#.#.#.#####.####.###",
                "###.##.####.##.#..##"
        );
    }

    class Target {
        Position position;
        String degree;
        int distance;

        public Target(Position position, String degree, int distance) {
            this.position = position;
            this.degree = degree;
            this.distance = distance;
        }
    }

    class Asteroid {
        long seenAsteroid;
        Position position;

        public Asteroid(Position position, long seenAsteroid) {
            this.seenAsteroid = seenAsteroid;
            this.position = position;
        }
    }

    class Position {
        int x = 0, y = 0;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public String angleTo(Position target) {
            double theta = Math.atan2(target.y - y, target.x - x) + Math.PI/2.0;
            double angle = Math.toDegrees(theta);
            return String.valueOf(angle < 0 ? angle + 360 : angle);
        }

        @Override
        public String toString() {
            return MessageFormat.format("({0},{1})", x, y);
        }

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
}
