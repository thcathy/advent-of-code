package com.adventofcode.year2019;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day13Part2 {
    Logger log = LoggerFactory.getLogger(Day13Part2.class);
    final static String inputFile = "2019/day13_1.txt";
    final static int INVALID_ADDRESS = -1;
    final static long STOP_PROGRAM_SINGAL = 99;

    public static void main(String... args) throws IOException {
        Day13Part2 solution = new Day13Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var software = new Software(convertToLongArray(lines.get(0)), List.of(2l));
        var result = playGame(software);
        log.warn("What is your score after the last block is broken? {}", result);
    }

    long[] convertToLongArray(String input) {
        return Arrays.stream(input.split(","))
                .mapToLong(Long::valueOf)
                .toArray();
    }

    long playGame(Software software) {
        software.program.put(0l, 2l);
        long score = 0;
        Tile paddle = null;
        while (!software.isFinished()) {
            getOperationType(String.valueOf(software.getOpCode())).apply.accept(software);
            if (hasTileOutput(software)) {
                Tile tile = extractTile(software);
                if (tile.isPaddle()) paddle = tile;
                if (tile.isBall() && paddle != null) {
                    software.input.add(tile.x == paddle.x ? 0 : tile.x > paddle.x ? 1l : -1l);
                }
                if (tile.isScore()) score = tile.id;
            }
        }
        return score;
    }

    private boolean hasTileOutput(Software software) { return software.output.size() == 3; }

    Tile extractTile(Software software) {
        return new Tile(software.output.remove(0), software.output.remove(0), software.output.remove(0));
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

    class Tile {
        long x, y, id;

        public Tile(long x, long y, long id) {
            this.x = x;
            this.y = y;
            this.id = id;
        }

        public boolean isBall() { return id == 4; }

        public boolean isPaddle() { return id == 3; }

        public boolean isScore() { return x == -1 && y == 0; }
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

}
