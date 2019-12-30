package com.adventofcode.year2019;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.assertEquals;

public class Day7Part2 {
    Logger log = LoggerFactory.getLogger(Day7Part2.class);
    final static String inputFile = "2019/day7_1.txt";
    final static int PROGRAM_OUTPUT = -1;
    final static int DO_NOTHING = -99999;
    final static int STOP_PROGRAM_SINGAL = 99;

    public static void main(String... args) throws IOException {
        Day7Part2 solution = new Day7Part2();
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
        while (!software.isFinished()) {
            var opCode = String.valueOf(software.program[software.pointer]);
            var operationType = getOperationType(opCode);
            var operation = operationType.getOperation.apply(new GetOperationInput(opCode, software, software.pointer));

            if (OperationType.Input == operationType && operation.inputValue == DO_NOTHING) {
                return;
            } else if (operation.outputPosition == PROGRAM_OUTPUT) {
                software.output.add(operation.inputValue);
            } else if (operation.outputPosition != DO_NOTHING) {
                software.program[operation.outputPosition] = operation.inputValue;
            }
            software.pointer = operation.nextPointer;
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
            if (input.software.input.isEmpty())
                return operation;
            operation.inputValue = input.software.input.remove(0);
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
        int inputValue = DO_NOTHING, outputPosition = DO_NOTHING, nextPointer;
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

        GetOperationInput(String opCode, Software software, int pointer) {
            this.opCode = opCode;
            this.software = software;
            this.pointer = pointer;
        }
    }

    int calculateThrusterSignal(final int[] input, List<Integer> phaseSettings) {
        int thruster = -1;
        var amplifiers = createAmplifiers(input, phaseSettings);

        amplifiers.get(0).input.add(0);
        while (true) {
            for (int i = 0; i < 5; i++) {
                executeProgram(amplifiers.get(i));
                if (amplifiers.get(4).isFinished())
                    return amplifiers.get(4).output.remove(0);
                copyOutputToNextAmplifier(amplifiers, i);
            }
        }
    }

    void copyOutputToNextAmplifier(List<Software> amplifiers, int pointer) {
        int next = pointer+1;
        if (next >= amplifiers.size()) next = 0;

        amplifiers.get(next).input.addAll(amplifiers.get(pointer).output);
        amplifiers.get(pointer).output.clear();
    }

    List<Software> createAmplifiers(int[] input, List<Integer> phaseSettings) {
        var amplifiers = phaseSettings.stream()
                .map(s -> new Software(Arrays.copyOf(input, input.length), List.of(s)))
                .collect(Collectors.toList());
        return amplifiers;
    }

    int maxThrusterSignal(final int[] input) {
        return allPhaseSettingPattern(List.of(5, 6, 7, 8, 9), Collections.EMPTY_LIST)
                .mapToInt(phaseSettings -> calculateThrusterSignal(input, (List<Integer>) phaseSettings))
                .max().getAsInt();
    }

    class Software {
        int[] program;
        List<Integer> input;
        List<Integer> output;
        int pointer;

        Software(int[] program, Collection<Integer> inputs) {
            this.program = program;
            this.input = new ArrayList<>(inputs);
            this.output = new ArrayList<>();
            this.pointer = 0;
        }

        boolean isFinished() {
            return program[pointer] == STOP_PROGRAM_SINGAL;
        }
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
    public void maxThrusterSignal_testcases() {
        assertEquals(139629729, maxThrusterSignal(new int[] {3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5}));
        assertEquals(18216, maxThrusterSignal(new int[] {3,52,1001,52,-5,52,3,53,1,52,56,54,1007,54,5,55,1005,55,26,1001,54,-5,54,1105,1,12,1,53,54,53,1008,54,0,55,1001,55,1,55,2,53,55,53,4,53,1001,56,-1,56,1005,56,6,99,0,0,0,0,10}));
    }

}
