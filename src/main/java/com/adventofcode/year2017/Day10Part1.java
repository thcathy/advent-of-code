package com.adventofcode.year2017;


import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.stream.IntStream;

public class Day10Part1 {

    final static int[] puzzleInput = new int[] {183,0,31,146,254,240,223,150,2,206,161,1,255,232,199,88};

    public static void main(String... args) throws IOException {
        Day10Part1 solution = new Day10Part1();
        solution.run();
    }

    void run() throws IOException {
        var list = IntStream.rangeClosed(0, 255).toArray();
        var result = puzzleOutput(list, puzzleInput);
        System.out.printf("what is the result of multiplying the first two numbers in the list? %d %n", result);
    }

    int puzzleOutput(int[] list, int[] puzzleInput) {
        int skipSize = 0;
        int currentPosition = 0;
        for (int input : puzzleInput) {
            int start = currentPosition;
            int end = start + input - 1;
            while (start < end) {
                swap(list, start, end);
                start++;
                end--;
            }
            currentPosition = (currentPosition + input + skipSize) % list.length;
            skipSize++;
        }
        return list[0] * list[1];
    }

    void swap(int[] array, int pos1, int pos2) {
        pos1 = pos1 % array.length;
        pos2 = pos2 % array.length;
        int temp = array[pos1];
        array[pos1] = array[pos2];
        array[pos2] = temp;
    }

    @Test
    public void unitTest() {
        Assert.assertEquals(12, puzzleOutput(IntStream.rangeClosed(0, 4).toArray(), new int[] {3, 4, 1, 5}));
    }
}
