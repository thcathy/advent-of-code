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

public class Day16Part2 {
    static Logger log = LoggerFactory.getLogger(Day16Part2.class);
    final static String inputFile = "2019/day16_1.txt";

    public static void main(String... args) throws IOException {
        Day16Part2 solution = new Day16Part2();
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
        int sum = 0;
        int i = 0;
        int patternValue = pattern[0];
        while (i < input.length) {
            if ((i+1) % (position+1) == 0) patternValue = pattern[(i+1)/(position+1) % pattern.length];
            if (patternValue == 0) {
                i += position;
                if (i > 0 && position > 0) i--;
            } else {
                sum += (input[i] * patternValue);
            }
            i++;
        }
        return Math.abs(sum % 10);
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

    String extractMessage(String input, int[] pattern) {
        var offset = Integer.valueOf(input.substring(0, 7));
        var intArray = repeatInput(convertToIntArray(input), 10000);
        var signal = afterPhase(intArray, pattern, 100);
        return Arrays.toString(Arrays.copyOfRange(signal, offset, offset+8)).replaceAll("\\[|,|]| ","");
    }

    int[] repeatInput(int[] input, int times) {
        int[] output = new int[input.length * times];
        for (int i = 0; i < times; i++) {
            for (int j = 0; j < input.length; j++) {
                output[i * j] = input[j];
            }
        }
        return output;
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

    @Test
    public void extractMessage_testcases() {
        var pattern = new int[] { 0, 1, 0, -1 };
        assertEquals("84462026", extractMessage("03036732577212944063491565474664", pattern));
        assertEquals("78725270", extractMessage("02935109699940807407585447034323", pattern));
        assertEquals("53553731", extractMessage("03081770884921959731165446850517", pattern));
    }
}
