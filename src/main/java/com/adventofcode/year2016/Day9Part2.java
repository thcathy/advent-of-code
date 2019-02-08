package com.adventofcode.year2016;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static junit.framework.TestCase.assertEquals;

public class Day9Part2 {
    Logger log = LoggerFactory.getLogger(Day9Part2.class);
    final static String inputFile = "2016/day9_1.txt";

    public static void main(String... args) throws IOException {
        Day9Part2 solution = new Day9Part2();
        solution.secondStar();
    }

    void secondStar() throws IOException {
      var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
      var result = decompress(lines.get(0));
      log.warn("Second star - What is the decompressed length of the file? {}", result);
    }

    long decompress(String input) {
        int pointer = 0;
        long count = 0;
        while (pointer < input.length()) {
            char character = input.charAt(pointer);
            if (character == '(') {
                int closeParenthesesPosition = input.indexOf(')', pointer);
                var params = input.substring(pointer+1, closeParenthesesPosition).split("x");
                String pattern = input.substring(closeParenthesesPosition + 1, closeParenthesesPosition + 1 + Integer.valueOf(params[0]));
                long patternLength = pattern.length();
                if (pattern.contains("(")) patternLength = decompress(pattern);
                count = count + patternLength * Integer.valueOf(params[1]);
                pointer = closeParenthesesPosition + Integer.valueOf(params[0]);
            } else
                count++;
            pointer++;
        }
        return count;
    }

    @Test
    public void test_decompress() {
        assertEquals(9, decompress("(3x3)XYZ"));
        assertEquals(20, decompress("X(8x2)(3x3)ABCY"));
        assertEquals(445, decompress("(25x3)(3x3)ABC(2x3)XY(5x2)PQRSTX(18x9)(3x2)TWO(5x7)SEVEN"));
        assertEquals(241920, decompress("(27x12)(20x12)(13x14)(7x10)(1x12)A"));
    }
}