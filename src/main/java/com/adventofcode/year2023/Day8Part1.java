package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class Day8Part1 {
    Logger log = LoggerFactory.getLogger(Day8Part1.class);
    final static String inputFile = "2023/day8.txt";

    public static void main(String... args) throws IOException {
        Day8Part1 solution = new Day8Part1();
        solution.run();
    }
    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var puzzle = Puzzle.parse(lines);
        var result = puzzle.stepToReachZZZ();
        log.warn("How many steps are required to reach ZZZ? {}", result);
    }

    record Puzzle(List<Character> instructions, Map<String, Node> networks) {
        static Puzzle parse(List<String> inputs) {
            var instructions = inputs.getFirst().chars().mapToObj(e->(char)e).toList();
            var networks = inputs.subList(2, inputs.size()).stream()
                    .map(Node::parse)
                    .collect(Collectors.toMap(n -> n.id, n -> n));
            return new Puzzle(instructions, networks);
        }

        int stepToReachZZZ() {
            String nodeId = "AAA";
            int steps = 0;
            while (!"ZZZ".equals(nodeId)) {
                Node node = networks.get(nodeId);
                nodeId = switch (instructions.get(steps % instructions.size())) {
                    case 'L' -> node.leftId;
                    case 'R' -> node.rightId;
                    default -> throw new IllegalStateException();
                };
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
    }
    
    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day8_test.txt"), Charsets.UTF_8);
        var puzzle = Puzzle.parse(lines);
        assertEquals(2, puzzle.stepToReachZZZ());
    }
}
