package com.adventofcode.year2024;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class Day14Part2 {
    private static final Logger log = LoggerFactory.getLogger(Day14Part2.class);
    private static final String INPUT_FILE = "2024/day14.txt";
    private static final int WIDTH = 101;
    private static final int HEIGHT = 103;

    public static void main(String... args) throws IOException {
        new Day14Part2().run();
    }

    private void run() throws IOException {
        var robots = parseInput();
        int seconds = findEasterEggTime(robots);
        log.warn("The robots form the Easter egg after {} seconds.", seconds);
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

    private int findEasterEggTime(List<Robot> robots) {
        int time = 0;
        int smallestArea = Integer.MAX_VALUE;
        int bestTime = 0;

        while (time < 10000) { // Arbitrary high limit
            moveRobots(robots);
            time++;

            var bounds = calculateBounds(robots);
            int width = bounds[1] - bounds[0] + 1;
            int height = bounds[3] - bounds[2] + 1;
            int area = width * height;

            // Detect the smallest area
            if (area < smallestArea) {
                smallestArea = area;
                bestTime = time;
            } else if (area > smallestArea) {
                // Area starts increasing, assume pattern has passed
                printRobots(robots);
                break;
            }
        }

        return bestTime;
    }

    private void moveRobots(List<Robot> robots) {
        robots.forEach(robot -> robot.move(WIDTH, HEIGHT));
    }

    private int[] calculateBounds(List<Robot> robots) {
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

        for (var robot : robots) {
            minX = Math.min(minX, robot.x());
            maxX = Math.max(maxX, robot.x());
            minY = Math.min(minY, robot.y());
            maxY = Math.max(maxY, robot.y());
        }

        return new int[]{minX, maxX, minY, maxY};
    }

    private void printRobots(List<Robot> robots) {
        char[][] grid = new char[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) {
            Arrays.fill(grid[i], '.');
        }

        for (var robot : robots) {
            grid[robot.y()][robot.x()] = '#';
        }

        for (var row : grid) {
            System.out.println(new String(row));
        }
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
