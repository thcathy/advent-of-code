package com.adventofcode.year2019;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.assertEquals;

public class Day5Part1 {
    Logger log = LoggerFactory.getLogger(Day5Part1.class);
    final static String inputFile = "2019/day5_1.txt";
    final static int PROGRAM_OUTPUT = -1;

    public static void main(String... args) throws IOException {
        Day5Part1 solution = new Day5Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = executeProgram(1, convertToIntArray(lines.get(0)));
        log.warn("determine what pair of inputs produces the output 19690720 = {}", result.get(result.size() - 1));
    }

    int[] convertToIntArray(String input) {
        return Arrays.stream(input.split(","))
                .mapToInt(Integer::valueOf)
                .toArray();
    }

    List<Integer> executeProgram(int input, int[] program) {
        var output = new ArrayList<Integer>();
        int pointer = 0;
        while (program[pointer] != 99) {
            var opCode = String.valueOf(program[pointer]);
            var operation = getOperation(opCode);
            fillParameters(operation, opCode, program, pointer, input);

            if (operation.outputPosition == PROGRAM_OUTPUT) {
                output.add(operation.inputValue);
                if (operation.inputValue != 0) break;
            } else {
                program[operation.outputPosition] = operation.inputValue;
            }

            pointer += operation.numOfParameters + 1;
        }
        return output;
    }

    Operation getOperation(String opCode) {
        String code = opCode.length() == 1 ? opCode : opCode.substring(opCode.length() - 1);
        for (Operation operation : Operation.values()) {
            if (code.equals(operation.code)) return operation;
        }
        throw new RuntimeException("cannot find operation from opcode " + opCode);
    }

    void fillParameters(Operation operation, String opCode, int[] program, int pointer, int inputValue) {
        if (operation == Operation.Addition) {
            operation.inputValue = getParameter(program, getMode(opCode, 1), program[pointer + 1])
                    + getParameter(program, getMode(opCode, 2), program[pointer + 2]);
            operation.outputPosition = program[pointer + 3];
        } else if (operation == Operation.Multiply) {
            operation.inputValue = getParameter(program, getMode(opCode, 1), program[pointer + 1])
                    * getParameter(program, getMode(opCode, 2), program[pointer + 2]);
            operation.outputPosition = program[pointer + 3];
        } if (operation == Operation.Input) {
            operation.inputValue = inputValue;
            operation.outputPosition = program[pointer + 1];
        } else if (operation == Operation.Output) {
            operation.inputValue = getParameter(program, getMode(opCode, 1), program[pointer + 1]);
            operation.outputPosition = PROGRAM_OUTPUT;
        }
    }

    enum Operation {
        Addition("1", 3), Multiply("2", 3), Input("3", 1), Output("4", 1);

        String code;
        int numOfParameters;
        int inputValue, outputPosition;

        Operation(String code, int numOfParameters) {
            this.code = code;
            this.numOfParameters = numOfParameters;
        }
    }

    String getMode(String input, int paramPosition) {
        var value = "0";
        if (input.length() >= paramPosition + 2) {
            value = input.substring(input.length() - 2 - paramPosition, input.length() - 1 - paramPosition);
        }
        return value;
    }

    int getParameter(int[] input, String mode, int value) {
        if ("0".equals(mode))
            return input[value];
        else
            return value;
    }

    @Test
    public void executeProgram_testcases() {
        var output1 = executeProgram(1, new int[] {3,0,4,0,99});
        assertEquals(1, output1.size());
        assertEquals(1, (int)output1.get(0));

        var output2 = executeProgram(1, new int[] {1002,4,3,4,33});
        assertEquals(0, output2.size());

        var output3 = executeProgram(1, new int[] {1101,100,-1,4,0});
        assertEquals(0, output2.size());
    }

}
