package com.adventofcode.year2017;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class Day2Part1 {
    Logger log = LoggerFactory.getLogger(Day2Part1.class);
    final static String inputFile = "2017/day2_1.txt";

    public static void main(String... args) throws IOException {
        Day2Part1 solution = new Day2Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = calculateChecksum(lines);
        log.warn("What is the checksum for the spreadsheet in your puzzle input? {}", result);
    }

    public int calculateChecksum(List<String> input) {
        return input.stream().mapToInt(this::calulateDifference).sum();
    }

    int calulateDifference(String line) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (String input : line.split("\\s+")) {
            int value = Integer.valueOf(input);
            if (value < min) min = value;
            if (value > max) max = value;
        }
        return max - min;
    }

}
