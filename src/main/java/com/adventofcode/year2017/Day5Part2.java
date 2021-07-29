package com.adventofcode.year2017;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class Day5Part2 {
    Logger log = LoggerFactory.getLogger(Day5Part2.class);
    final static String inputFile = "2017/day5_1.txt";

    public static void main(String... args) throws IOException {
        Day5Part2 solution = new Day5Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = runInstruction(convertToArray(lines));
        log.warn("How many steps does it take to reach the exit? {}", result);
    }

    int runInstruction(int[] instructions) {
        int steps = 0, position = 0;
        while (position > -1 && position < instructions.length) {
            int value = instructions[position];
            if (value >= 3) {
                instructions[position]--;
            } else {
                instructions[position]++;
            }
            position += value;
            steps++;
        }
        return steps;
    }

    int[] convertToArray(List<String> inputs) {
        int[] result = new int[inputs.size()];
        for (int i=0; i<inputs.size(); i++) {
            result[i] = Integer.parseInt(inputs.get(i));
        }
        return result;
    }

}
