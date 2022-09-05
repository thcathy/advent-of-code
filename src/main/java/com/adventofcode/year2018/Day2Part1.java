package com.adventofcode.year2018;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day2Part1 {
    Logger log = LoggerFactory.getLogger(Day2Part1.class);
    final static String inputFile = "2018/day2.txt";

    public static void main(String... args) throws IOException {
        Day2Part1 solution = new Day2Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = checksum(lines);
        log.warn("What is the checksum for your list of box IDs? {}", result);
    }

    public long checksum(List<String> inputs) {
        return boxesWithExactXOfAnyLetter(inputs, 2) * boxesWithExactXOfAnyLetter(inputs, 3);
    }

    long boxesWithExactXOfAnyLetter(List<String> inputs, long matchingCount) {
        return inputs.stream()
                .filter(s -> isExactXOfAnyLetter(s, matchingCount))
                .count();
    }
    
    boolean isExactXOfAnyLetter(String input, long matchingCount) {
        Map<Integer, Long> counts = input.chars().boxed()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return counts.values().contains(matchingCount);
    }

    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2018/day2_test.txt"), Charsets.UTF_8);
        assertEquals(12, checksum(lines));
    }
}
