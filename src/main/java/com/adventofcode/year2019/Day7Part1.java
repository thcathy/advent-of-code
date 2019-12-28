package com.adventofcode.year2019;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class Day7Part1 {
    Logger log = LoggerFactory.getLogger(Day7Part1.class);
    final static String inputFile = "2019/day7_1.txt";
    final static int PROGRAM_OUTPUT = -1;
    final static int DO_NOTHING = -99999;

    public static void main(String... args) throws IOException {
        Day7Part1 solution = new Day7Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = maxThrusterSignal(convertToIntArray(lines.get(0)));
        log.warn("What is the highest signal that can be sent to the thrusters? {}", result);
    }

    int[] convertToIntArray(String input) {
        return Arrays.stream(input.split(","))
                .mapToInt(Integer::valueOf)
                .toArray();
    }

    void executeProgram(Software software) {
        int pointer = 0;
        while (software.program[pointer] != 99) {
            var opCode = String.valueOf(software.program[pointer]);
            var operation = getOperationType(opCode).getOperation.apply(new GetOperationInput(opCode, software, pointer));
            
            if (operation.outputPosition == PROGRAM_OUTPUT) {
                addOutputToIO(software, operation);
            } else if (operation.outputPosition != DO_NOTHING) {
                software.program[operation.outputPosition] = operation.inputValue;
            }

            pointer = operation.nextPointer;
        }
    }

    private void addOutputToIO(Software software, Operation operation) {
        if (software.io.isEmpty()) {
            software.io.addFirst(operation.inputValue);
        } else {
            var setting = software.io.remove();
            software.io.addFirst(operation.inputValue);
            software.io.addFirst(setting);
        }
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
            operation.outputPosition = input.software.program[input.pointer + 3];
            operation.nextPointer = input.pointer + 4;
            return operation;
        }),
        Multiply("2", (input) -> {
            var operation = new Operation();
            operation.inputValue = getParamValue(input, 1) * getParamValue(input, 2);
            operation.outputPosition = input.software.program[input.pointer + 3];
            operation.nextPointer = input.pointer + 4;
            return operation;
        }),
        Input("3", (input) -> {
            var operation = new Operation();
            operation.inputValue = input.software.io.remove();
            operation.outputPosition = input.software.program[input.pointer + 1];
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
            operation.outputPosition = input.software.program[input.pointer + 3];
            operation.nextPointer = input.pointer + 4;
            return operation;
        }),
        Equals("8", (input) -> {
            var operation = new Operation();
            operation.inputValue = (getParamValue(input, 1) == getParamValue(input, 2)) ? 1 : 0;
            operation.outputPosition = input.software.program[input.pointer + 3];
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
        int value = input.software.program[input.pointer + paramPosition];
        if ("0".equals(getMode(input.opCode, paramPosition)))
            return input.software.program[value];
        else
            return value;
    }

    class GetOperationInput {
        String opCode;
        Software software;
        int pointer;

        public GetOperationInput(String opCode, Software software, int pointer) {
            this.opCode = opCode;
            this.software = software;
            this.pointer = pointer;
        }
    }

    int calculateThrusterSignal(final int[] input, List<Integer> phaseSettings) {
        phaseSettings.add(1, 0);
        var software = new Software(Arrays.copyOf(input, input.length), phaseSettings);
        for (int i = 0; i < 5; i++) {
            executeProgram(software);
            software.program = Arrays.copyOf(input, input.length);
        }
        return software.io.remove();
    }

    int maxThrusterSignal(final int[] input) {
        return allPhaseSettingPattern(List.of(0, 1, 2, 3, 4), Collections.EMPTY_LIST)
                .mapToInt(phaseSettings -> calculateThrusterSignal(input, (List<Integer>) phaseSettings))
                .max().getAsInt();
    }

    class Software {
        int[] program;
        Deque<Integer> io;

        public Software(int[] program, Collection<Integer> inputs) {
            this.program = program;
            this.io = new ArrayDeque<>(inputs);
        }
    }

    int getParameter(int[] input, String mode, int value) {
        if ("0".equals(mode))
            return input[value];
        else
            return value;
    }

    Stream<List<Integer>> allPhaseSettingPattern(final List<Integer> allInt, List<Integer> list) {
        if (list.size() == allInt.size()) return Stream.of(list);

        return allInt.stream()
                .flatMap(item -> {
                    if (list.contains(item)) return Stream.empty();
                    var newlist = new ArrayList(list);
                    newlist.add(item);
                    return Stream.of(newlist);
                })
                .flatMap(newlist -> allPhaseSettingPattern(allInt, newlist));
    }

    @Test
    public void allPhaseSettingPattern_testcases() {
        var result = new Day7Part1().allPhaseSettingPattern(List.of(0, 1, 2, 3, 4), Collections.emptyList())
                .collect(Collectors.toList());
        assertEquals(120, result.size());
    }

    @Test
    public void maxThrusterSignal_testcases() {
        assertEquals(43210, maxThrusterSignal(new int[] {3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0}));
        assertEquals(54321, maxThrusterSignal(new int[] {3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0}));
        assertEquals(65210, maxThrusterSignal(new int[] {3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0}));

    }

}
