package com.adventofcode.year2023;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day1Part2 {
    Logger log = LoggerFactory.getLogger(Day1Part2.class);
    final static String inputFile = "2023/day1.txt";

    public static void main(String... args) throws IOException {
        Day1Part2 solution = new Day1Part2();
        solution.run();
    }

    List<String> digitString = List.of("one","two","three","four","five","six","seven","eight","nine","ten","1","2","3","4","5","6","7","8","9");

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = sumOfCalibrationValues(lines);
        log.warn("What is the sum of all of the calibration values? {}", result);
    }

    public long sumOfCalibrationValues(List<String> inputs) {
        return inputs.stream()
                .mapToLong(this::getCalibrationValue)
                .sum();
    }

    long getCalibrationValue(String input) {        
        var firstDigit = firstDigit(input);
        var secondDigit = lastDigit(input);
        return Long.parseLong(firstDigit + secondDigit);
    }

    String lastDigit(String input) {
        var positions = digitLastPositions(input);
        var rawDigit = positions.get(Collections.max(positions.keySet()));
        return transform(rawDigit);
    }

    String firstDigit(String input) {
        var positions = digitFirstPositions(input);
        var rawDigit = positions.get(Collections.min(positions.keySet()));
        return transform(rawDigit);
    }

    Map<Integer, String> digitFirstPositions(String input) {
        var positions = new HashMap<Integer, String>();
        for (var digit : digitString) {
            var index = input.indexOf(digit);
            if (index >= 0) positions.put(index, digit);
        }
        return positions;
    }

    Map<Integer, String> digitLastPositions(String input) {
        var positions = new HashMap<Integer, String>();
        for (var digit : digitString) {
            var index = input.lastIndexOf(digit);
            if (index >= 0) positions.put(index, digit);
        }
        return positions;
    }

    String transform(String digit) {
        return switch (digit) {
            case "one" -> "1";
            case "two" -> "2";
            case "three" -> "3";
            case "four" -> "4";
            case "five" -> "5";
            case "six" -> "6";
            case "seven" -> "7";
            case "eight" -> "8";
            case "nine" -> "9";
            default -> digit;
        };
    }

    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day1_test2.txt"), Charsets.UTF_8);
        assertEquals(281, sumOfCalibrationValues(lines));
    }
}
