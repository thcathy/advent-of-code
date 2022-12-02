package com.adventofcode.year2022;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Day1Part2 {
    Logger log = LoggerFactory.getLogger(Day1Part1.class);
    final static String inputFile = "2022/day1.txt";

    public static void main(String... args) throws IOException {
        Day1Part2 solution = new Day1Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = sumOftopThreeCalories(lines);
        log.warn("How many Calories are those Elves carrying in total? {}", result);
    }

    public int sumOftopThreeCalories(List<String> inputs) {
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
        caloriesOfElfs.add(sum);
        Collections.sort(caloriesOfElfs);
        return caloriesOfElfs.get(caloriesOfElfs.size()-1) + caloriesOfElfs.get(caloriesOfElfs.size()-2) + caloriesOfElfs.get(caloriesOfElfs.size()-3);
    }

    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2022/day1_test.txt"), Charsets.UTF_8);
        assertEquals(45000, sumOftopThreeCalories(lines));
    }
}
