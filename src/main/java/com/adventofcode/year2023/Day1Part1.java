package com.adventofcode.year2023;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day1Part1 {
    Logger log = LoggerFactory.getLogger(Day1Part1.class);
    final static String inputFile = "2023/day1.txt";

    public static void main(String... args) throws IOException {
        Day1Part1 solution = new Day1Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = sumOfCalibrationValues(lines);
        log.warn("What is the sum of all of the calibration values? {}", result);
    }

    public long sumOfCalibrationValues(List<String> inputs) {
        return inputs.stream().mapToLong(this::getCalibrationValue)
            .sum();
    }

    long getCalibrationValue(String input) {
        var digits = input.replaceAll("[^0-9]", "");        
        return Long.parseLong(digits.substring(0, 1) + digits.substring(digits.length() - 1));
    }

    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day1_test.txt"), Charsets.UTF_8);
        assertEquals(142, sumOfCalibrationValues(lines));
    }
}
