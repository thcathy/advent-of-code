package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class Day15Part1 {
    Logger log = LoggerFactory.getLogger(Day15Part1.class);
    final static String inputFile = "2023/day15.txt";

    public static void main(String... args) throws IOException {
        Day15Part1 solution = new Day15Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var strings = new Day15Part1().parseInput(lines);
        var result = sumOfHash(strings);
        log.warn("What is the sum of the results? {}", result);
    }

    int sumOfHash(List<String> strings) {
        return strings.stream().mapToInt(this::hash).sum();
    }

    int hash(String s) {
        int value = 0;
        for (int i : s.toCharArray()) {
            value += i;
            value *= 17;
            value = value % 256;
        }
        return value;
    }

    //region Data Objects
    
    //endregion

    //region Input Parsing

    List<String> parseInput(List<String> inputs) {
        return Arrays.asList(inputs.get(0).split(","));
    }

    //endregion

    @Test
    public void unitTest() throws IOException {        
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day15_test.txt"), Charsets.UTF_8);
        var steps = new Day15Part1().parseInput(lines);
        var result = sumOfHash(steps);
        Assert.assertEquals(1320, result);
    }
}
