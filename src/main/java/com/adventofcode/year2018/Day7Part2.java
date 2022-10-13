package com.adventofcode.year2018;


import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day7Part2 {
    final static String inputFile = "2018/day7.txt";
    final static List<Character> EMPTY_LIST = new ArrayList<>();
    
    public static void main(String... args) throws IOException {
        Day7Part2 solution = new Day7Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = secondToComplete(parseNextSteps(lines), parseDependency(lines), 5, 60);
        System.out.println("how long will it take to complete all of the steps? " + result);
    }

    int secondToComplete(
        Map<Character, List<Character>> nextSteps,
        Map<Character, List<Character>> dependencies,
        int numOfWorkers, int baseDuration
    ) {
        int second = 0;       
        var queue = new PriorityQueue<Character>();
        var completedSteps = new HashSet<Character>();
        var workers = createWorkers(numOfWorkers);        
        queue.addAll(firstSteps(nextSteps));
        int totalSteps = totalSteps(nextSteps);

        while (completedSteps.size() < totalSteps) {
            for (Worker w : workers) {
                w.secondToComplete--;

                if (w.jobCompleted()) {
                    completedSteps.add(w.step);
                    for (Character nextStep : nextSteps.getOrDefault(w.step, EMPTY_LIST)) {
                        if (completedSteps.containsAll(dependencies.get(nextStep))) {
                            queue.add(nextStep);
                        }
                    }
                    w.step = null;
                }
            }
            
            for (Worker w : workers) {
                if (w.isFree() && queue.size() > 0) {
                    w.step = queue.remove();
                    w.secondToComplete = baseDuration + durationOf(w.step);
                }
            }            
            second++;            
        }
        
        return second-1;
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

    int totalSteps(Map<Character, List<Character>> stepsMap) {
        var steps = new HashSet<>(stepsMap.keySet());
        stepsMap.values().stream().flatMap(l -> l.stream()).forEach(s -> steps.add(s));
        return steps.size();
    }

    Set<Character> firstSteps(Map<Character, List<Character>> dependencies) {
        var steps = new HashSet<>(dependencies.keySet());
        dependencies.values().stream().flatMap(l -> l.stream()).forEach(s -> steps.remove(s));
        return steps;
    }

    int durationOf(char c) { return c - 64;}

    List<Worker> createWorkers(int size) {
        return IntStream.rangeClosed(1, size).mapToObj(i -> new Worker()).toList();
    }
    
    class Worker {
        int secondToComplete = 0;
        Character step = null;

        boolean isFree() {
            return step == null;
        }

        boolean jobCompleted() {
            return step != null && secondToComplete <= 0;
        }
    }
        
    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2018/day7_test.txt"), Charsets.UTF_8);
        var nextSteps = parseNextSteps(lines);
        var dependencies = parseDependency(lines);
        assertEquals(26, durationOf('Z'));
        assertEquals(15, secondToComplete(nextSteps, dependencies, 2, 0));
    }
}
