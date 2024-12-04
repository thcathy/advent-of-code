package com.adventofcode.year2024;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class Day1Part2 {
    Logger log = LoggerFactory.getLogger(this.getClass());
    final static String inputFile = "2024/day1.txt";

    public static void main(String... args) throws IOException {
        new Day1Part2().run();
    }

    List<Integer> leftList = new ArrayList<>();
    List<Integer> rightList = new ArrayList<>();

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        parseInput(lines);
        var result = calculateSimilarityScore(leftList, rightList);;
        log.warn("What is their similarity score? {}", result);
    }

    private void parseInput(List<String> lines) {
        for (String line : lines) {
            String[] parts = line.trim().split("\\s+");
            leftList.add(Integer.parseInt(parts[0]));
            rightList.add(Integer.parseInt(parts[1]));
        }
    }

    public static int calculateSimilarityScore(List<Integer> leftList, List<Integer> rightList) {
        Map<Integer, Integer> rightCountMap = new HashMap<>();
        for (Integer number : rightList) {
            rightCountMap.put(number, rightCountMap.getOrDefault(number, 0) + 1);
        }

        int similarityScore = 0;
        for (Integer number : leftList) {
            int countInRight = rightCountMap.getOrDefault(number, 0);
            similarityScore += number * countInRight;
        }

        return similarityScore;
    }
}
