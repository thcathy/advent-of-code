package com.adventofcode.year2018;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day1Part2 {
    Logger log = LoggerFactory.getLogger(Day1Part2.class);
    final static String inputFile = "2018/day1.txt";

    public static void main(String... args) throws IOException {
        Day1Part2 solution = new Day1Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = firstDuplicateFrequency(lines);
        log.warn("What is the first frequency your device reaches twice? {}", result);
    }

    public int firstDuplicateFrequency(List<String> inputs) {
        int frequency = 0;
        Set<Integer> frequencies = new HashSet<>();
        frequencies.add(frequency);
        while (true) {
            for (String s : inputs) {
                frequency += Integer.parseInt(s);
                if (frequencies.contains(frequency)) {
                    return frequency;
                }
                frequencies.add(frequency);
            }   
        }
             
    }

}
