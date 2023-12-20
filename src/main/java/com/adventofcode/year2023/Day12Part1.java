package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day12Part1 {
    Logger log = LoggerFactory.getLogger(Day12Part1.class);
    final static String inputFile = "2023/day12.txt";

    public static void main(String... args) throws IOException {
        Day12Part1 solution = new Day12Part1();
        solution.run();
    }
    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var question = new Day12Part1().parseInput(lines);
        var result = question.totalArrangements();
        log.warn("What is the sum of those counts? {}", result);
    }

    List<ConditionalRecord> conditionalRecords = new ArrayList<>();

    Day12Part1 parseInput(List<String> inputs) {
        for (var input : inputs) {
            var parts = input.split(" ");
            var contiguousGroups = Arrays.stream(parts[1].split(","))
                    .map(Integer::parseInt).toList();
            conditionalRecords.add(new ConditionalRecord(parts[0], contiguousGroups));
        }
        return this;
    }

    int totalArrangements() {
        return conditionalRecords.stream().mapToInt(this::totalArrangement).sum();
    }

    int totalArrangement(ConditionalRecord conditionalRecord) {
        if (conditionalRecord.noUnknownCondition()) return conditionalRecord.matchArrangement() ? 1 : 0;

        var replaceToOperational = new ConditionalRecord(conditionalRecord.conditions.replaceFirst("\\?", "."), conditionalRecord.contiguousGroups);
        var replaceToDamaged = new ConditionalRecord(conditionalRecord.conditions.replaceFirst("\\?", "#"), conditionalRecord.contiguousGroups);

        return totalArrangement(replaceToOperational) + totalArrangement(replaceToDamaged);
    }

    record ConditionalRecord(String conditions, List<Integer> contiguousGroups) {
        boolean noUnknownCondition() {
            return !conditions.contains("?");
        }
        boolean matchArrangement() {
            var groups = Arrays.stream(conditions.split("\\."))
                    .map(String::length).filter(l -> l > 0).toList();
            return groups.equals(contiguousGroups);
        }
    }

    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day12_test.txt"), Charsets.UTF_8);
        var question = new Day12Part1().parseInput(lines);
        Assert.assertEquals(21, question.totalArrangements());
    }
}
