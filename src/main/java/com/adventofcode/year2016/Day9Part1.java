package com.adventofcode.year2016;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static junit.framework.TestCase.assertEquals;

public class Day9Part1 {
    Logger log = LoggerFactory.getLogger(Day9Part1.class);
    final static String inputFile = "2016/day9_1.txt";

    public static void main(String... args) throws IOException {
        Day9Part1 solution = new Day9Part1();
        solution.firstStar();
    }

    void firstStar() throws IOException {
      var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
      var result = decompress(lines.get(0)).length();
      log.warn("First star - What is the decompressed length of the file? {}", result);
    }

    String decompress(String input) {
        StringBuilder builder = new StringBuilder();
        int pointer = 0;
        while (pointer < input.length()) {
            char character = input.charAt(pointer);
            if (character == '(')
                pointer = processParentheses(input, pointer, builder);
            else
                builder.append(character);

            pointer++;
        }
        return builder.toString();
    }

    private int processParentheses(String input, int pointer, StringBuilder builder) {
        int closeParenthesesPosition = input.indexOf(')', pointer);
        var params = input.substring(pointer+1, closeParenthesesPosition).split("x");
        String pattern = input.substring(closeParenthesesPosition + 1, closeParenthesesPosition + 1 + Integer.valueOf(params[0]));
        builder.append(StringUtils.repeat(pattern, Integer.valueOf(params[1])));
        return closeParenthesesPosition + pattern.length();
    }

    @Test
    public void test_decompress() {
        assertEquals("ADVENT", decompress("ADVENT"));
        assertEquals("ABBBBBC", decompress("A(1x5)BC"));
        assertEquals("XYZXYZXYZ", decompress("(3x3)XYZ"));
        assertEquals("ABCBCDEFEFG", decompress("A(2x2)BCD(2x2)EFG"));
        assertEquals("(1x3)A", decompress("(6x1)(1x3)A"));
        assertEquals("X(3x3)ABC(3x3)ABCY", decompress("X(8x2)(3x3)ABCY"));
    }


}