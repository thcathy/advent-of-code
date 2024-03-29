package com.adventofcode.year2022;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Day11Part1 {
    final static String inputFile = "2022/day11.txt";
    final static int ROUNDS = 20;

    public static void main(String... args) throws IOException {
        Day11Part1 solution = new Day11Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = monkeyBusinessLevel(parseMonkeys(lines));
        System.out.println("What is the level of monkey business after 20 rounds of stuff-slinging simian shenanigans? " + result);
    }

    int monkeyBusinessLevel(List<Monkey> monkeys) {
        for (int i = 0; i < ROUNDS; i++) {
            nextRound(monkeys);
        }
        monkeys.sort(Comparator.comparingInt(m -> m.inspectedItems));
        return monkeys.get(monkeys.size() - 1).inspectedItems * monkeys.get(monkeys.size() - 2).inspectedItems;
    }

    void nextRound(List<Monkey> monkeys) {
        for (Monkey monkey : monkeys) {
            monkey.inspectedItems += monkey.items.size();
            for (int item : monkey.items) {
                item = monkey.operation.apply(item) / 3;
                var throwTo = monkey.test.apply(item) ? monkey.testSuccessMonkey : monkey.testFailMonkey; 
                monkeys.get(throwTo).items.add(item);
            }
            monkey.items.clear();
        }
    }

    class Monkey {
        int id;
        Queue<Integer> items = new LinkedList<>();
        Function<Integer, Integer> operation;
        Function<Integer, Boolean> test;
        int testSuccessMonkey;
        int testFailMonkey;
        int inspectedItems = 0;
    }

    List<Monkey> parseMonkeys(List<String> inputs) {
        var list = new ArrayList<Monkey>();
        for (int i = 0; i < inputs.size(); i += 7) {
            list.add(parseMonkey(inputs.subList(i, i+6)));
        }
        return list;
    }

    Function<Integer, Integer> parseOperation(String[] inputs) {
        if (inputs[7].equals("old")) {
            return switch (inputs[6]) {
                case "*" -> (v) -> v * v;
                case "+" -> (v) -> v + v;
                case "-" -> (v) -> 0;
                default -> throw new RuntimeException();
            };
        } else {
            var x = Integer.parseInt(inputs[7]);
            return switch (inputs[6]) {
                case "*" -> (v) -> v * x;
                case "+" -> (v) -> v + x;
                case "-" -> (v) -> v - x;
                default -> throw new RuntimeException();
            };
        }
    }

    Monkey parseMonkey(List<String> inputs) {
        var monkey = new Monkey();
        monkey.id = Integer.parseInt(inputs.get(0).split(" ")[1].replace(":", ""));
        for (var string : inputs.get(1).replace("  Starting items: ", "").split(", ")) {
            monkey.items.add(Integer.parseInt(string));
        }
        monkey.operation = parseOperation(inputs.get(2).split(" "));
        monkey.test = (v) -> v % Integer.parseInt(inputs.get(3).split(" ")[5]) == 0;
        monkey.testSuccessMonkey = Integer.parseInt(inputs.get(4).split(" ")[9]);
        monkey.testFailMonkey = Integer.parseInt(inputs.get(5).split(" ")[9]);
        return monkey;
    }

    @Test
    public void unitTest() throws Exception {
        var lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day11_test.txt").toURI()), Charset.defaultCharset());
        var monkeys = parseMonkeys(lines);
        assertEquals(4, monkeys.size());
        var monkey0 = monkeys.get(0);
        assertEquals(0, monkey0.id);
        assertEquals(190, monkey0.operation.apply(10).intValue());
        assertTrue( monkey0.test.apply(46));
        assertEquals(2, monkey0.testSuccessMonkey);
        assertEquals(3, monkey0.testFailMonkey);

        nextRound(monkeys);
        assertEquals(4, monkeys.get(0).items.size());
        assertEquals(6, monkeys.get(1).items.size());
        assertEquals(0, monkeys.get(2).items.size());
        assertEquals(0, monkeys.get(3).items.size());

        assertEquals(10605, monkeyBusinessLevel(parseMonkeys(lines)));
    }
}
