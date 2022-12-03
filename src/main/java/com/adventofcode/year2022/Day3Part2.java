package com.adventofcode.year2022;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day3Part2 {
    Logger log = LoggerFactory.getLogger(Day3Part2.class);
    final static String inputFile = "2022/day3.txt";

    public static void main(String... args) throws IOException {
        Day3Part2 solution = new Day3Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = sumOfPrioritiesOfShareItems(lines);
        System.out.println("What is the sum of the priorities of those item types? " + result);
    }

    int sumOfPrioritiesOfShareItems(List<String> inputs) {
        int sum = 0;
        for (int i = 0; i < inputs.size(); i += 3) {
            sum += prioritesOf(shareItem(inputs.subList(i, i+3)));
        }
        return sum;
    }

    char shareItem(List<String> inputs) {
        Set<Character> set1 = toSet(inputs.get(0));
        Set<Character> set2 = toSet(inputs.get(1));
        Set<Character> set3 = toSet(inputs.get(2));
        set1.retainAll(set2);
        set1.retainAll(set3);
        return set1.iterator().next();
    }

    Set<Character> toSet(String input) {
        return input.chars().mapToObj(e->(char)e).collect(Collectors.toSet());
    }

    int prioritesOf(char c) {
        if (Character.isUpperCase(c)) {
            return (int) c - 38;
        } else {
            return (int) c - 96;
        }
    }

    @Test
    public void unitTest() throws IOException {
        assertEquals(16, prioritesOf('p'));
        assertEquals(38, prioritesOf('L'));

        var lines = Resources.readLines(ClassLoader.getSystemResource("2022/day3_test.txt"), Charsets.UTF_8);
        assertEquals(70, sumOfPrioritiesOfShareItems(lines));
    }
}
