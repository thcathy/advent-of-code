package com.adventofcode.year2018;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class Day1Part1 {
    Logger log = LoggerFactory.getLogger(Day1Part1.class);
    final static String inputFile = "2018/day1.txt";

    public static void main(String... args) throws IOException {
        Day1Part1 solution = new Day1Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = calculateFrequency(lines);
        log.warn("What is the first frequency your device reaches twice? {}", result);
    }

    public int calculateFrequency(List<String> inputs) {
        int frequency = 0;
        for (String s : inputs) {
            frequency += Integer.parseInt(s);
        }
        return frequency;
    }

}
