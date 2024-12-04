package com.adventofcode.year2024;

import com.adventofcode.year2023.Day14Part1;
import com.adventofcode.year2023.Day21Part1;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Day1Part1 {
    Logger log = LoggerFactory.getLogger(this.getClass());
    final static String inputFile = "2024/day1.txt";

    public static void main(String... args) throws IOException {
        new Day1Part1().run();
    }

    List<Integer> leftList = new ArrayList<>();
    List<Integer> rightList = new ArrayList<>();

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        parseInput(lines);
        var result = calculateTotalDistance(leftList, rightList);
        log.warn("What is the total distance between your lists? {}", result);
    }

    private void parseInput(List<String> lines) {
        for (String line : lines) {
            String[] parts = line.trim().split("\\s+");
            leftList.add(Integer.parseInt(parts[0]));
            rightList.add(Integer.parseInt(parts[1]));
        }
    }

    public static int calculateTotalDistance(List<Integer> leftList, List<Integer> rightList) {
        Collections.sort(leftList);
        Collections.sort(rightList);
        int totalDistance = 0;
        for (int i = 0; i < leftList.size(); i++) {
            totalDistance += Math.abs(leftList.get(i) - rightList.get(i));
        }

        return totalDistance;
    }
}
