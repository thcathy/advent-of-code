package com.adventofcode.year2024;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day4Part1 {
    Logger log = LoggerFactory.getLogger(this.getClass());
    final static String inputFile = "2024/day4.txt";

    public static void main(String... args) throws IOException {
        new Day4Part1().run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var input = parseInput(lines);
        var result = countOccurrences(input, "XMAS");
        log.warn("How many times does XMAS appear? {}", result);
    }

    private char[][] parseInput(List<String> lines) {
        int rows = lines.size();
        int cols = lines.get(0).length();
        char[][] grid = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            grid[i] = lines.get(i).toCharArray();
        }

        return grid;
    }

    private int countOccurrences(char[][] grid, String word) {
        int count = 0;
        int rows = grid.length;
        int cols = grid[0].length;

        // Directions: right, down, diagonal down-right, left, up, diagonal up-left, diagonal down-left, diagonal up-right
        int[][] directions = {
                {0, 1},
                {1, 0},
                {1, 1},
                {0, -1},
                {-1, 0},
                {-1, -1},
                {1, -1},
                {-1, 1}
        };

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                for (int[] direction : directions) {
                    count += countWordInDirection(grid, row, col, direction[0], direction[1], word);
                }
            }
        }

        return count;
    }

    private int countWordInDirection(char[][] grid, int startRow, int startCol, int rowDelta, int colDelta, String word) {
        int wordLength = word.length();
        int row = startRow;
        int col = startCol;

        for (int i = 0; i < wordLength; i++) {
            if (row < 0 || row >= grid.length || col < 0 || col >= grid[0].length || grid[row][col] != word.charAt(i)) {
                return 0; // Word does not match
            }
            row += rowDelta;
            col += colDelta;
        }

        return 1; // Found one occurrence of the word
    }
}
