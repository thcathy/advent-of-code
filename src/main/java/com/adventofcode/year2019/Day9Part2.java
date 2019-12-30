package com.adventofcode.year2019;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.assertEquals;

public class Day9Part2 {
    Logger log = LoggerFactory.getLogger(Day9Part2.class);
    final static String inputFile = "2019/day9_1.txt";
    final static int INVALID_ADDRESS = -1;
    final static long STOP_PROGRAM_SINGAL = 99;

    public static void main(String... args) throws IOException {
        Day9Part2 solution = new Day9Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var software = new Software(convertToLongArray(lines.get(0)), List.of(2l));
        executeProgram(software);
        log.warn("What BOOST keycode does it produce? {}", software.output.get(0));
    }

    long[] convertToLongArray(String input) {
        return Arrays.stream(input.split(","))
                .mapToLong(Long::valueOf)
                .toArray();
    }

    void executeProgram(Software software) {
        while (!software.isFinished()) {
            getOperationType(String.valueOf(software.getOpCode())).apply.accept(software);
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
        Addition("1", (software) -> {
            var value = getParamValue(software, 1) + getParamValue(software, 2);
            var outputPosition = getOutputPosition(software, 3);
            software.program.put(outputPosition, value);
            software.increasePointer(4);
        }),
        Multiply("2", (software) -> {
            var value = getParamValue(software, 1) * getParamValue(software, 2);
            var outputPosition = getOutputPosition(software, 3);
            software.program.put(outputPosition, value);
            software.increasePointer(4);
        }),
        Input("3", (software) -> {
            var outputPosition = getOutputPosition(software, 1);
            software.program.put(outputPosition, software.input.remove(0));
            software.increasePointer(2);
        }),
        Output("4", (software) -> {
            var value = getParamValue(software, 1);
            software.output.add(value);
            software.increasePointer(2);
        }),
        JumpIfTrue("5", (software) -> {
            if (getParamValue(software, 1) != 0)
                software.pointer = getParamValue(software, 2);
            else
                software.increasePointer(3);

        }),
        JumpIfFalse("6", (software) -> {
            if (getParamValue(software, 1) == 0)
                software.pointer = getParamValue(software, 2);
            else
                software.increasePointer(3);
        }),
        LessThan("7", (software) -> {
            var value = (getParamValue(software, 1) < getParamValue(software, 2)) ? 1l : 0l;
            var outputPosition = getOutputPosition(software, 3);
            software.program.put(outputPosition, value);
            software.increasePointer(4);
        }),
        Equals("8", (software) -> {
            var value = (getParamValue(software, 1) == getParamValue(software, 2)) ? 1l : 0l;
            var outputPosition = getOutputPosition(software, 3);
            software.program.put(outputPosition, value);
            software.increasePointer(4);
        }),
        AdjustRelativeBase("9", (software) -> {
            software.relativeBase += getParamValue(software, 1);
            software.increasePointer(2);
        });

        String code;
        Consumer<Software> apply;

        OperationType(String code, Consumer<Software> apply) {
            this.code = code;
            this.apply = apply;
        }
    }

    class Software {
        Map<Long, Long> program = new HashMap<>();
        List<Long> input;
        List<Long> output;
        long pointer;
        long relativeBase = 0;

        Software(long[] program, Collection<Long> inputs) {
            setProgram(program);
            this.input = new ArrayList<>(inputs);
            this.output = new ArrayList<>();
            this.pointer = 0;
        }

        void setProgram(long[] input) {
            for (int i = 0; i < input.length; i++) {
                program.put(Long.valueOf(i), input[i]);
            }
        }

        boolean isFinished() {
            return STOP_PROGRAM_SINGAL == program.get(pointer);
        }

        long getOpCode() { return program.get(pointer); }

        void increasePointer(int i) { pointer += i; }
    }

    static String getMode(long input, int paramPosition) {
        var inputString = String.valueOf(input);
        var value = "0";
        if (inputString.length() >= paramPosition + 2) {
            value = inputString.substring(inputString.length() - 2 - paramPosition, inputString.length() - 1 - paramPosition);
        }
        return value;
    }

    static long getParamValue(Software software, int paramPosition) {
        long value = software.program.get(software.pointer + paramPosition);
        var mode = getMode(software.getOpCode(), paramPosition);
        if ("1".equals(mode))
            return value;

        long position = INVALID_ADDRESS;
        if ("0".equals(mode))
            position = software.program.containsKey(value) ? value : INVALID_ADDRESS;
        else if ("2".equals(mode))
            position = software.program.containsKey(software.relativeBase + value) ? software.relativeBase + value : INVALID_ADDRESS;

        return (position != INVALID_ADDRESS) ? software.program.get(position) : 0;
    }

    static long getOutputPosition(Software software, int paramPosition) {
        long value = software.program.get(software.pointer + paramPosition);
        var mode = getMode(software.getOpCode(), paramPosition);

        return value + ("2".equals(mode) ? software.relativeBase : 0);
    }

    @Test
    public void executeProgram_testcases() {
        var software1 = new Software(new long[] {3,0,4,0,99}, List.of(1l));
        executeProgram(software1);
        assertEquals(1, software1.output.size());
        assertEquals(1, (long) software1.output.get(0));

        var software2 = new Software(new long[] {1002,4,3,4,33}, List.of(1l));
        executeProgram(software2);
        assertEquals(0, software2.output.size());

        var software3 = new Software(new long[] {109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99}, Collections.EMPTY_LIST);
        executeProgram(software3);
        assertEquals(16, software3.output.size());

        var software4 = new Software(new long[] {1102,34915192,34915192,7,4,7,99,0}, Collections.EMPTY_LIST);
        executeProgram(software4);
        assertEquals(1, software4.output.size());
        assertEquals(1219070632396864l, (long) software4.output.get(0));

        var software5 = new Software(new long[] {104,1125899906842624l,99}, Collections.EMPTY_LIST);
        executeProgram(software5);
        assertEquals(1, software5.output.size());
        assertEquals(1125899906842624l, (long) software5.output.get(0));

        var software6 = new Software(new long[] {1101,100,-1,4,0}, List.of(1l));
        executeProgram(software6);
        assertEquals(0, software6.output.size());

        // equal to, position mode
        var software7 = new Software(new long[] {3,9,8,9,10,9,4,9,99,-1,8}, List.of(1l));
        executeProgram(software7);
        assertEquals(1, software7.output.size());
        assertEquals(0, (long) software7.output.get(0));
        var software8 = new Software(new long[] {3,9,8,9,10,9,4,9,99,-1,8}, List.of(8l));
        executeProgram(software8);
        assertEquals(1, software8.output.size());
        assertEquals(1, (long) software8.output.get(0));

        // less than, position mode
        var software9 = new Software(new long[] {3,9,7,9,10,9,4,9,99,-1,8}, List.of(7l));
        executeProgram(software9);
        assertEquals(1, software9.output.size());
        assertEquals(1, (long) software9.output.get(0));
        var software10 = new Software(new long[] {3,9,7,9,10,9,4,9,99,-1,8}, List.of(8l));
        executeProgram(software10);
        assertEquals(1, software10.output.size());
        assertEquals(0, (long) software10.output.get(0));

        // equal to, immediate mode
        var software11 = new Software(new long[] {3,3,1108,-1,8,3,4,3,99}, List.of(8l));
        executeProgram(software11);
        assertEquals(1, software11.output.size());
        assertEquals(1, (long) software11.output.get(0));
        var software12 = new Software(new long[] {3,3,1108,-1,8,3,4,3,99}, List.of(9l));
        executeProgram(software12);
        assertEquals(1, software12.output.size());
        assertEquals(0, (long) software12.output.get(0));

        // less than, immediate mode
        var software13 = new Software(new long[] {3,3,1107,-1,8,3,4,3,99}, List.of(1l));
        executeProgram(software13);
        assertEquals(1, software13.output.size());
        assertEquals(1, (long) software13.output.get(0));
        var software14 = new Software(new long[] {3,3,1107,-1,8,3,4,3,99}, List.of(8l));
        executeProgram(software14);
        assertEquals(1, software14.output.size());
        assertEquals(0, (long) software14.output.get(0));

        // jump tests
        var software15 = new Software(new long[] {3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9}, List.of(0l));
        executeProgram(software15);
        assertEquals(1, software15.output.size());
        assertEquals(0, (long) software15.output.get(0));
        var software16 = new Software(new long[] {3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9}, List.of(5l));
        executeProgram(software16);
        assertEquals(1, software16.output.size());
        assertEquals(1, (long) software16.output.get(0));

        var software17 = new Software(new long[] {3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,
                1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,
                999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99}, List.of(7l));
        executeProgram(software17);
        assertEquals(1, software17.output.size());
        assertEquals(999, (long) software17.output.get(0));
        var software18 = new Software(new long[] {3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,
                1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,
                999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99}, List.of(8l));
        executeProgram(software18);
        assertEquals(1, software18.output.size());
        assertEquals(1000, (long) software18.output.get(0));
        var software19 = new Software(new long[] {3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,
                1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,
                999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99}, List.of(9l));
        executeProgram(software19);
        assertEquals(1, software19.output.size());
        assertEquals(1001, (long) software19.output.get(0));

        var software20 = new Software(new long[] {109, -1, 4, 1, 99}, Collections.EMPTY_LIST);
        executeProgram(software20);
        assertEquals(-1, (long) software20.output.get(0));
        var software21 = new Software(new long[] {109, -1, 104, 1, 99}, Collections.EMPTY_LIST);
        executeProgram(software21);
        assertEquals(1, (long) software21.output.get(0));
        var software22 = new Software(new long[] {109, -1, 204, 1, 99}, Collections.EMPTY_LIST);
        executeProgram(software22);
        assertEquals(109, (long) software22.output.get(0));
        var software23 = new Software(new long[] {109, 1, 9, 2, 204, -6, 99}, Collections.EMPTY_LIST);
        executeProgram(software23);
        assertEquals(204, (long) software23.output.get(0));
        var software24 = new Software(new long[] {109, 1, 109, 9, 204, -6, 99}, Collections.EMPTY_LIST);
        executeProgram(software24);
        assertEquals(204, (long) software24.output.get(0));
        var software25 = new Software(new long[] {109, 1, 209, -1, 204, -106, 99}, Collections.EMPTY_LIST);
        executeProgram(software25);
        assertEquals(204, (long) software25.output.get(0));
        var software26 = new Software(new long[] {109, 1, 3, 3, 204, 2, 99}, List.of(7l));
        executeProgram(software26);
        assertEquals(7, (long) software26.output.get(0));
        var software27 = new Software(new long[] {109, 1, 203, 2, 204, 2, 99}, List.of(7l));
        executeProgram(software27);
        assertEquals(7, (long) software27.output.get(0));
    }

}
