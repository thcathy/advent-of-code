package com.adventofcode.year2016;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Day20Part1 {
    Logger log = LoggerFactory.getLogger(Day20Part1.class);
    String inputFile = "2016/day20_1.txt";

    public static void main(String... args) throws Exception {
        Day20Part1 solution = new Day20Part1();
        solution.run();
    }

    void run() throws Exception {
        firstStar();
    }

    void firstStar() throws IOException {
        var inputs = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var ip = firstValidIP(inputs);

        log.warn("First star - what is the lowest-valued IP that is not blocked? {}", ip);
    }

    long firstValidIP(List<String> inputs) {
        var blacklist = convertToRange(inputs);
        return LongStream.range(0, Long.MAX_VALUE).parallel()
                .filter(l -> isValidIP(l, blacklist))
                .findFirst()
                .getAsLong();
    }

    private List<Range> convertToRange(List<String> inputs) {
        return inputs.stream()
                .map(l -> l.split("-"))
                .map(values -> new Range(Long.valueOf(values[0]), Long.valueOf(values[1])))
                .collect(Collectors.toList());
    }

    boolean isValidIP(long ip, List<Range> blacklist) {
        return blacklist.stream().allMatch(l -> ip < l.low || ip > l.high);
    }

    class Range {
        long low, high;

        public Range(long low, long high) {
            this.low = low;
            this.high = high;
        }
    }

    @Test
    public void test_isValidIP() {
        var blacklist = List.of(
                new Range(5, 8),
                new Range(0, 2),
                new Range(4, 7));
        assertTrue(isValidIP(3, blacklist));
        assertTrue(isValidIP(9, blacklist));
        assertFalse(isValidIP(4, blacklist));
    }

    @Test
    public void test_firstValidIP() {
        long result = firstValidIP(List.of("5-8","0-2","4-7"));
        assertEquals(3, result);
    }
}