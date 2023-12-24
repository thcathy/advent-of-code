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
import java.util.stream.IntStream;

public class Day19Part1 {
    Logger log = LoggerFactory.getLogger(Day19Part1.class);
    final static String inputFile = "2023/day19.txt";

    public static void main(String... args) throws IOException {
        Day19Part1 solution = new Day19Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var puzzle = new Day19Part1().parseInput(lines);
        var result = totalRatingNumbers(puzzle);
        log.warn("what do you get if you add together all of the rating numbers? {}", result);
    }

    int totalRatingNumbers(Puzzle puzzle) {
        return puzzle.listOfParts.stream()
                .filter(p -> isAccepted(puzzle.rulesMap, p))
                .mapToInt(Parts::ratingNumber)
                .sum();
    }

    boolean isAccepted(Map<String, RuleList> rulesMap, Parts parts) {
        var ruleList = rulesMap.get("in");
        while (true) {
            var rule = ruleList.rules.stream()
                        .filter(r -> r.condition == null || r.condition.match(parts))
                    .findFirst().get();
            switch (rule.sendTo) {
                case "A" -> { return true; }
                case "R" -> { return false; }
                default -> ruleList = rulesMap.get(rule.sendTo);
            }
        }
    }

    //region Data Objects

    record Puzzle(Map<String, RuleList> rulesMap, List<Parts> listOfParts) {}

    record Parts(Map<Part, Integer> map) {
        int ratingNumber() { return map.values().stream().mapToInt(v -> v).sum(); }
    }

    record RuleList(String id, List<Rule> rules) {}

    record Rule(Condition condition, String sendTo) {}

    record Condition(Part part, char operator, int value) {
        boolean match(Parts parts) {
            var partValue = parts.map.get(part);
            return switch (operator) {
                case '>' -> partValue > value;
                case '<' -> partValue < value;
                default -> false;
            };
        }
    }

    enum Part { x, m, a, s }
    
    //endregion

    //region Input Parsing

    Puzzle parseInput(List<String> inputs) {
        var index = 0;
        var rulesMap = new HashMap<String, RuleList>();
        while (StringUtils.isNotBlank(inputs.get(index))) {
            var ruleSet = parseRuleSet(inputs.get(index));
            rulesMap.put(ruleSet.id, ruleSet);
            index++;
        }
        var listOfParts = IntStream.range(index + 1, inputs.size())
                .mapToObj(inputs::get)
                .map(this::parseParts).toList();
        return new Puzzle(rulesMap, listOfParts);
    }

    Parts parseParts(String input) {
        var inputs = input.substring(1, input.length() - 1).split(",");
        var partMap = new HashMap<Part, Integer>();
        for (String part : inputs) {
            partMap.put(
                    Part.valueOf(part.substring(0, 1)),
                    Integer.parseInt(part.substring(2))
            );
        }
        return new Parts(partMap);
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
        var puzzle = new Day19Part1().parseInput(lines);
        var result = totalRatingNumbers(puzzle);
        Assert.assertEquals(19114, result);
    }
}
