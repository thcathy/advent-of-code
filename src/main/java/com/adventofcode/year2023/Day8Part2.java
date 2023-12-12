package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class Day8Part2 {
    Logger log = LoggerFactory.getLogger(Day8Part2.class);
    final static String inputFile = "2023/day8.txt";

    public static void main(String... args) throws IOException {
        Day8Part2 solution = new Day8Part2();
        solution.run();
    }
    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var puzzle = Puzzle.parse(lines);
        var result = puzzle.stepToAllReachZZZ();
        log.warn("How many steps does it take before you're only on nodes that end with Z? {}", result);
    }

    record Puzzle(List<Character> instructions, Map<String, Node> networks) {
        static Puzzle parse(List<String> inputs) {
            var instructions = inputs.getFirst().chars().mapToObj(e->(char)e).toList();
            var networks = inputs.subList(2, inputs.size()).stream()
                    .map(Node::parse)
                    .collect(Collectors.toMap(n -> n.id, n -> n));
            return new Puzzle(instructions, networks);
        }

        long stepToAllReachZZZ() {
            List<Node> nodes = networks.values().stream().filter(n -> n.id.endsWith("A")).toList();
            return nodes.stream().mapToLong(this::stepToAllZ)
                    .reduce(1, (a, b) -> a * (b / gcd(a, b)));
        }

        public static long gcd(long a, long b) {
            return b == 0 ? a : gcd(b, a % b);
        }

        int stepToAllZ(Node node) {
            int steps = 0;
            while (!node.id.endsWith("Z")) {
                var instruction = instructions.get(steps % instructions.size());
                node = networks.get(node.nextId(instruction));
                steps++;
            }
            return steps;
        }
    }

    record Node(String id, String leftId, String rightId) {
        static Node parse(String input) {
            var parts = input.split(" = ");
            var destinations = parts[1].split(",");
            return new Node(parts[0].trim(), destinations[0].substring(1, 4), destinations[1].substring(1, 4));
        }

        String nextId(Character instruction) {
            return switch (instruction) {
                case 'L' -> leftId;
                case 'R' -> rightId;
                default -> throw new IllegalStateException();
            };
        }
    }
    
    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day8_test2.txt"), Charsets.UTF_8);
        var puzzle = Puzzle.parse(lines);
        assertEquals(6, puzzle.stepToAllReachZZZ());
    }
}
