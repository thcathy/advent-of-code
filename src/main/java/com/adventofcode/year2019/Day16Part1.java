package com.adventofcode.year2019;


import java.io.IOException;
import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.assertEquals;

public class Day16Part1 {
    static Logger log = LoggerFactory.getLogger(Day16Part1.class);
    final static String inputFile = "2019/day16_1.txt";

    public static void main(String... args) throws IOException {
        Day16Part1 solution = new Day16Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var pattern = new int[] { 0, 1, 0, -1 };
        var signal = afterPhase(convertToIntArray(lines.get(0)),pattern, 100);
        log.warn("what are the first eight digits in the final output list? {}", Arrays.toString(Arrays.copyOf(signal, 8)).replaceAll("\\[|,|]| ",""));
    }


    int[] nextPhase(int[] input, int[] pattern) {
        return IntStream.range(0, input.length)
                .map(i -> nextDigit(i, input, pattern))
                .toArray();
    }

    int nextDigit(int position, int[] input, int[] pattern) {
        return Math.abs(IntStream.range(0, input.length)
                .map(i -> input[i] * pattern[(i+1)/(position+1) % pattern.length])
                .sum() % 10);
    }

    int[] convertToIntArray(String input) {
        return input.chars().map(c -> c-48).toArray();
    }

    int[] afterPhase(int[] input, int[] pattern, int phases) {
        for (int i = 0; i < phases; i++) {
            input = nextPhase(input, pattern);
        }
        return input;
    }

    @Test
    public void nextPhase_testcases() {
        var signal = convertToIntArray("12345678");
        var pattern = new int[] { 0, 1, 0, -1 };
        signal = nextPhase(signal, pattern);
        assertEquals("48226158", Arrays.toString(signal).replaceAll("\\[|,|]| ",""));
        signal = nextPhase(signal, pattern);
        assertEquals("34040438", Arrays.toString(signal).replaceAll("\\[|,|]| ",""));
        signal = nextPhase(signal, pattern);
        assertEquals("03415518", Arrays.toString(signal).replaceAll("\\[|,|]| ",""));
        signal = nextPhase(signal, pattern);
        assertEquals("01029498", Arrays.toString(signal).replaceAll("\\[|,|]| ",""));
    }

    @Test
    public void afterPhase_testcases() {
        var pattern = new int[] { 0, 1, 0, -1 };
        var signal1 = afterPhase(convertToIntArray("80871224585914546619083218645595"),pattern, 100);
        assertEquals("24176176", Arrays.toString(Arrays.copyOf(signal1, 8)).replaceAll("\\[|,|]| ",""));
        var signal2 = afterPhase(convertToIntArray("19617804207202209144916044189917"),pattern, 100);
        assertEquals("73745418", Arrays.toString(Arrays.copyOf(signal2, 8)).replaceAll("\\[|,|]| ",""));
        var signal3 = afterPhase(convertToIntArray("69317163492948606335995924319873"),pattern, 100);
        assertEquals("52432133", Arrays.toString(Arrays.copyOf(signal3, 8)).replaceAll("\\[|,|]| ",""));
    }
}
