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

public class Day10Part2 {
    final static String inputFile = "2022/day10.txt";

    public static void main(String... args) throws IOException {
        Day10Part2 solution = new Day10Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = CRTString(lines);
        System.out.println("What eight capital letters appear on your CRT?");
        printCRT(result);
    }

    void printCRT(String string) {
        for (int i = 0; i < 6; i++) {
            System.out.println(string.substring(i * 40, (i+1) * 40));
        }
    }

    String CRTString(List<String> inputs) {
        var state = new State();

        for (String s : inputs) {
            if ("noop".equals(s)) {
                nextCycle(state);
            } else if (s.startsWith("addx")) {
                nextCycle(state);
                nextCycle(state);
                state.spritePosition += Integer.parseInt(s.split(" ")[1]);
            }
        }

        return state.sb.toString();
    } 

    void nextCycle(State state) {
        state.cycle++;
        state.sb.append(onSprite(state.cycle, state.spritePosition) ? '#' : '.');
    }

    boolean onSprite(int p, int spritePosition) {
        return spritePosition <= p % 40 && p % 40 <= spritePosition + 2;
    }

    class State {
        int sum = 0;
        int cycle = 0;
        int spritePosition = 1;
        StringBuilder sb = new StringBuilder();
    }


    @Test
    public void unitTest() throws Exception {
        var lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day10_test.txt").toURI()), Charset.defaultCharset());
        var string = CRTString(lines);
        assertEquals("##..##..##..##..##..##..##..##..##..##..", string.substring(0, 40));
        assertEquals("###...###...###...###...###...###...###.", string.substring(40, 80));
    }
}
