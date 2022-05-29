package com.adventofcode.year2017;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class Day9Part1 {
    final static String inputFile = "2017/day9_1.txt";

    public static void main(String... args) throws IOException {
        Day9Part1 solution = new Day9Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = totalScore(lines.get(0).toCharArray());
        System.out.printf("What is the total score for all groups in your input? %d %n", result);
    }

    int totalScore(char[] input) {
        int score = 0;
        int total = 0;
        boolean isGarbage = false;
        for (int i=0; i<input.length; i++) {
            char c = input[i];
            if (c == '!') {
                i++;
            } else if (c == '<') {
                isGarbage = true;
            } else if (c == '>') {
                isGarbage = false;
            } else if (c == '{' && !isGarbage) {
                score++;
            } else if (c == '}' && !isGarbage) {
                total += score;
                score--;
            }
        }

        return total;
    }

    @Test
    public void unitTest() {
        Assert.assertEquals(1, totalScore("{}".toCharArray()));
        Assert.assertEquals(6, totalScore("{{{}}}".toCharArray()));
        Assert.assertEquals(5, totalScore("{{},{}}".toCharArray()));
        Assert.assertEquals(16, totalScore("{{{},{},{{}}}}".toCharArray()));
        Assert.assertEquals(1, totalScore("{<a>,<a>,<a>,<a>}".toCharArray()));
        Assert.assertEquals(9, totalScore("{{<ab>},{<ab>},{<ab>},{<ab>}}".toCharArray()));
        Assert.assertEquals(9, totalScore("{{<!!>},{<!!>},{<!!>},{<!!>}}".toCharArray()));
        Assert.assertEquals(3, totalScore("{{<a!>},{<a!>},{<a!>},{<ab>}}".toCharArray()));
    }
}
