package com.adventofcode.year2017;


import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.stream.IntStream;

public class Day10Part2 {

    final static String puzzleInput = "183,0,31,146,254,240,223,150,2,206,161,1,255,232,199,88";

    public static void main(String... args) throws IOException {
        Day10Part2 solution = new Day10Part2();
        solution.run();
    }

    void run() throws IOException {
        var result = hash(puzzleInput);
        System.out.printf("what is the result of multiplying the first two numbers in the list? %s %n", result);
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
        StringBuffer sb = new StringBuffer();
        for (int v : values) {
            sb.append(toHex(v));
        }
        return sb.toString();
    }

    String toHex(int value) {
        var string = Integer.toHexString(value);
        return string.length() == 1 ? "0" + string : string;
    }

    @Test
    public void unitTest() {
        Assert.assertEquals("4007ff", toHex(new int[] {64,7, 255}));
        Assert.assertEquals(64, denseHash(new int[] {65,27,9,1,4,3,40,50,91,7,6,0,2,5,68,22})[0]);
        Assert.assertEquals("33efeb34ea91902bb2f59c9920caa6cd", hash("AoC 2017"));
    }
}
