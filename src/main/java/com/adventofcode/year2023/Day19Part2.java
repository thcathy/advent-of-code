package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class Day19Part2 {
    Logger log = LoggerFactory.getLogger(Day19Part2.class);
    final static String inputFile = "2023/day19.txt";

    public static void main(String... args) throws IOException {
        Day19Part2 solution = new Day19Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var puzzle = new Day19Part2().parseInput(lines);
        var result = totalCombinations(puzzle);
        log.warn("How many distinct combinations of ratings will be accepted? {}", result);
    }

    long totalCombinations(Map<String, RuleList> rulesMap) {
        var acceptedParts = new ArrayList<Parts>();
        var partsToTest = new ArrayDeque<Parts>();
        partsToTest.add(defaultParts());

        while (!partsToTest.isEmpty()) {
            var parts = partsToTest.poll();
            if ("A".equals(parts.sendTo)) {
                acceptedParts.add(parts);
            }
            else if (!"R".equals(parts.sendTo)) {
                var ruleList = rulesMap.get(parts.sendTo);
                var nextParts = checkParts(ruleList, parts);
                partsToTest.addAll(nextParts);
            }
        }

        return acceptedParts.stream().mapToLong(Parts::combinations).sum();
    }

    Parts defaultParts() {
        return new Parts(
                Map.of(
                        Part.x, new Range(1, 4000),
                        Part.m, new Range(1, 4000),
                        Part.a, new Range(1, 4000),
                        Part.s, new Range(1, 4000)),
            "in");
    }

    List<Parts> checkParts(RuleList rules, Parts parts) {
        var nextParts = new ArrayList<Parts>();

        for (var rule : rules.rules) {
            if (rule.condition == null) {
                nextParts.add(new Parts(parts.map, rule.sendTo));
            } else if (rule.condition.match(parts)) {
                var matchedRange = rule.condition.matchedRange(parts);
                var matchedParts = new Parts(nextParts(parts, rule.condition, matchedRange), rule.sendTo);
                nextParts.add(matchedParts);

                var unmatchedRange = rule.condition.unmatchedRange(parts);
                parts = new Parts(nextParts(parts, rule.condition, unmatchedRange), parts.sendTo);
            }
        }
        return nextParts;
    }

    Map<Part, Range> nextParts(Parts parts, Condition condition, Range newRange) {
        var newParts = new HashMap<>(parts.map);
        newParts.put(condition.part, newRange);
        return newParts;
    }

    //region Data Objects

    record Parts(Map<Part, Range> map, String sendTo) {
        long combinations() {
            return map.values().stream()
                    .mapToLong(range -> range.length() + 1)
                    .reduce(1, (a, b) -> a * b );
        }
    }

    record Range(int low, int high) {
        int length() { return high - low; }
    }
    record RuleList(String id, List<Rule> rules) {}

    record Rule(Condition condition, String sendTo) {}

    record Condition(Part part, char operator, int value) {
        boolean match(Parts parts) {
            var partRange = parts.map.get(part);
            return switch (operator) {
                case '>' -> partRange.high > value;
                case '<' -> partRange.low < value;
                default -> false;
            };
        }

        Range matchedRange(Parts parts) {
            var range = parts.map.get(part);
            return switch (operator) {
                case '<' -> new Range(range.low, value - 1);
                case '>' -> new Range(value + 1, range.high);
                default -> throw new IllegalStateException("Unexpected value: " + operator);
            };
        }

        Range unmatchedRange(Parts parts) {
            var range = parts.map.get(part);
            return switch (operator) {
                case '<' -> new Range(value, range.high);
                case '>' -> new Range(range.low, value);
                default -> throw new IllegalStateException("Unexpected value: " + operator);
            };
        }
    }

    enum Part { x, m, a, s }
    
    //endregion

    //region Input Parsing

    Map<String, RuleList> parseInput(List<String> inputs) {
        var index = 0;
        var rulesMap = new HashMap<String, RuleList>();
        while (StringUtils.isNotBlank(inputs.get(index))) {
            var ruleSet = parseRuleSet(inputs.get(index));
            rulesMap.put(ruleSet.id, ruleSet);
            index++;
        }
        return rulesMap;
    }

    RuleList parseRuleSet(String input) {
        var inputs = input.split("\\{");
        var rules = Arrays.stream(
                        inputs[1].substring(0, inputs[1].length() - 1).split(",")
                ).map(this::parseRule).toList();
        return new RuleList(inputs[0], rules);
    }

    Rule parseRule(String input) {
        var inputs = input.split(":");
        if (inputs.length == 1) {
            return new Rule(null, input);   // default rule
        }
        var part = Part.valueOf(input.substring(0, 1));
        var value = Integer.parseInt(inputs[0].substring(2));
        return new Rule(new Condition(part, input.charAt(1), value), inputs[1]);
    }

    //endregion

    @Test
    public void unitTest() throws IOException {        
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day19_test.txt"), Charsets.UTF_8);
        var puzzle = new Day19Part2().parseInput(lines);
        var result = totalCombinations(puzzle);
        Assert.assertEquals(167409079868000L, result);
    }
}
