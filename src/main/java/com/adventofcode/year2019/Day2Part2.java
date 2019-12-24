package com.adventofcode.year2019;


import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class Day2Part2 {
    Logger log = LoggerFactory.getLogger(Day2Part2.class);

    public static void main(String... args) throws IOException {
        Day2Part2 solution = new Day2Part2();
        solution.run();
    }

    void run() throws IOException {
        for (int input1 = 0; input1 <= 99; input1++) {
            for (int input2 = 0; input2 <= 99; input2++) {
                int[] input = new int[]{1,0,0,3,1,1,2,3,1,3,4,3,1,5,0,3,2,10,1,19,1,6,19,23,2,23,6,27,1,5,27,31,1,31,9,35,2,10,35,39,1,5,39,43,2,43,10,47,1,47,6,51,2,51,6,55,2,55,13,59,2,6,59,63,1,63,5,67,1,6,67,71,2,71,9,75,1,6,75,79,2,13,79,83,1,9,83,87,1,87,13,91,2,91,10,95,1,6,95,99,1,99,13,103,1,13,103,107,2,107,10,111,1,9,111,115,1,115,10,119,1,5,119,123,1,6,123,127,1,10,127,131,1,2,131,135,1,135,10,0,99,2,14,0,0};
                input[1] = input1;
                input[2] = input2;
                program(input);
                if (input[0] == 19690720) {
                    log.warn("determine what pair of inputs produces the output 19690720 = {}", 100 * input1 + input2);
                    break;
                }
            }
        }

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
