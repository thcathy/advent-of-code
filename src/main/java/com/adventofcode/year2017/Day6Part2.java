package com.adventofcode.year2017;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Day6Part2 {
    Logger log = LoggerFactory.getLogger(Day6Part2.class);
    final static String inputFile = "2017/day6_1.txt";

    public static void main(String... args) throws IOException {
        Day6Part2 solution = new Day6Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = countCyclesBetweenPattern(convertToArray(lines));
        log.warn("How many cycles are in the infinite loop that arises from the configuration in your puzzle input? {}", result);
    }

    private int[] convertToArray(List<String> lines) {
        var strings = lines.get(0).split("\\s");
        int[] result = new int[strings.length];
        for (int i=0; i<strings.length; i++) {
            result[i] = Integer.parseInt(strings[i]);
        }
        return result;
    }

    int countCyclesBetweenPattern(int[] banks) {
        int count = 0;
        var patterns = new HashMap<String, Integer>();
        String pattern = "";
        while(true) {
            count++;
            banks = redistribution(banks);
            pattern = Arrays.toString(banks);
            if (patterns.containsKey(pattern))
                return count - patterns.get(pattern);
            patterns.put(pattern, count);
        }
    }

    int[] redistribution(int[] banks) {
        int position = positionWithMostBlocks(banks);
        int blocks = banks[position];
        banks[position] = 0;
        while (blocks > 0) {
            blocks--;
            position++;
            if (position >= banks.length) position = 0;
            banks[position]++;
        }
        return banks;
    }

    int positionWithMostBlocks(int[] banks) {
        int position = 0;
        int maxBlocks = banks[0];
        for (int i=1; i<banks.length; i++) {
            if (banks[i] > maxBlocks) {
                maxBlocks = banks[i];
                position = i;
            }
        }
        return position;
    }

}
