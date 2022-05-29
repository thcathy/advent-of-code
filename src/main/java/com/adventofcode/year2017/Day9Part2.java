package com.adventofcode.year2017;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class Day9Part2 {
    final static String inputFile = "2017/day9_1.txt";

    public static void main(String... args) throws IOException {
        Day9Part2 solution = new Day9Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = totalGarbage(lines.get(0).toCharArray());
        System.out.printf("How many non-canceled characters are within the garbage in your puzzle input? %d %n", result);
    }

    int totalGarbage(char[] input) {
        int totalGarbage = 0;
        boolean isGarbage = false;
        for (int i=0; i<input.length; i++) {
            char c = input[i];
            if (c == '!') {
                i++;
            } else if (c == '<' && !isGarbage) {
                isGarbage = true;
            } else if (c == '>') {
                isGarbage = false;
            } else if (isGarbage) {
                totalGarbage++;
            }
        }

        return totalGarbage;
    }

    @Test
    public void unitTest() {
        assertEquals(0, totalGarbage("<>".toCharArray()));
        assertEquals(17, totalGarbage("<random characters>".toCharArray()));
        assertEquals(3, totalGarbage("<<<<>".toCharArray()));
        assertEquals(2, totalGarbage("<{!>}>".toCharArray()));
        assertEquals(0, totalGarbage("<!!>".toCharArray()));
        assertEquals(0, totalGarbage("<!!!>>".toCharArray()));
        assertEquals(10, totalGarbage("<{o\"i!a,<{i<a>".toCharArray()));
    }
}
