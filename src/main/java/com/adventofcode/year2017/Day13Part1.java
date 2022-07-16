package com.adventofcode.year2017;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day13Part1 {
    final static String inputFile = "2017/day13_1.txt";

    public static void main(String... args) throws IOException {
        Day13Part1 solution = new Day13Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = totalSeverity(parseFirewallFromInput(lines));
        System.out.printf("what is the severity of your whole trip? %d %n", result);
    }

    int totalSeverity(Map<Integer, Integer> firewall) {
        int totalSeverity = 0;
        for (Map.Entry<Integer, Integer> layer : firewall.entrySet()) {
            int depth = layer.getKey();
            int range = layer.getValue();

            int pos = depth % (2 * range - 2);
            if (pos > (range-1)) {
                pos = (range-1) - (pos - range + 1);
            }
            if (pos == 0) {
                totalSeverity += depth * range;
            }
        }
        return totalSeverity;
    }

    Map<Integer, Integer> parseFirewallFromInput(List<String> inputs) {
        return inputs.stream()
                .map(s -> s.split(": "))
                .collect(Collectors.toMap(arr -> Integer.parseInt(arr[0]), arr -> Integer.parseInt(arr[1])));
    }
}
