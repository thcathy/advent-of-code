package com.adventofcode.year2019;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.assertEquals;

public class Day5Part2 {
    Logger log = LoggerFactory.getLogger(Day5Part2.class);
    final static String inputFile = "2019/day5_1.txt";
    final static int PROGRAM_OUTPUT = -1;
    final static int DO_NOTHING = -99999;

    public static void main(String... args) throws IOException {
        Day5Part2 solution = new Day5Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = executeProgram(5, convertToIntArray(lines.get(0)));
        log.warn("What is the diagnostic code for system ID 5? {}", result.get(0));
    }

    int[] convertToIntArray(String input) {
        return Arrays.stream(input.split(","))
                .mapToInt(Integer::valueOf)
                .toArray();
    }

    List<Integer> executeProgram(int programInput, int[] program) {
        var output = new ArrayList<Integer>();
        int pointer = 0;
        while (program[pointer] != 99) {
            var opCode = String.valueOf(program[pointer]);
            var operationType = getOperationType(opCode);
            var operation = getOperationType(opCode).getOperation.apply(new GetOperationInput(opCode, program, pointer, programInput));

            log.debug("{}, input {}, output {}, next pointer {}", operationType, operation.inputValue, operation.outputPosition, operation.nextPointer);

            if (operation.outputPosition == PROGRAM_OUTPUT) {
                output.add(operation.inputValue);
                if (operation.inputValue != 0) break;
            } else if (operation.outputPosition != DO_NOTHING) {
                program[operation.outputPosition] = operation.inputValue;
            }

            pointer = operation.nextPointer;

            log.debug("{} - {}", pointer, program);
        }
        return output;
    }

    OperationType getOperationType(String opCode) {
        String code = opCode.length() == 1 ? opCode : opCode.substring(opCode.length() - 1);
        for (OperationType operation : OperationType.values()) {
            if (code.equals(operation.code)) return operation;
        }
        throw new RuntimeException("cannot find operation type from opcode " + opCode);
    }

    enum OperationType {
        Addition("1", (input) -> {
            var operation = new Operation();
            operation.inputValue = getParamValue(input, 1) + getParamValue(input, 2);
            operation.outputPosition = input.program[input.pointer + 3];
            operation.nextPointer = input.pointer + 4;
            return operation;
        }),
        Multiply("2", (input) -> {
            var operation = new Operation();
            operation.inputValue = getParamValue(input, 1) * getParamValue(input, 2);
            operation.outputPosition = input.program[input.pointer + 3];
            operation.nextPointer = input.pointer + 4;
            return operation;
        }),
        Input("3", (input) -> {
            var operation = new Operation();
            operation.inputValue = input.programInput;
            operation.outputPosition = input.program[input.pointer + 1];
            operation.nextPointer = input.pointer + 2;
            return operation;
        }),
        Output("4", (input) -> {
            var operation = new Operation();
            operation.inputValue = getParamValue(input, 1);
            operation.outputPosition = PROGRAM_OUTPUT;
            operation.nextPointer = input.pointer + 2;
            return operation;
        }),
        JumpIfTrue("5", (input) -> {
            var operation = new Operation();
            if (getParamValue(input, 1) != 0)
                operation.nextPointer = getParamValue(input, 2);
            else
                operation.nextPointer = input.pointer + 3;
            return operation;
        }),
        JumpIfFalse("6", (input) -> {
            var operation = new Operation();
            if (getParamValue(input, 1) == 0)
                operation.nextPointer = getParamValue(input, 2);
            else
                operation.nextPointer = input.pointer + 3;
            return operation;
        }),
        LessThan("7", (input) -> {
            var operation = new Operation();
            operation.inputValue = (getParamValue(input, 1) < getParamValue(input, 2)) ? 1 : 0;
            operation.outputPosition = input.program[input.pointer + 3];
            operation.nextPointer = input.pointer + 4;
            return operation;
        }),
        Equals("8", (input) -> {
            var operation = new Operation();
            operation.inputValue = (getParamValue(input, 1) == getParamValue(input, 2)) ? 1 : 0;
            operation.outputPosition = input.program[input.pointer + 3];
            operation.nextPointer = input.pointer + 4;
            return operation;
        });

        String code;
        Function<GetOperationInput, Operation> getOperation;

