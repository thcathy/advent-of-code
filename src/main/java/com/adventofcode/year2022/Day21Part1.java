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

import static com.adventofcode.year2022.Day21Part1.Operator.*;
import static org.junit.Assert.assertEquals;

public class Day21Part1 {
    final static String inputFile = "2022/day21.txt";

    public static void main(String... args) throws IOException {
        Day21Part1 solution = new Day21Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = rootYell(parseInput(lines));
        System.out.println("What number will the monkey named root yell? " + result);
    }

    long rootYell(Map<String, Monkey> monkeys) {
        return monkeys.get("root").yell(monkeys);
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

        assertEquals(32, monkeys.get("hmdt").yell(monkeys));
        assertEquals(30, monkeys.get("drzm").yell(monkeys));
        assertEquals(150, monkeys.get("sjmn").yell(monkeys));
        assertEquals(152, monkeys.get("root").yell(monkeys));
    }

}
