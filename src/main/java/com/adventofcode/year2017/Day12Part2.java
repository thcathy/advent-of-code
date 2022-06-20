package com.adventofcode.year2017;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.util.*;

public class Day12Part2 {
    final static String inputFile = "2017/day12_1.txt";

    public static void main(String... args) throws IOException {
        Day12Part2 solution = new Day12Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = totalGroups(programMap(lines));
        System.out.printf("How many groups are there in total? %d %n", result);
    }

    int totalGroups(Map<Integer, Set<Integer>> programMap) {
        Set<Integer> groupedPrograms = new HashSet<>();
        int totalGroups = 0;
        for (Map.Entry<Integer, Set<Integer>> entry : programMap.entrySet()) {
            if (groupedPrograms.contains(entry.getKey())) continue;

            groupedPrograms.addAll(findGroup(programMap, entry.getKey()));
            totalGroups++;
        }
        return totalGroups;
    }
    Set<Integer> findGroup(Map<Integer, Set<Integer>> programMap, int programId) {
        Set<Integer> group = new HashSet<>();
        List<Integer> programs = new LinkedList<>();
        programs.add(programId);
        while (!programs.isEmpty()) {
            var program = programs.remove(0);
            if (!group.contains(program)) {
                group.add(program);
                programs.addAll(programMap.get(program));
            }
        }
        return group;
    }

    Map<Integer, Set<Integer>> programMap(List<String> inputs) {
        Map<Integer, Set<Integer>> map = new HashMap<>();
        for (String input : inputs) {
            var programs = input.split(" <-> ");
            int keyProgram = Integer.parseInt(programs[0].trim());
            for (String programString : programs[1].split(",")) {
                int program = Integer.parseInt(programString.trim());
                map.computeIfAbsent(keyProgram, v -> new HashSet<>());
                map.get(keyProgram).add(program);
                map.computeIfAbsent(program, v -> new HashSet<>());
                map.get(program).add(keyProgram);
            }
        }
        return map;
    }

}
