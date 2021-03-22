package com.adventofcode.year2019;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class Day2Part1 {
    Logger log = LoggerFactory.getLogger(Day2Part1.class);
    final static int[] input = new int[]{1,0,0,3,1,1,2,3,1,3,4,3,1,5,0,3,2,13,1,19,1,5,19,23,2,10,23,27,1,27,5,31,2,9,31,35,1,35,5,39,2,6,39,43,1,43,5,47,2,47,10,51,2,51,6,55,1,5,55,59,2,10,59,63,1,63,6,67,2,67,6,71,1,71,5,75,1,13,75,79,1,6,79,83,2,83,13,87,1,87,6,91,1,10,91,95,1,95,9,99,2,99,13,103,1,103,6,107,2,107,6,111,1,111,2,115,1,115,13,0,99,2,0,14,0};

    public static void main(String... args) throws IOException {
        Day2Part1 solution = new Day2Part1();
        solution.run();
    }

    void run() throws IOException {
        input[1] = 12;
        input[2] = 2;
        program(input);
        log.warn("What value is left at position 0 = {}", input[0]);
    }

    void operation(int[] input, int opcodePosition) {
        int opcode = input[opcodePosition], position1 = input[opcodePosition + 1], position2 = input[opcodePosition + 2], resultPosition = input[opcodePosition + 3];
        if (opcode == 1) {
            input[resultPosition] = input[position1] + input[position2];
        } else if (opcode == 2) {
            input[resultPosition] = input[position1] * input[position2];
        }
    }

    void program(int[] input) {
        int position = 0;
        while (input[position] != 99) {
            operation(input, position);
            position += 4;
        }
    }

    @Test
    public void program_testcases() {
        var input = new int[]{1, 9, 10, 3, 2, 3, 11, 0, 99, 30, 40, 50};
        program(input);
        assertEquals(input[0], 3500);

        var input2 = new int[]{1,1,1,4,99,5,6,0,99};
        program(input2);
        assertEquals(input2[0], 30);
        assertEquals(input2[4], 2);
    }

    @Test
    public void operation_testcases() {
        var input = new int[]{1, 9, 10, 3, 2, 3, 11, 0, 99, 30, 40, 50};
        operation(input, 0);
        assertEquals(input[3], 70);
        operation(input, 4);
        assertEquals(input[0], 3500);

        var input2 = new int[]{1, 0, 0, 0, 99};
        operation(input2, 0);
        assertEquals(input2[0], 2);

        var input3 = new int[]{2,3,0,3,99};
        operation(input3, 0);
        assertEquals(input3[3], 6);

        var input4 = new int[]{2,4,4,5,99,0};
        operation(input4, 0);
        assertEquals(input4[5], 9801);
    }
}
