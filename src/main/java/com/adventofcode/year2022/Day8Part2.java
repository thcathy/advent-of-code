package com.adventofcode.year2022;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day8Part2 {
    final static String inputFile = "2022/day8.txt";

    public static void main(String... args) throws IOException {
        Day8Part2 solution = new Day8Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = highestScenicScore(parseMap(lines));
        System.out.println("how many trees are visible from outside the grid? " + result);
    }

    int highestScenicScore(int[][] map) {
        int highestScore = 0;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                highestScore = Math.max(highestScore, scenicScore(x, y, map));
            }
        }
        return highestScore;
    }

    int scenicScore(int x, int y, int[][] map) {
        if (x == 0 || y == 0 || x == map[0].length - 1 || y == map.length - 1)
            return 0;

        int leftTrees = 0, rightTrees = 0, upTrees = 0, downTrees = 0;
        int value = map[y][x];

        for (int i = x-1; i >= 0; i--) {
            leftTrees++;
            if (map[y][i] >= value) break;
        }
        for (int i = x+1; i < map[0].length; i++) {
            rightTrees++;
            if (map[y][i] >= value) break;
        }
        for (int j = y-1; j >= 0; j--) {
            upTrees++;
            if (map[j][x] >= value) break;
        }
        for (int j = y+1; j < map.length; j++) {
            downTrees++;
            if (map[j][x] >= value) break;
        }

        return leftTrees * rightTrees * upTrees * downTrees;
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
        assertEquals(8, highestScenicScore(map));
    }
}
