package com.adventofcode.year2022;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day1Part1 {
    Logger log = LoggerFactory.getLogger(Day1Part1.class);
    final static String inputFile = "2022/day1.txt";

    public static void main(String... args) throws IOException {
        Day1Part1 solution = new Day1Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = mostCalories(lines);
        log.warn("How many total Calories is that Elf carrying? {}", result);
    }

    public int mostCalories(List<String> inputs) {
        List<Integer> caloriesOfElfs = new ArrayList<>();
        int sum = 0;
        for (String s : inputs) {
            if ("".equals(s)) {
                caloriesOfElfs.add(sum);
                sum = 0;
            } else {
                sum += Integer.parseInt(s);
            }
        }
        return caloriesOfElfs.stream().mapToInt(v -> v).max().getAsInt();
    }

    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2022/day1_test.txt"), Charsets.UTF_8);
        assertEquals(24000, mostCalories(lines));
    }
}
