package com.adventofcode.year2022;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.IntStream.range;

// reference https://github.com/SimonBaars/AdventOfCode-Java/blob/master/src/main/java/com/sbaars/adventofcode/year22/days/Day16.java
public class Day16Part2 {
    final static String inputFile = "2022/day16.txt";
    final static int TIMEOUT = 26; // 30 minutes

    public static void main(String... args) throws IOException {
        Day16Part2 solution = new Day16Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = mostPressureReleased(parseInput(lines));
        System.out.println("With you and an elephant working together for 26 minutes, what is the most pressure you could release? " + result);
    }

    public record Valve(String name, long flow, List<String> others) {}

    public record State(Map<String, Long> open, Valve me, Valve elephant, long totalFlow) {}

    public long mostPressureReleased(Map<String, Valve> valves) {
        Set<String> openable = valves.values().stream().filter(s -> s.flow > 0).map(Valve::name).collect(Collectors.toSet());
        Set<State> states = new HashSet<>();
        states.add(new State(new HashMap<>(), valves.get("AA"), valves.get("AA"), 0));
        Map<Integer, Long> kpis = Map.of(5, 15L, 10, 50L, 15, 100L, 20, 140L, 25, 160L);
        for (int minutes = 0; minutes < TIMEOUT; minutes++) {
            Set<State> newStates = new HashSet<>();
            for (State s : states) {
                long flow = s.open.values().stream().mapToLong(e -> e).sum() + s.totalFlow;
                if (s.open.size() == openable.size()) { // All valves are open, time to chill
                    newStates.add(new State(s.open, valves.get("AA"), valves.get("AA"), flow));
                }
                int nStates = newStates.size();
                newStates.addAll(openValve(s.me, s.elephant, false, valves, s, flow));
                newStates.addAll(openValve(s.elephant, s.me, false, valves, s, flow));
                newStates.addAll(openValve(s.me, s.elephant, true, valves, s, flow));
                if (newStates.size() == nStates) { // If there are no valves to be opened, we walk
                    allPairs(s.me.others, s.elephant.others)
                            .forEach(p -> newStates.add(new State(s.open, valves.get(p.getLeft()), valves.get(p.getRight()), flow)));
                }
            }
            states = newStates;
            if (kpis.containsKey(minutes)) {
                long kpi = kpis.get(minutes);
                states = states.stream().filter(e -> e.open.values().stream().mapToLong(f -> f).sum() >= kpi).collect(Collectors.toSet());
            }
        }
        return states.stream().mapToLong(State::totalFlow).max().getAsLong();
    }

    public static <A, B> Stream<Pair<A, B>> allPairs(List<A> l1, List<B> l2) {
        return range(0, l1.size()).boxed().flatMap(i -> l2.stream().map(b -> Pair.of(l1.get(i), b)));
    }

    private List<State> openValve(Valve v1, Valve v2, boolean both, Map<String, Valve> valves, State s, long flow) {
        if (v1.flow > 0 && !s.open.containsKey(v1.name) && (!both || (v2.flow > 0 && !s.open.containsKey(v2.name)))) {
            Map<String, Long> newOpen = new HashMap<>(s.open);
            newOpen.put(v1.name, v1.flow);
            if (both) {
                newOpen.put(v2.name, v2.flow);
                return List.of(new State(newOpen, v1, v2, flow));
            }
            return v2.others.stream().map(name -> new State(newOpen, v1, valves.get(name), flow)).toList();
        }
        return List.of();
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
