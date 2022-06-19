package com.adventofcode.year2017;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;

/**
 * Using Cube coordinates of hex grids
 * Reference - https://www.redblobgames.com/grids/hexagons/
 */
public class Day11Part2 {
    final static String inputFile = "2017/day11_1.txt";

    public static void main(String... args) throws IOException {
        Day11Part2 solution = new Day11Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = furthestDistance(lines.get(0));
        System.out.printf("How many steps away is the furthest he ever got from his starting position? %d %n", result);
    }

    int furthestDistance(String steps) {
        int q=0, r=0, s=0, maxDistance=0;
        for (String step : steps.split(",")) {
            if ("n".equals(step)) {
                s++; r--;
            } else if ("ne".equals(step)) {
                q++; r--;
            } else if ("se".equals(step)) {
                q++; s--;
            } else if ("s".equals(step)) {
                r++; s--;
            } else if ("sw".equals(step)) {
                r++; q--;
            } else if ("nw".equals(step)) {
                s++; q--;
            }
            maxDistance = Math.max(maxDistance, distance(q, r, s));
        }
        return maxDistance;
    }

    int distance(int q, int r, int s) {
        return (Math.abs(q) + Math.abs(r) + Math.abs(s)) / 2;
    }

}
