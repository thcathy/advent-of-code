package com.adventofcode.year2017;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Day2Part2 {
    Logger log = LoggerFactory.getLogger(Day2Part2.class);
    final static String inputFile = "2017/day2_1.txt";

    public static void main(String... args) throws IOException {
        Day2Part2 solution = new Day2Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = sumAllChecksum(lines);
        log.warn("What is the sum of each row's result in your puzzle input? {}", result);
    }

    public int sumAllChecksum(List<String> input) {
        return input.stream().mapToInt(this::calculateChecksum).sum();
    }

    int calculateChecksum(String line) {
        var values = Arrays.stream(line.split("\\s+")).mapToInt(Integer::valueOf).toArray();
        for (int i=0; i<values.length-1; i++) {
            for (int j=i+1; j<values.length; j++) {
                if (values[i] % values[j] == 0) {
                    return values[i] / values[j];
                } else if (values[j] % values[i] == 0) {
                    return values[j] / values[i];
                }
            }
        }
        throw new RuntimeException("two numbers in each row where one evenly divides the other");
    }

}
