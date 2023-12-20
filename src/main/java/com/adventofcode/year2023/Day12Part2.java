package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class Day12Part2 {
    Logger log = LoggerFactory.getLogger(Day12Part2.class);
    final static String inputFile = "2023/day12.txt";

    public static void main(String... args) throws IOException {
        Day12Part2 solution = new Day12Part2();
        solution.run();
    }
    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var question = new Day12Part2().parseInput(lines);
        var result = question.totalArrangements();
        log.warn("What is the sum of those counts? {}", result);
    }

    List<ConditionalRecord> conditionalRecords = new ArrayList<>();

    Day12Part2 parseInput(List<String> inputs) {
        for (var input : inputs) {
            var parts = input.split(" ");
            var conditions = String.join("?", Collections.nCopies(5, parts[0]));
            var contiguousGroups = Arrays.stream(parts[1].split(",")).map(Integer::parseInt).toList();
            var repeatedGroups = Collections.nCopies(5, contiguousGroups).stream().flatMap(Collection::stream).toList();

            conditionalRecords.add(new ConditionalRecord(conditions, repeatedGroups));
        }
        return this;
    }


    long totalArrangements() {
        return conditionalRecords.parallelStream()
                .mapToLong(r -> r.findArrangementsCached(new MatchContext(r.conditions, 0), new HashMap<>()))
                .sum();
    }

    record MatchContext(String pattern, int groupPosition) {}

    record ConditionalRecord(String conditions, List<Integer> contiguousGroups) {

        long findArrangementsCached(MatchContext matchContext, Map<MatchContext, Long> cache) {
            if (!cache.containsKey(matchContext))
                cache.put(matchContext, findArrangements(matchContext, cache));
            return cache.get(matchContext);
        }

        long findArrangements(MatchContext matchContext, Map<MatchContext, Long> cache) {
            if (matchContext.pattern.isEmpty()) {
                return matchContext.groupPosition >= contiguousGroups.size() ? 1 : 0;
            }
            return switch (matchContext.pattern.charAt(0)) {
                case '.' -> processDot(matchContext, cache);
                case '?' -> processQuestionMark(matchContext, cache);
                case '#' -> processHash(matchContext, cache);
                default -> 0;
            };
        }

        long processHash(MatchContext matchContext, Map<MatchContext, Long> cache) {
            if (matchContext.groupPosition >= contiguousGroups.size()) return 0;

            int requiredLength = contiguousGroups.get(matchContext.groupPosition);
            long notDotLength = matchContext.pattern.indexOf('.');
            if (notDotLength == -1) notDotLength = matchContext.pattern.length();

            if (notDotLength < requiredLength) {
                return 0;
            } else if (matchContext.pattern.length() == requiredLength) {
                return findArrangementsCached(new MatchContext("", matchContext.groupPosition + 1), cache);
            } else if (matchContext.pattern.charAt(requiredLength) == '#') {
                return 0;
            } else {
                return findArrangementsCached(new MatchContext(matchContext.pattern.substring(requiredLength+1), matchContext.groupPosition + 1), cache);
            }
        }

        long processQuestionMark(MatchContext matchContext, Map<MatchContext, Long> cache) {
            return findArrangementsCached(new MatchContext("." + matchContext.pattern.substring(1), matchContext.groupPosition), cache)
                    + findArrangementsCached(new MatchContext("#" + matchContext.pattern.substring(1), matchContext.groupPosition), cache);
        }

        long processDot(MatchContext matchContext, Map<MatchContext, Long> cache) {
            return findArrangementsCached(new MatchContext(matchContext.pattern.substring(1), matchContext.groupPosition), cache);
        }

    }

    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day12_test.txt"), Charsets.UTF_8);
        var question = new Day12Part2().parseInput(lines);
        assertEquals(525152, question.totalArrangements());
    }
}
