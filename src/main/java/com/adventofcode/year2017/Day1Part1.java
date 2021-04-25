package com.adventofcode.year2017;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Day1Part1 {
    Logger log = LoggerFactory.getLogger(Day1Part1.class);
    final static String inputFile = "2017/day1_1.txt";

    public static void main(String... args) throws IOException {
        Day1Part1 solution = new Day1Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = calculateCaptcha(lines.get(0));
        log.warn("What is the solution to your captcha? {}", result);
    }

    public int calculateCaptcha(String input) {
        int captcha = 0;
        for (int i=0; i<input.length()-1; i++) {
            if (input.charAt(i) == input.charAt(i+1))
                captcha += Character.getNumericValue(input.charAt(i));
        }
        if (input.charAt(0) == input.charAt(input.length()-1))
            captcha += Character.getNumericValue(input.charAt(0));
        return captcha;
    }

}
