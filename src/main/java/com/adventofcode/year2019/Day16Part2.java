package com.adventofcode.year2019;


import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
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
        var signal = extractMessage(lines.get(0));
        log.warn("what is the eight-digit message embedded in the final output list? {}", signal);
    }

    String extractMessage(String input) {
        var offset = Integer.valueOf(input.substring(0, 7));
        var signal = createSignal(String.join("", Collections.nCopies(10000, input)), offset);

        for(int i = 1; i <= 100; i++) {
            int result = 0;
            for(int position = signal.length - 1; position >= 0; position--) {
                result += signal[position];
                result = Math.abs(result%10);
                signal[position] = result;
            }
        }
        return Arrays.toString(Arrays.copyOfRange(signal, 0, 8)).replaceAll("\\[|,|]| ","");
    }

    int[] createSignal(String inputSignal, int offset) {
        return IntStream.range(0, inputSignal.length())
                .skip(offset)
                .map(index -> Character.getNumericValue(inputSignal.charAt(index)))
                .toArray();
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
    public void extractMessage_testcases() {
        assertEquals("84462026", extractMessage("03036732577212944063491565474664"));
        assertEquals("78725270", extractMessage("02935109699940807407585447034323"));
        assertEquals("53553731", extractMessage("03081770884921959731165446850517"));
    }
}
