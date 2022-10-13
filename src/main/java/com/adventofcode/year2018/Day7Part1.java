package com.adventofcode.year2018;


import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day7Part1 {
    final static String inputFile = "2018/day7.txt";
    final static List<Character> EMPTY_LIST = new ArrayList<>();
    
    public static void main(String... args) throws IOException {
        Day7Part1 solution = new Day7Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = orderOfSteps(parseNextSteps(lines), parseDependency(lines));
        System.out.println("In what order should the steps in your instructions be completed? " + result);
    }

    String orderOfSteps(Map<Character, List<Character>> nextSteps, Map<Character, List<Character>> dependencies) {
        var sb = new StringBuilder();
        var queue = new PriorityQueue<Character>();
        var completedSteps = new HashSet<Character>();
        queue.addAll(firstSteps(nextSteps));
        while (!queue.isEmpty()) {
            var step = queue.remove();
            sb.append(step);
            completedSteps.add(step);
            for (Character nextStep : nextSteps.getOrDefault(step, EMPTY_LIST)) {
                if (completedSteps.containsAll(dependencies.get(nextStep))) {
                    queue.add(nextStep);
                }
            }            
        }
        return sb.toString();
    }

    Map<Character, List<Character>> parseNextSteps(List<String> inputs) {
        var nextSteps = new HashMap<Character, List<Character>>();
        for (String input : inputs) {
            var step = input.charAt(5);
            nextSteps.computeIfAbsent(step, (k) -> new ArrayList<Character>());
            nextSteps.get(step).add(input.charAt(36));
        }
        return nextSteps;
    }

    Map<Character, List<Character>> parseDependency(List<String> inputs) {
        var dependencies = new HashMap<Character, List<Character>>();
        for (String input : inputs) {
            var step = input.charAt(36);
            dependencies.computeIfAbsent(step, (k) -> new ArrayList<Character>());
            dependencies.get(step).add(input.charAt(5));
        }
        return dependencies;
    }

    Set<Character> firstSteps(Map<Character, List<Character>> dependencies) {
        var steps = new HashSet<>(dependencies.keySet());
        dependencies.values().stream().flatMap(l -> l.stream()).forEach(s -> steps.remove(s));
        return steps;
    }
        
    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2018/day7_test.txt"), Charsets.UTF_8);
        var nextSteps = parseNextSteps(lines);
        var dependencies = parseDependency(lines);
        assertEquals(1, firstSteps(nextSteps).size());
        assertEquals("CABDFE", orderOfSteps(nextSteps, dependencies));
    }
}
