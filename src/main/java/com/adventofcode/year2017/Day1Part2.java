package com.adventofcode.year2017;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Day1Part2 {
    Logger log = LoggerFactory.getLogger(Day1Part2.class);
    final static String inputFile = "2017/day1_1.txt";

    public static void main(String... args) throws IOException {
        Day1Part2 solution = new Day1Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = calculateCaptcha(lines.get(0));
        log.warn("What is the solution to your new captcha? {}", result);
    }

    public int calculateCaptcha(String input) {
        int captcha = 0;
        int delta = input.length() / 2;
        int length = input.length();

        for (int i=0; i<input.length(); i++) {
            if (input.charAt(i) == input.charAt(positionOf(i+delta, length)))
                captcha += Character.getNumericValue(input.charAt(i));
        }
        return captcha;
    }

    int positionOf(int index, int length) {
        if (index < length)
            return index;
        else
            return (index + length) % length;
    }

}
