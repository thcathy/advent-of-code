package com.adventofcode.year2024;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day3Part2 {
    Logger log = LoggerFactory.getLogger(this.getClass());
    final static String inputFile = "2024/day3.txt";

    public static void main(String... args) throws IOException {
        new Day3Part2().run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var input = parseInput(lines);
        var result = calculateTotalMultiplications(input);
        log.warn("what do you get if you add up all of the results of just the enabled multiplications? {}", result);
    }

    private String parseInput(List<String> lines) {return lines.getFirst();}

    private int calculateTotalMultiplications(String memory) {
        int total = 0;
        boolean enabled = true; // Multiplications are enabled by default

        // Regex patterns for mul, do and don't instructions
        Pattern mulPattern = Pattern.compile("mul\\s*\\(\\s*(\\d{1,3})\\s*,\\s*(\\d{1,3})\\s*\\)");
        Pattern doPattern = Pattern.compile("do\\s*\\(\\s*\\)");
        Pattern dontPattern = Pattern.compile("don't\\s*\\(\\s*\\)");

        Matcher matcher = Pattern.compile(mulPattern.pattern() + "|" + doPattern.pattern() + "|" + dontPattern.pattern()).matcher(memory);

        while (matcher.find()) {
            if (matcher.group(1) != null) { // If it's a mul instruction
                if (enabled) {
                    int x = Integer.parseInt(matcher.group(1));
                    int y = Integer.parseInt(matcher.group(2));
                    total += x * y;
                }
            } else if (matcher.group(0).startsWith("don't")) { // If it's a do() instruction
                enabled = false;
            } else if (matcher.group(0).startsWith("do")) { // If it's a don't() instruction
                enabled = true;
            }
        }

        return total;
    }
}
