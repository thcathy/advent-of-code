package com.adventofcode.year2022;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day8Part1 {
    final static String inputFile = "2022/day8.txt";

    public static void main(String... args) throws IOException {
        Day8Part1 solution = new Day8Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = totalVisible(parseMap(lines));
        System.out.println("how many trees are visible from outside the grid? " + result);
    }

    int totalVisible(int[][] map) {
        int totalVisible = 0;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (isVisible(x, y, map)) totalVisible++;
            }
        }
        return totalVisible;
    }

    boolean isVisible(int x, int y, int[][] map) {
        if (x == 0 || y == 0 || x == map[0].length - 1 || y == map.length - 1)
            return true;

        int value = map[y][x];
        
        if (IntStream.range(0, x).allMatch(i -> map[y][i] < value)) return true;    // left
        if (IntStream.range(x+1, map[0].length).allMatch(i -> map[y][i] < value)) return true;     // right
        if (IntStream.range(0, y).allMatch(j -> map[j][x] < value)) return true;    // up
        if (IntStream.range(y+1, map.length).allMatch(j -> map[j][x] < value)) return true;         // down
        return false;
    }

    int[][] parseMap(List<String> strings) {
        int[][] map = new int[strings.size()][strings.get(0).length()];
        for (int i = 0; i < map.length; i++) {
            var line = strings.get(i);
            for (int j = 0; j < line.length(); j++) {
                map[i][j] = Character.getNumericValue(line.charAt(j));
            }
        }
        return map;
    }

    @Test
    public void unitTest() throws Exception {
        var lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day8_test.txt").toURI()), Charset.defaultCharset());
        var map = parseMap(lines);
        assertTrue(isVisible(1, 1, map));
        assertTrue(isVisible(2, 1, map));
        assertFalse(isVisible(3, 1, map));
        assertEquals(21, totalVisible(map));
    }
}
