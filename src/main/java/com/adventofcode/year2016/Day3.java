package com.adventofcode.year2016;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class Day3 {
    Logger log = LoggerFactory.getLogger(Day3.class);
    final static String inputFile = "2016/day3_1.txt";

    public static void main(String... args) throws IOException {
        Day3 solution = new Day3();
        solution.firstStar();
        solution.secondStar();
    }

    void firstStar() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var count = lines.stream()
                .filter(s -> isValidTriangle(toIntegerList(s)))
                .count();

        log.warn("First star - number of valid triangle: {}", count);
    }

    void secondStar() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var inputs = parseInputVertically(lines);
        var count = inputs.stream()
                .filter(i -> isValidTriangle(i))
                .count();

        log.warn("Second star - number of valid triangle: {}", count);
    }

    boolean isValidTriangle(List<Integer> input) {
        Collections.sort(input);
        return input.get(0) + input.get(1) > input.get(2);
    }

    List<List<Integer>> parseInputVertically(List<String> input) {
        var results = new LinkedList<List<Integer>>();
        var row = 0;
        while (row < input.size()) {
            var line1 = toIntegerList(input.get(row));
            var line2 = toIntegerList(input.get(row+1));
            var line3 = toIntegerList(input.get(row+2));

            results.add(Lists.newArrayList(line1.get(0), line2.get(0), line3.get(0)));
            results.add(Lists.newArrayList(line1.get(1), line2.get(1), line3.get(1)));
            results.add(Lists.newArrayList(line1.get(2), line2.get(2), line3.get(2)));

            row += 3;
        }
        return results;
    }

    List<Integer> toIntegerList(String input) {
        return Arrays.stream(input.split(" "))
                .filter(StringUtils::isNotBlank)
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    @Test
    public void isValidTriangle_givenInvalidInput_shouldReturnFalse() {
        String input = "5 10 25";
        assertEquals(false, isValidTriangle(toIntegerList(input)));
    }

    @Test
    public void parseInputVertically_isWork() {
        var input = Lists.newArrayList(
                "101 301 501",
                "102 302 502",
                "103 303 503",
                "201 401 601",
                "202 402 602",
                "203 403 603");
        var results = parseInputVertically(input);
        assertEquals(6, results.size());
        assertEquals(101, results.get(0).get(0).intValue());
        assertEquals(102, results.get(0).get(1).intValue());
        assertEquals(103, results.get(0).get(2).intValue());
    }

}