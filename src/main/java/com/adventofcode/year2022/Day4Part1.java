package com.adventofcode.year2022;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day4Part1 {
    Logger log = LoggerFactory.getLogger(Day4Part1.class);
    final static String inputFile = "2022/day4.txt";

    public static void main(String... args) throws IOException {
        Day4Part1 solution = new Day4Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = fullyContainPairs(lines);
        System.out.println("In how many assignment pairs does one range fully contain the other? " + result);
    }

    long fullyContainPairs(List<String> inputs) {
        return inputs.stream().map(this::toRangePair)
            .filter(pair -> isFullyContain(pair.range1, pair.range2) || isFullyContain(pair.range2, pair.range1))
            .count();
    }

    RangePair toRangePair(String input) {
        var pairs = input.split(",");
        return new RangePair(toRange(pairs[0]), toRange(pairs[1]));
    }

    Range toRange(String input) {
        var numbers = input.split("-");
        return new Range(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1]));
    }

    record RangePair(Range range1, Range range2) {}

    record Range(int start, int end) {}

    boolean isFullyContain(Range inner, Range outer) {
        return outer.start <= inner.start && inner.start <= outer.end
            && outer.start <= inner.end && inner.end <= outer.end; 
    }

    @Test
    public void unitTest() throws IOException {
        assertTrue(isFullyContain(new Range(3, 7), new Range(2, 8)));
        assertFalse(isFullyContain(new Range(2, 4), new Range(6, 8)));
    }
}
