package com.adventofcode.year2022;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day10Part1 {
    final static String inputFile = "2022/day10.txt";
    final static List<Integer> CYCLES_TO_CAPTURE = List.of(20, 60, 100, 140, 180, 220);

    public static void main(String... args) throws IOException {
        Day10Part1 solution = new Day10Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = sumOfSignalStrengths(lines);
        System.out.println("What is the sum of these six signal strengths? " + result);
    }

    int sumOfSignalStrengths(List<String> inputs) {
        int sum = 0;
        int cycle = 0;
        int x = 1;

        for (String s : inputs) {
            if ("noop".equals(s)) {
                cycle++;
                if (CYCLES_TO_CAPTURE.contains(cycle)) sum += x * cycle;
            } else if (s.startsWith("addx")) {
                cycle++;
                if (CYCLES_TO_CAPTURE.contains(cycle)) sum += x * cycle;
                cycle++;
                if (CYCLES_TO_CAPTURE.contains(cycle)) sum += x * cycle;
                x += Integer.parseInt(s.split(" ")[1]);
            }
        }

        return sum;
    } 


    @Test
    public void unitTest() throws Exception {
        var lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day10_test.txt").toURI()), Charset.defaultCharset());
        
        assertEquals(13140, sumOfSignalStrengths(lines));
    }
}
