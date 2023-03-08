package com.adventofcode.year2022;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.adventofcode.year2022.Day21Part2.Operator.*;
import static org.junit.Assert.assertEquals;

public class Day21Part2 {
    final static String inputFile = "2022/day21.txt";
    public static final String HUMAN_CODE = "humn";

    public static void main(String... args) throws IOException {
        Day21Part2 solution = new Day21Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = findHumanValue(parseInput(lines));
        System.out.println("What number do you yell to pass root's equality test? " + result);
    }

    long findHumanValue(Map<String, Monkey> monkeys) {
        HashMap<String, Long> yellValues = yellValuesWithoutHuman(monkeys);

        var root = monkeys.get("root");
        long equalValue;
        Monkey monkeyContainHumanValue;
        if (yellValues.containsKey(root.monkey1)) {
            equalValue = yellValues.get(root.monkey1);
            monkeyContainHumanValue = monkeys.get(root.monkey2);
        } else {
            equalValue = yellValues.get(root.monkey2);
            monkeyContainHumanValue = monkeys.get(root.monkey1);
        }

        return findHumanValue(monkeys, yellValues, equalValue, monkeyContainHumanValue);
    }

    private static long findHumanValue(Map<String, Monkey> monkeys, HashMap<String, Long> yellValue, long equalValue, Monkey monkeyContainHumanValue) {
        while (true) {
            var value = yellValue.containsKey(monkeyContainHumanValue.monkey1) ? yellValue.get(monkeyContainHumanValue.monkey1) : yellValue.get(monkeyContainHumanValue.monkey2);
            String nextMonkeyName = null;
            if (yellValue.containsKey(monkeyContainHumanValue.monkey1)) {
                equalValue = switch (monkeyContainHumanValue.operator) {
                    case Add -> equalValue - value;
                    case Minus -> value - equalValue;
                    case Multiply -> equalValue / value;
                    case Divide -> value / equalValue;
                };
                nextMonkeyName = monkeyContainHumanValue.monkey2;
            } else {
                equalValue = switch (monkeyContainHumanValue.operator) {
                    case Add -> equalValue - value;
                    case Minus -> value + equalValue;
                    case Multiply -> equalValue / value;
                    case Divide -> value * equalValue;
                };
                nextMonkeyName = monkeyContainHumanValue.monkey1;
            }
            if (nextMonkeyName.equals(HUMAN_CODE)) return equalValue;
            monkeyContainHumanValue = monkeys.get(nextMonkeyName);
        }
    }

    private static HashMap<String, Long> yellValuesWithoutHuman(Map<String, Monkey> monkeys) {
        var yellValue = new HashMap<String, Long>();
        for (Monkey m : monkeys.values()) {
            try {
                yellValue.put(m.name, m.yell(monkeys));
            } catch (Exception e) {}
        }
        yellValue.remove(HUMAN_CODE);
        return yellValue;
    }

    Map<String, Monkey> parseInput(List<String> inputs) {
        var monkeys = new HashMap<String, Monkey>();
        for (String string : inputs) {
            var input = string.split(" ");
            var name = input[0].replace(":", "");
            try {
                var value = Long.parseLong(input[1]);
                monkeys.put(name, new Monkey(name, Optional.of(value), null, null, null));
            } catch (NumberFormatException nfe) {
                monkeys.put(name, new Monkey(name, Optional.empty(), input[1], parseOperator(input[2]), input[3]));
            }
        }
        return monkeys;
    }

    Operator parseOperator(String i) {
        return switch (i) {
            case "+" -> Add;
            case "-" -> Minus;
            case "*" -> Multiply;
            case "/" -> Divide;
            default -> throw new RuntimeException();
        };
    }

    record Monkey(String name, Optional<Long> value, String monkey1, Operator operator, String monkey2) {
        long yell(Map<String, Monkey> monkeys) {
            if (HUMAN_CODE.equals(monkey1) || HUMAN_CODE.equals(monkey2)) throw new RuntimeException();
            return value.orElseGet(() -> calculate(monkeys.get(monkey1).yell(monkeys), operator, monkeys.get(monkey2).yell(monkeys)));
        }

        long calculate(long v1, Operator operator, long v2) {
            return switch (operator) {
                case Add -> v1 + v2;
                case Minus -> v1 - v2;
                case Multiply -> v1 * v2;
                case Divide -> v1 / v2;
            };
        }
    }

    enum Operator { Add, Minus, Multiply, Divide }

    @Test
    public void unitTest() throws Exception {
        var lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day21_test.txt").toURI()));
        var monkeys = parseInput(lines);
        assertEquals(4, monkeys.get("lfqf").value.get().longValue());
        var ptdq = monkeys.get("ptdq");
        assertEquals("humn", ptdq.monkey1);
        assertEquals(Minus, ptdq.operator);
        assertEquals("dvpt", ptdq.monkey2);

        assertEquals(301, findHumanValue(monkeys));
    }

}
