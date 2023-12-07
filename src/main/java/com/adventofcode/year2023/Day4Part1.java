package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class Day4Part1 {
    Logger log = LoggerFactory.getLogger(Day4Part1.class);
    final static String inputFile = "2023/day4.txt";

    public static void main(String... args) throws IOException {
        Day4Part1 solution = new Day4Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = sumOfPoints(lines);
        log.warn("How many points are they worth in total? {}", result);
    }

    int sumOfPoints(List<String> inputs) {
        return inputs.stream().map(this::parseCard)
                .mapToInt(this::calculatePoints)
                .sum();
    }

    int calculatePoints(Card card) {
        int point = 0;
        for (var number : card.owningNumbers) {
            if (card.winningNumbers.contains(number)) {
                point = point == 0 ? 1 : point * 2;
            }
        }
        return point;
    }

    Card parseCard(String input) {
        var parts = input.split("\\|");
        var winningNumbers = Arrays.stream(parts[0].split(": ")[1].split(" "))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
        var owningNumbers = Arrays.stream(parts[1].split(" "))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(Integer::parseInt)
                .toList();
        return new Card(winningNumbers, owningNumbers);
    }

    record Card(Set<Integer> winningNumbers, List<Integer> owningNumbers) {}

    
    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day4_test.txt"), Charsets.UTF_8);
        assertEquals(13, sumOfPoints(lines));
    }
}
