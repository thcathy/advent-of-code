package com.adventofcode.year2019;


import java.io.IOException;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.assertEquals;

public class Day10Part1 {
    Logger log = LoggerFactory.getLogger(Day10Part1.class);
    final static String inputFile = "2019/day10_1.txt";

    public static void main(String... args) throws IOException {
        Day10Part1 solution = new Day10Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = maxAsteroidSeenFromBestLocation(getAsteroids(lines));
        log.warn("How many other asteroids can be detected from that location? {}", result);
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

    long maxAsteroidSeenFromBestLocation(List<Position> allAsteroids) {
        return allAsteroids.stream()
                .mapToLong(asteroid -> asteroidsCanDetect(asteroid, allAsteroids))
                .max().getAsLong();
    }

    @Test
    public void angleTo_testcases() {
        var point = new Position(3, 4);
        assertEquals(point.angleTo(new Position(1, 0)), point.angleTo(new Position(2, 2)));

        var center = new Position(0, 0);
        assertEquals(center.angleTo(new Position(4, 3)), center.angleTo(new Position(8, 6)));
    }

    @Test
    public void maxAsteroidSeenFromBestLocation_testcases() {
        var input1 = List.of(
            ".#..#",
            ".....",
            "#####",
            "....#",
            "...##"
        );
        assertEquals(8, maxAsteroidSeenFromBestLocation(getAsteroids(input1)));

        var input2 = List.of(
                "......#.#.",
                "#..#.#....",
                "..#######.",
                ".#.#.###..",
                ".#..#.....",
                "..#....#.#",
                "#..#....#.",
                ".##.#..###",
                "##...#..#.",
                ".#....####"
        );
        assertEquals(33, maxAsteroidSeenFromBestLocation(getAsteroids(input2)));

        var input3 = List.of(
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
        assertEquals(210, maxAsteroidSeenFromBestLocation(getAsteroids(input3)));
    }

    class Position {
        int x = 0, y = 0;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public String angleTo(Position target) {
            float angle = (float) Math.toDegrees(Math.atan2(target.y - y, target.x - x));
            if(angle < 0) { angle += 360; }
            return String.valueOf(angle);
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
