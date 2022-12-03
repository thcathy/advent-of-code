package com.adventofcode.year2022;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day3Part1 {
    Logger log = LoggerFactory.getLogger(Day3Part1.class);
    final static String inputFile = "2022/day3.txt";

    public static void main(String... args) throws IOException {
        Day3Part1 solution = new Day3Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = sumOfPrioritiesOfShareItems(lines);
        System.out.println("What is the sum of the priorities of those item types? " + result);
    }

    int sumOfPrioritiesOfShareItems(List<String> inputs) {
        int sum = 0;
        for (String input : inputs) {
            var shareItems = shareItems(input);
            sum += shareItems.stream().mapToInt(this::prioritesOf).sum();
        }
        return sum;
    }

    Set<Character> shareItems(String input) {
        int halfSize = input.length() / 2;
        Set<Character> compartment1 = new HashSet<>();
        Set<Character> compartment2 = new HashSet<>();
        for (int i = 0; i < halfSize; i++) {
            compartment1.add(input.charAt(i));
            compartment2.add(input.charAt(i + halfSize));
        }
        compartment1.retainAll(compartment2);
        return compartment1;
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

        assertEquals(1, shareItems("vJrwpWtwJgWrhcsFMMfFFhFp").size());
        assertTrue(shareItems("vJrwpWtwJgWrhcsFMMfFFhFp").contains('p'));
        assertTrue(shareItems("jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL").contains('L'));

        var lines = Resources.readLines(ClassLoader.getSystemResource("2022/day3_test.txt"), Charsets.UTF_8);
        assertEquals(157, sumOfPrioritiesOfShareItems(lines));
    }
}
