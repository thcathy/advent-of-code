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
        log.warn("Easter egg appears after {} seconds", seconds);
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
        int seconds = 0;
        while (true) {
            simulateRobots(robots);
            seconds++;
            if (isEasterEgg(robots)) {
                return seconds;
            }
        }
    }

    private void simulateRobots(List<Robot> robots) {
        robots.forEach(robot -> robot.move(WIDTH, HEIGHT));
    }

    private boolean isEasterEgg(List<Robot> robots) {
        // Define the expected pattern for the Easter egg (Christmas tree)
        // This is a simplified example; you may need to adjust the pattern based on the actual input
        var grid = new int[HEIGHT][WIDTH];
        robots.forEach(robot -> grid[robot.y()][robot.x()]++);

        // Example pattern check (adjust based on actual pattern)
        // Check if the robots form a specific shape (e.g., a tree)
        // This is a placeholder; you need to define the actual pattern
        return checkChristmasTreePattern(grid);
    }

    private boolean checkChristmasTreePattern(int[][] grid) {
        printGrid(grid);
        int height = grid.length;
        int width = grid[0].length;

        // Check for a dense region in the middle (trunk of the tree)
        int midX = width / 2;
        int midY = height / 2;

        // Define the expected width of the tree at different heights
        // For example, the tree should be wider at the bottom and narrower at the top
        int maxTreeWidth = width / 2; // Maximum width of the tree
        int minTreeWidth = 5; // Minimum width of the tree (narrower at the top)

        // Check density and symmetry
        for (int y = 0; y < height; y++) {
            int expectedWidth = maxTreeWidth - (y * (maxTreeWidth - minTreeWidth) / height);
            int leftBound = midX - expectedWidth / 2;
            int rightBound = midX + expectedWidth / 2;

            // Check if the density of robots in this row matches the expected tree shape
            int robotCount = 0;
            for (int x = leftBound; x <= rightBound; x++) {
                if (grid[y][x] > 0) {
                    robotCount++;
                }
            }

            // If the robot count is too low, it doesn't match the tree pattern
            if (robotCount < expectedWidth / 2) {
                return false;
            }

            // Check symmetry
            for (int x = 0; x < midX; x++) {
                if (grid[y][x] != grid[y][width - 1 - x]) {
                    return false;
                }
            }
        }

        // If all checks pass, assume it's a Christmas tree
        return true;
    }

    private void printGrid(int[][] grid) {
        System.out.println("-------------------------------------------------------");
        int height = grid.length;
        int width = grid[0].length;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[y][x] > 0) {
                    System.out.print("#"); // Robot present
                } else {
                    System.out.print("."); // No robot
                }
            }
            System.out.println(); // New line after each row
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
