package com.adventofcode.year2017;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Using Cube coordinates of hex grids
 * Reference - https://www.redblobgames.com/grids/hexagons/
 */
public class Day11Part1 {
    final static String inputFile = "2017/day11_1.txt";

    public static void main(String... args) throws IOException {
        Day11Part1 solution = new Day11Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = distance(lines.get(0));
        System.out.printf("the fewest number of steps required? %d %n", result);
    }

    int distance(String steps) {
        int q=0, r=0, s=0;
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
        }
        return (Math.abs(q) + Math.abs(r) + Math.abs(s)) / 2;
    }


    @Test
    public void unitTest() {
        Assert.assertEquals(3, distance("ne,ne,ne"));
        Assert.assertEquals(0, distance("ne,ne,sw,sw"));
        Assert.assertEquals(2, distance("ne,ne,s,s"));
        Assert.assertEquals(3, distance("se,sw,se,sw,sw"));
    }
}
