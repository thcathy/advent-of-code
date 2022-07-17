package com.adventofcode.year2017;


import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class Day14Part1 {
    final static String puzzleInput = "amgozmfv";

    public static void main(String... args) throws IOException {
        Day14Part1 solution = new Day14Part1();
        solution.run();
    }

    void run() throws IOException {
        var result = totalUsedSpace(puzzleInput);
        System.out.printf("how many squares are used? %s %n", result);
    }

    int totalUsedSpace(String input) {
        return IntStream.range(0, 128)
                .mapToObj(i -> input + "-" + i)
                .map(this::hash)
                .mapToInt(this::stringToUsedSpace)
                .sum();
    }
    String hash(String input) {
        int[] inputArray = ArrayUtils.addAll(input.chars().toArray(), 17, 31, 73, 47, 23);
        int[] sparseHash = sparseHash(inputArray);
        return toHex(denseHash(sparseHash));
    }

    int[] sparseHash(int[] inputs) {
        int[] intArray = IntStream.rangeClosed(0, 255).toArray();
        int skipSize = 0;
        int currentPosition = 0;

        for (int i=0; i<64; i++) {
            for (int input : inputs) {
                int start = currentPosition;
                int end = start + input - 1;
                while (start < end) {
                    swap(intArray, start, end);
                    start++;
                    end--;
                }
                currentPosition = (currentPosition + input + skipSize) % intArray.length;
                skipSize++;
            }
        }

        return intArray;
    }

    void swap(int[] array, int pos1, int pos2) {
        pos1 = pos1 % array.length;
        pos2 = pos2 % array.length;
        int temp = array[pos1];
        array[pos1] = array[pos2];
        array[pos2] = temp;
    }

    int[] denseHash(int[] input) {
        int[] result = new int[input.length / 16];
        for (int i=0; i < result.length; i++) {
            int start = i * 16;
            int value = input[start];
            for (int j=1; j < 16; j++) {
                value = value ^ input[start + j];
            }
            result[i] = value;
        }
        return result;
    }

    String toHex(int[] values) {
        StringBuilder sb = new StringBuilder();
        for (int v : values) {
            sb.append(toHex(v));
        }
        return sb.toString();
    }

    String toHex(int value) {
        var string = Integer.toHexString(value);
        return string.length() == 1 ? "0" + string : string;
    }

    int stringToUsedSpace(String hexString) {
        int usedSpace = 0;
        for (char c : hexString.toCharArray()) {
            usedSpace += hexToUsedSpace(c);
        }
        return usedSpace;
    }

    int hexToUsedSpace(char hex) {
        return switch (hex) {
            case '0' -> 0;
            case '1', '2', '4', '8' -> 1;
            case '3', '5', '6', '9', 'a', 'c' -> 2;
            case '7', 'b', 'd', 'e' -> 3;
            case 'f' -> 4;
            default -> throw new IllegalStateException("Unexpected value: " + hex);
        };
    }

    @Test
    public void unitTest() {
        assertEquals(8108, totalUsedSpace("flqrgnkx"));
    }
}