        OperationType(String code, Function<GetOperationInput, Operation> getOperation) {
            this.code = code;
            this.getOperation = getOperation;
        }
    }

    static class Operation {
        int inputValue, outputPosition = DO_NOTHING, nextPointer;
    }

    static String getMode(String input, int paramPosition) {
        var value = "0";
        if (input.length() >= paramPosition + 2) {
            value = input.substring(input.length() - 2 - paramPosition, input.length() - 1 - paramPosition);
        }
        return value;
    }

    static int getParamValue(GetOperationInput input, int paramPosition) {
        int value = input.program[input.pointer + paramPosition];
        if ("0".equals(getMode(input.opCode, paramPosition)))
            return input.program[value];
        else
            return value;
    }

    class GetOperationInput {
        String opCode;
        int[] program;
        int pointer, programInput;

        public GetOperationInput(String opCode, int[] program, int pointer, int programInput) {
            this.opCode = opCode;
            this.program = program;
            this.pointer = pointer;
            this.programInput = programInput;
        }
    }

    @Test
    public void executeProgram_testcases() {
        var output1 = executeProgram(1, new int[] {3,0,4,0,99});
        assertEquals(1, output1.size());
        assertEquals(1, (int) output1.get(0));

        var output2 = executeProgram(1, new int[] {1002,4,3,4,33});
        assertEquals(0, output2.size());

        var output3 = executeProgram(1, new int[] {1101,100,-1,4,0});
        assertEquals(0, output3.size());

        // equal to, position mode
        var output4 = executeProgram(1, new int[] {3,9,8,9,10,9,4,9,99,-1,8});
        assertEquals(1, output4.size());
        assertEquals(0, (int) output4.get(0));
        var output5 = executeProgram(8, new int[] {3,9,8,9,10,9,4,9,99,-1,8});
        assertEquals(1, output5.size());
        assertEquals(1, (int) output5.get(0));

        // less than, position mode
        var output6 = executeProgram(7, new int[] {3,9,7,9,10,9,4,9,99,-1,8});
        assertEquals(1, output6.size());
        assertEquals(1, (int) output6.get(0));
        var output7 = executeProgram(8, new int[] {3,9,7,9,10,9,4,9,99,-1,8});
        assertEquals(1, output7.size());
        assertEquals(0, (int) output7.get(0));

        // equal to, immediate mode
        var output8 = executeProgram(8, new int[] {3,3,1108,-1,8,3,4,3,99});
        assertEquals(1, output8.size());
        assertEquals(1, (int) output8.get(0));
        var output9 = executeProgram(9, new int[] {3,3,1108,-1,8,3,4,3,99});
        assertEquals(1, output9.size());
        assertEquals(0, (int) output9.get(0));

        // less than, immediate mode
        var output10 = executeProgram(1, new int[] {3,3,1107,-1,8,3,4,3,99});
        assertEquals(1, output10.size());
        assertEquals(1, (int) output10.get(0));
        var output11 = executeProgram(8, new int[] {3,3,1107,-1,8,3,4,3,99});
        assertEquals(1, output11.size());
        assertEquals(0, (int) output11.get(0));

        // jump tests
        var output12 = executeProgram(0, new int[] {3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9});
        assertEquals(1, output12.size());
        assertEquals(0, (int) output12.get(0));
        var output13 = executeProgram(5, new int[] {3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9});
        assertEquals(1, output13.size());
        assertEquals(1, (int) output13.get(0));
        var output14 = executeProgram(0, new int[] {3,3,1105,-1,9,1101,0,0,12,4,12,99,1});
        assertEquals(1, output14.size());
        assertEquals(0, (int) output14.get(0));
        var output15 = executeProgram(4, new int[] {3,3,1105,-1,9,1101,0,0,12,4,12,99,1});
        assertEquals(1, output15.size());
        assertEquals(1, (int) output15.get(0));

        var output16 = executeProgram(7, new int[] {3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,
                1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,
                999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99});
        assertEquals(1, output16.size());
        assertEquals(999, (int) output16.get(0));
        var output17 = executeProgram(8, new int[] {3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,
                1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,
                999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99});
        assertEquals(1, output17.size());
        assertEquals(1000, (int) output17.get(0));
        var output18 = executeProgram(9, new int[] {3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,
                1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,
                999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99});
        assertEquals(1, output18.size());
        assertEquals(1001, (int) output18.get(0));
    }

}
