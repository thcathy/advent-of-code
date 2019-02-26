package com.adventofcode.year2016;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.assertEquals;

public class Day20Part2 {
    Logger log = LoggerFactory.getLogger(Day20Part2.class);
    String inputFile = "2016/day20_1.txt";

    public static void main(String... args) throws Exception {
        Day20Part2 solution = new Day20Part2();
        solution.run();
    }

    void run() throws Exception {
        var inputs = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var ip = totalValidIP(inputs, Long.valueOf("4294967295"));

        log.warn("Second star - How many IPs are allowed by the blacklist? {}", ip);
    }

    long totalValidIP(List<String> inputs, long maxIP) {
        var blacklist = convertToRange(inputs);
        Collections.sort(blacklist, Comparator.comparingLong(a -> a.low));
        var mergedRange = mergeRange(blacklist);

        var offset = maxIP - mergedRange.get(mergedRange.size() - 1).high;
        if (offset < 0) offset = 0;

        return IntStream.range(1, mergedRange.size())
                .mapToLong(i -> mergedRange.get(i).low - mergedRange.get(i-1).high - 1)
                .sum() + offset;
    }

    List<Range> mergeRange(List<Range> blacklist) {
        var merged = new ArrayList<Range>();
        merged.add(blacklist.get(0));

        IntStream.range(1, blacklist.size())
                .mapToObj(blacklist::get)
                .forEach(l -> merge(l, merged));
        return merged;
    }

    void merge(Range l, ArrayList<Range> merged) {
        var lastRange = merged.get(merged.size() - 1);
        if (l.low > lastRange.high)
            merged.add(l);
        else if (l.high > lastRange.high)
            lastRange.high = l.high;
    }

    List<Range> convertToRange(List<String> inputs) {
        return inputs.stream()
                .map(l -> l.split("-"))
                .map(values -> new Range(Long.valueOf(values[0]), Long.valueOf(values[1])))
                .collect(Collectors.toList());
    }

    class Range {
        long low, high;

        public Range(long low, long high) {
            this.low = low;
            this.high = high;
        }
    }

    @Test
    public void test_totalValidIP() {
        long result = totalValidIP(List.of("5-8","0-2","4-7"), 9);
        assertEquals(2, result);
    }
}