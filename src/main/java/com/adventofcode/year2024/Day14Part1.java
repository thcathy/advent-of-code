package com.adventofcode.year2024;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class Day14Part1 {
    private static final Logger log = LoggerFactory.getLogger(Day14Part1.class);
    private static final String INPUT_FILE = "2024/day14.txt";
    private static final int WIDTH = 101;
    private static final int HEIGHT = 103;
    private static final int SIMULATION_TIME = 100;

    public static void main(String... args) throws IOException {
        new Day14Part1().run();
    }

    private void run() throws IOException {
        var robots = parseInput();
        simulateRobots(robots);
        var safetyFactor = calculateSafetyFactor(robots);
        log.warn("Safety factor after {} seconds: {}", SIMULATION_TIME, safetyFactor);
    }

    private List<Robot> parseInput() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(INPUT_FILE), Charsets.UTF_8);
        return lines.stream().map(this::parseRobot).toList();
    }

    private Robot parseRobot(String line) {
        var parts = line.split(" ");
        var px = Integer.parseInt(parts[0].substring(2, parts[0].indexOf(",")));
        var py = Integer.parseInt(parts[0].substring(parts[0].indexOf(",") + 1));
        var vx = Integer.parseInt(parts[1].substring(2, parts[1].indexOf(",")));
        var vy = Integer.parseInt(parts[1].substring(parts[1].indexOf(",") + 1));
        return new Robot(px, py, vx, vy);
    }

    private void simulateRobots(List<Robot> robots) {
        robots.forEach(robot -> robot.move(WIDTH, HEIGHT, SIMULATION_TIME));
    }

    private int calculateSafetyFactor(List<Robot> robots) {
        var grid = new int[HEIGHT][WIDTH];

        robots.forEach(robot -> grid[robot.y()][robot.x()]++);

        var halfWidth = WIDTH / 2;
        var halfHeight = HEIGHT / 2;

        var quadrants = new int[4];

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (x == halfWidth || y == halfHeight) continue;
                var quadrantIndex = (y < halfHeight ? 0 : 2) + (x < halfWidth ? 0 : 1);
                quadrants[quadrantIndex] += grid[y][x];
            }
        }

        return Arrays.stream(quadrants).reduce(1, (a, b) -> a * b);
    }

    private static final class Robot {
        private int x, y;
        private final int vx, vy;

        Robot(int x, int y, int vx, int vy) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
        }

        void move(int width, int height, int steps) {
            for (int t = 0; t < steps; t++) {
                move(width, height);
            }
        }

        void move(int width, int height) {
            x = Math.floorMod(x + vx, width);
            y = Math.floorMod(y + vy, height);
        }

        int x() {
            return x;
        }

        int y() {
            return y;
        }
    }

}
