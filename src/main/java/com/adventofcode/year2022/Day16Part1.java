package com.adventofcode.year2022;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.*;

// reference https://github.com/SimonBaars/AdventOfCode-Java/blob/master/src/main/java/com/sbaars/adventofcode/year22/days/Day16.java
public class Day16Part1 {
    final static String inputFile = "2022/day16.txt";
    final static int TIMEOUT = 30; // 30 minutes

    public static void main(String... args) throws IOException {
        Day16Part1 solution = new Day16Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = mostPressureReleased(parseInput(lines));
        System.out.println("What is the most pressure you can release? " + result);
    }

    public record Valve(String name, long flow, List<String> others) {}

    public record State(Map<String, Long> open, Valve valve, long totalFlow) {}

    public long mostPressureReleased(Map<String, Valve> valves) {
        Set<State> states = new HashSet<>();
        states.add(new State(new HashMap<>(), valves.get("AA"), 0));
        for (int minutes = 0; minutes < TIMEOUT; minutes++) {
            Set<State> newStates = new HashSet<>();
            for (State s : states) {
                long flow = s.open.values().stream().mapToLong(e -> e).sum() + s.totalFlow;
                if (s.valve.flow > 0 && !s.open.containsKey(s.valve.name)) {
                    Map<String, Long> newOpen = new HashMap<>(s.open);
                    newOpen.put(s.valve.name, s.valve.flow);
                    newStates.add(new State(newOpen, s.valve, flow));
                }
                s.valve.others.stream().forEach(name -> newStates.add(new State(s.open, valves.get(name), flow)));
            }
            states = newStates;
        }
        return states.stream().mapToLong(State::totalFlow).max().getAsLong();
    }

    Map<String, Valve> parseInput(List<String> lines) {
        var valves = new HashMap<String, Valve>();

        for (String input : lines) {
            var splittedInput = input.split(";");
            var firstParts = splittedInput[0].split(" ");
            var name = firstParts[1];
            var flowRate = firstParts[4].split("=")[1];
            var secondParts = splittedInput[1].split(" ");
            var leadTo = Arrays.stream(ArrayUtils.subarray(secondParts, 5, secondParts.length))
                    .map(s -> s.replace(",", ""))
                    .toList();

            valves.put(name, new Valve(name, Long.parseLong(flowRate), leadTo));
        }

        return valves;
    }

}
