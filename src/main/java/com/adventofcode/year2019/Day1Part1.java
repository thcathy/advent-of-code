package com.adventofcode.year2019;


import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.assertEquals;

public class Day1Part1 {
    Logger log = LoggerFactory.getLogger(Day1Part1.class);
    final static String inputFile = "2019/day1_1.txt";

    public static void main(String... args) throws IOException {
        Day1Part1 solution = new Day1Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = lines.stream()
                        .map(Integer::valueOf)
                        .mapToInt(this::fuelRequiredForModule)
                        .sum();
        log.warn("What is the sum of the fuel requirements = {}", result);
    }

    public int fuelRequiredForModule(int mass) {
        return mass / 3 - 2;
    }

    @Test
    public void testcases() {
        assertEquals(2, fuelRequiredForModule(12));
        assertEquals(2, fuelRequiredForModule(14));
        Assert.assertEquals(654, fuelRequiredForModule(1969));
        Assert.assertEquals(33583, fuelRequiredForModule(100756));
    }
}
