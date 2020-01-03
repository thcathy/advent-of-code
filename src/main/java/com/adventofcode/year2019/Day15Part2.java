package com.adventofcode.year2019;


import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.assertEquals;

public class Day15Part2 {
    Logger log = LoggerFactory.getLogger(Day15Part2.class);
    final static String inputFile = "2019/day15_1.txt";
    final static int INVALID_ADDRESS = -1;
    final static long STOP_PROGRAM_SINGAL = 99;

    public static void main(String... args) throws IOException {
        Day15Part2 solution = new Day15Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var map = generateMap(convertToLongArray(lines.get(0)));
        var oxygenSystem = map.entrySet().stream().filter(e -> e.getValue() == 2).findFirst().get().getKey();
        var spaces = map.entrySet().stream().filter(e -> e.getValue() == 1).map(Map.Entry::getKey).collect(Collectors.toSet());
        var result = stepToFillOxygen(oxygenSystem, spaces);
        log.warn("How many minutes will it take to fill with oxygen? {}", result);
    }

    long[] convertToLongArray(String input) {
        return Arrays.stream(input.split(","))
                .mapToLong(Long::valueOf)
                .toArray();
    }

    void executeProgram(Software software) {
        while (software.output.size() < 1) {
            getOperationType(String.valueOf(software.getOpCode())).apply.accept(software);
        }
    }

    void drawMap(Map<Position, Long> map) {
        int minX = map.keySet().stream().mapToInt(p -> p.x).min().getAsInt();
        int maxX = map.keySet().stream().mapToInt(p -> p.x).max().getAsInt();
        int minY = map.keySet().stream().mapToInt(p -> p.y).min().getAsInt();
        int maxY = map.keySet().stream().mapToInt(p -> p.y).max().getAsInt();

        for (int y = maxY; y >= minY; y--) {
            for (int x = minX; x <= maxX; x++) {
                boolean isSpace = map.getOrDefault(new Position(x, y), 0l) == 1;
                System.out.print(isSpace ? '.' : '#');
            }
            System.out.println();
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
        Map<Long, Long> program;
        List<Long> input;
        List<Long> output;
        long pointer;
        long relativeBase = 0;

        Software(Map<Long, Long> program, Collection<Long> inputs) {
            this.program = program;
            this.input = new ArrayList<>(inputs);
            this.output = new ArrayList<>();
            this.pointer = 0;
        }

        boolean isFinished() {
            return STOP_PROGRAM_SINGAL == program.get(pointer);
        }

        long getOpCode() { return program.get(pointer); }

        void increasePointer(int i) { pointer += i; }
    }

    Map<Long, Long> toMap(long[] input) {
        var program = new HashMap<Long, Long>();
        for (int i = 0; i < input.length; i++) {
            program.put(Long.valueOf(i), input[i]);
        }
        return program;
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

    int stepToFillOxygen(Position start, Set<Position> spaces) {
        int step = 0;
        var positions = List.of(start);
        while (spaces.size() > 0) {
            step++;
            positions = positions.stream().flatMap(this::nextPosition)
                    .filter(p -> spaces.contains(p))
                    .map(p -> { spaces.remove(p); return p; })
                    .collect(Collectors.toList());
        }
        return step;
    }

    Map<Position, Long> generateMap(long[] input) {
        var startPosition= new Position(0, 0);
        var droids = List.of(new Droid(new Software(toMap(input), List.of()), startPosition));
        Map<Position, Long> map = new HashMap<>();
        map.put(startPosition, 1l);
        while (droids.size() > 0) {
            droids = droids.stream().flatMap(d -> nextPossibleStep(d, map)).collect(Collectors.toList());
        }
        return map;
    }

    Stream<Droid> nextPossibleStep(Droid droid, Map<Position, Long> visited) {
        return allDirection()
                .mapToObj(l -> moveDroid(droid, l, visited))
                .filter(d -> !d.stopped);
    }

    Stream<Position> nextPosition(Position p) {
        return Stream.of(
                new Position(p.x+1, p.y),
                new Position(p.x-1, p.y),
                new Position(p.x, p.y+1),
                new Position(p.x, p.y-1)
        );
    }

    LongStream allDirection() { return LongStream.rangeClosed(1, 4); }

    Droid moveDroid(Droid droid, long direction, Map<Position, Long> visited) {
        var newPosition = nextPosition(droid.position, direction);
        if (visited.containsKey(newPosition)) {
            droid.stopped = true;
            return droid;
        }

        droid = droid.copy();
        droid.software.input.add(direction);
        droid.position = newPosition;
        executeProgram(droid.software);
        var output = droid.software.output.remove(0);
        visited.put(newPosition, output);
        if (output == 0) droid.stopped = true;
        return droid;
    }

    Position nextPosition(Position position, long direction) {
        switch ((int) direction) {
            case 1:
                return new Position(position.x, position.y+1);
            case 2:
                return new Position(position.x, position.y-1);
            case 3:
                return new Position(position.x-1, position.y);
            case 4:
                return new Position(position.x+1, position.y);
            default:
                return position;
        }
    }

    class Droid {
        Software software;
        Position position;
        boolean stopped;

        public Droid(Software software, Position position) {
            this.software = software;
            this.position = position;
        }

        Droid copy() {
            var copiedProgram = software.program.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            var copiedVisited = new HashSet<Position>();
            return new Droid(new Software(copiedProgram, List.of()), position);
        }
    }

    class Position {
        int x = 0, y = 0;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() { return MessageFormat.format("({0},{1})", x, y); }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return new EqualsBuilder()
                    .append(x, position.x)
                    .append(y, position.y)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(x)
                    .append(y)
                    .toHashCode();
        }
    }

    @Test
    public void stepToFillOxygen_testcases() {
        var start = new Position(-1, -1);
        var spaces = new HashSet<Position>();
        spaces.add(new Position(0, 0));
        spaces.add(new Position(1, 0));
        spaces.add(new Position(0, -1));
        spaces.add(new Position(-2, -1));
        spaces.add(new Position(-2, 0));
        spaces.add(new Position(-2, 1));
        spaces.add(new Position(-1, 1));

        assertEquals(4, stepToFillOxygen(start, spaces));
    }
}
