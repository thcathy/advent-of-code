package com.adventofcode.year2024;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class Day4Part2 {
    Logger log = LoggerFactory.getLogger(this.getClass());
    final static String inputFile = "2024/day4.txt";

    public static void main(String... args) throws IOException {new Day4Part2().run();}

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var input = parseInput(lines);
        var result = countXMASOccurrences(input);
        log.warn("How many times does XMAS appear? {}", result);
    }

    private char[][] parseInput(List<String> lines) {
        int rows = lines.size();
        char[][] grid = new char[rows][lines.getFirst().length()];
        for (int i = 0; i < rows; i++) {
            grid[i] = lines.get(i).toCharArray();
        }
        return grid;
    }

    private int countXMASOccurrences(char[][] grid) {
        int count = 0;
        int rows = grid.length;
        int cols = grid[0].length;

        for (int row = 1; row < rows - 1; row++) { // Start from 1 to avoid out of bounds
            for (int col = 1; col < cols - 1; col++) { // Same here
                count += checkForXMAS(grid, row, col);
            }
        }

        return count;
    }

    private int checkForXMAS(char[][] grid, int row, int col) {
        int count = 0;

        // Check if the center is 'A'
        if (grid[row][col] == 'A') {
            if (isInBounds(grid, row - 1, col - 1) && isInBounds(grid, row - 1, col + 1) &&
                    isInBounds(grid, row + 1, col - 1) && isInBounds(grid, row + 1, col + 1)) {
                if (grid[row - 1][col - 1] == 'M' && grid[row + 1][col + 1] == 'S' &&
                        grid[row + 1][col - 1] == 'M' && grid[row - 1][col + 1] == 'S') {
                    count++;
                }

                if (grid[row - 1][col - 1] == 'M' && grid[row + 1][col + 1] == 'S' &&
                        grid[row + 1][col - 1] == 'S' && grid[row - 1][col + 1] == 'M') {
                    count++;
                }

                if (grid[row - 1][col - 1] == 'S' && grid[row + 1][col + 1] == 'M' &&
                        grid[row + 1][col - 1] == 'M' && grid[row - 1][col + 1] == 'S') {
                    count++;
                }

                if (grid[row - 1][col - 1] == 'S' && grid[row + 1][col + 1] == 'M' &&
                        grid[row + 1][col - 1] == 'S' && grid[row - 1][col + 1] == 'M') {
                    count++;
                }
            }
        }

        return count;
    }

    private boolean isInBounds(char[][] grid, int row, int col) {
        return row >= 0 && row < grid.length && col >= 0 && col < grid[0].length;
    }
}
