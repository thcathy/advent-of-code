package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class Day15Part2 {
    Logger log = LoggerFactory.getLogger(Day15Part2.class);
    final static String inputFile = "2023/day15.txt";

    public static void main(String... args) throws IOException {
        Day15Part2 solution = new Day15Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var steps = new Day15Part2().parseInput(lines);
        var result = totalFocusingPower(computeBoxes(steps));
        log.warn("What is the focusing power of the resulting lens configuration? {}", result);
    }

    int totalFocusingPower(Map<Integer, Box> boxes) {
        return boxes.entrySet().stream()
                .mapToInt(e -> focusingPower(e.getKey(), e.getValue().lens()))
                .sum();
    }

    int focusingPower(int boxId, List<Len> lens) {
        int focusingPower = 0;
        for (int i = 0; i < lens.size(); i++) {
            focusingPower += (boxId + 1) * (i + 1) * lens.get(i).focalLength;
         }
        return focusingPower;
    }

    HashMap<Integer, Box> computeBoxes(List<Step> steps) {
        var boxes = new HashMap<Integer, Box>();
        for (var step : steps) {
            switch (step.operation) {
                case '-' -> removeLens(boxes, step);
                case '=' -> addLens(boxes, step);
            }
        }
        return boxes;
    }

    void addLens(HashMap<Integer, Box> boxes, Step step) {
        var box = boxes.getOrDefault(step.boxId, new Box(Collections.emptyList()));
        var lens = new ArrayList<>(box.lens);
        var lenIndex = -1;
        for (int i = 0; i < lens.size(); i++) {
            if (lens.get(i).label.equals(step.label)) {
                lenIndex = i;
                break;
            }

        }
        var newLen = new Len(step.label, step.focalLength);
        if (lenIndex != -1) {
            lens.set(lenIndex, newLen);
        } else {
            lens.add(newLen);
        }
        boxes.put(step.boxId, new Box(lens));
    }

    void removeLens(HashMap<Integer, Box> boxes, Step step) {
        boxes.computeIfPresent(step.boxId, (k, box) -> {
            box.lens.removeIf(l -> l.label.equals(step.label));
            return box;
        });
    }

    int hash(String s) {
        int value = 0;
        for (int i : s.toCharArray()) {
            value += i;
            value *= 17;
            value = value % 256;
        }
        return value;
    }

    //region Data Objects

    record Len(String label, int focalLength) {}

    record Box(List<Len> lens) {}

    record Step(String label, int boxId, char operation, int focalLength) {}
    
    //endregion

    //region Input Parsing

    List<Step> parseInput(List<String> inputs) {
        return Arrays.stream(inputs.getFirst().split(","))
                .map(s -> {
                    var indexOfDash = s.indexOf('-');
                    if (indexOfDash >= 0 ) {
                        var label = s.substring(0, s.length() - 1);
                        return new Step(label, hash(label), '-', -1);
                    } else {
                        var parts = s.split("=");
                        return new Step(parts[0], hash(parts[0]), '=', Integer.parseInt(parts[1]));
                    }
                }).toList();
    }

    //endregion

    @Test
    public void unitTest() throws IOException {        
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day15_test.txt"), Charsets.UTF_8);
        var steps = new Day15Part2().parseInput(lines);
        var result = totalFocusingPower(computeBoxes(steps));
        Assert.assertEquals(145, result);
    }
}
