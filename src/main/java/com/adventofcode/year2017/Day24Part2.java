package com.adventofcode.year2017;


import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day24Part2 {
    final static String inputFile = "2017/day24_1.txt";
    
    public static void main(String... args) throws Exception {
        Day24Part2 solution = new Day24Part2();
        solution.run();
    }

    void run() throws Exception {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = strengthOfLongestBridge(parseComponent(lines));
        System.out.println("What is the strength of the strongest bridge you can make? " + result);
    }

    int strengthOfLongestBridge(List<Component> components) {
        Map<Integer, Integer> maxStrengthPerLength = new HashMap<>();
        findMaxStrengthPerLength(components, new boolean[components.size()], 0, 0, 0, maxStrengthPerLength);
        int maxLength = maxStrengthPerLength.keySet().stream().mapToInt(k -> k).max().getAsInt();
        return maxStrengthPerLength.get(maxLength);
    }

    void findMaxStrengthPerLength(List<Component> components, boolean[] tookComponents, int inputPins, int strength, int length, Map<Integer, Integer> maxStrengthPerLength) {        
        for (int i = 0; i < components.size(); i++) {
            if (!tookComponents[i]) {
                Component component = components.get(i);
                if (component.port1 == inputPins || components.get(i).port2 == inputPins) {
                    boolean[] nextTookComponents = tookComponents.clone();
                    nextTookComponents[i] = true;
                    int nextPins = (component.port1 == inputPins) ? component.port2 : component.port1;
                    findMaxStrengthPerLength(components, nextTookComponents, nextPins, strength + component.strength(), length + 1, maxStrengthPerLength);
                }
            }
        }
        int maxStregthInSameLength = Math.max(strength, maxStrengthPerLength.getOrDefault(length, 0));
        maxStrengthPerLength.put(length, maxStregthInSameLength);
    }

    List<Component> parseComponent(List<String> inputs) {
        return inputs.stream().map(s -> s.split("/"))
                .map(arr -> new Component(Integer.valueOf(arr[0]), Integer.valueOf(arr[1])))
                .toList();
    }

    record Component(int port1, int port2) {
        int strength() { return port1 + port2; }
    }

    @Test
    public void unitTest() throws Exception {        
        var lines = Resources.readLines(ClassLoader.getSystemResource("2017/day24_test.txt"), Charsets.UTF_8);        
        assertEquals(19, strengthOfLongestBridge(parseComponent(lines)));
    }

}
