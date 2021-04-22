package com.adventofcode.year2019;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

public class Day24Part1 {
    Logger log = LoggerFactory.getLogger(Day24Part1.class);
    static final String inputFile = "2019/day24_1.txt";
    static final char SPACE = '.';
    static final char BUG = '#';
    static final int MAP_SIZE = 5;

    public static void main(String... args) throws IOException {
        Day24Part1 solution = new Day24Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var map = parseMap(lines);
        var result = biodiversityRatingOfFirstRepeatLayout(map);
        log.warn("What is the biodiversity rating for the first layout that appears twice? {}", result);
    }

    private long biodiversityRatingOfFirstRepeatLayout(char[][] map) {
        var layouts = new HashSet<Long>();
        while (true) {
            printMap(map);
            long rating = biodiversityRating(map);
            if (!layouts.add(rating)) return rating;
            map = nextMap(map);
        }
    }

    char[][] nextMap(char[][] map) {
        var nextMap = new char[MAP_SIZE][MAP_SIZE];
        for (int y=0; y<MAP_SIZE; y++) {
            for (int x=0; x<MAP_SIZE; x++) {
                nextMap[y][x] = calculateNextValue(map, x, y);
            }
        }
        return nextMap;
    }

    char calculateNextValue(char[][] map, int x, int y) {
        var adjacentBugs = isBug(map, x-1, y) + isBug(map, x+1, y) + isBug(map, x, y-1) + isBug(map, x, y+1);
        if (map[y][x] == BUG)
            return (adjacentBugs == 1) ? BUG : SPACE;
        else
            return (adjacentBugs == 1 || adjacentBugs == 2) ? BUG : SPACE;
    }

    int isBug(char[][] map, int x, int y) {
        if (x<0 || y<0 || x>=MAP_SIZE || y>=MAP_SIZE)
            return 0;
        else
            return map[y][x] == BUG ? 1 : 0;
    }

    char[][] parseMap(List<String> lines) {
        char[][] map = new char[lines.size()][lines.get(0).length()];
        for (int i = 0; i < map.length; i++) {
            map[i] = lines.get(i).toCharArray();
        }
        return map;
    }

    long biodiversityRating(char[][] map) {
        long value = 0;
        for (int y=MAP_SIZE-1; y>=0; y--) {
            for (int x=MAP_SIZE-1; x>=0; x--) {
                value += map[y][x] == BUG ? 1 : 0;
                value = value << 1;
            }
        }
        return value >> 1;
    }

    void printMap(char[][] map) {
        for (char[] line : map) {
            for (char c : line) {
                System.out.print(c);
            }
            System.out.println();
        }
        System.out.println();
    }

}
