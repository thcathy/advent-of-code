package com.adventofcode.year2024;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day3Part1 {
    Logger log = LoggerFactory.getLogger(this.getClass());
    final static String inputFile = "2024/day3.txt";

    public static void main(String... args) throws IOException {
        new Day3Part1().run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var input = parseInput(lines);
        var result = calculateTotalMultiplications(input);
        log.warn("What do you get if you add up all of the results of the multiplications? {}", result);
    }

    private String parseInput(List<String> lines) {
        return lines.getFirst();
    }

    private int calculateTotalMultiplications(String memory) {
        int total = 0;
        Pattern pattern = Pattern.compile("mul\\s*\\(\\s*(\\d{1,3})\\s*,\\s*(\\d{1,3})\\s*\\)");
        Matcher matcher = pattern.matcher(memory);

        while (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            total += x * y;
        }

        return total;
    }
}
