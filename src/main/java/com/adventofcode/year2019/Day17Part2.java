package com.adventofcode.year2019;


import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static com.adventofcode.year2019.Day17Part2.Direction.D;
import static com.adventofcode.year2019.Day17Part2.Direction.L;
import static com.adventofcode.year2019.Day17Part2.Direction.R;
import static com.adventofcode.year2019.Day17Part2.Direction.U;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

public class Day17Part2 {
    Logger log = LoggerFactory.getLogger(Day17Part1.class);
    final static String inputFile = "2019/day17_1.txt";
    final static int INVALID_ADDRESS = -1;
    final static long STOP_PROGRAM_SINGAL = 99;

    public static void main(String... args) throws IOException {
        Day17Part2 solution = new Day17Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var software = new Software(convertToLongArray(lines.get(0)), Collections.emptyList());
        executeProgram(software);
        var map = getMap(software.output);
        var path = getPath(getMap(software.output));
        var functions = extractRoutineAndFunction(path);
        var result = runProgram(convertToLongArray(lines.get(0)), functions);
        log.warn("how much dust does the vacuum robot report it has collected? {}", result);
    }

    private long runProgram(long[] program, List<List<String>> functions) {
        program[0] = 2;

        var input = toSoftwareInput(functions.get(0));
        input.addAll(toSoftwareInput(functions.get(1)));
        input.addAll(toSoftwareInput(functions.get(2)));
        input.addAll(toSoftwareInput(functions.get(3)));
        input.add((long)'n');
        input.add(10l);
        var software = new Software(program, input);
        while (!software.isFinished()) {
            getOperationType(String.valueOf(software.getOpCode())).apply.accept(software);
        }
        return software.output.get(software.output.size()-1);
    }

    private List<Long> toSoftwareInput(List<String> strings) {
        var input = new ArrayList<Long>();
        for (String s : strings) {
            s.chars().forEach(i -> input.add((long) i));
            input.add(44l);
        }
        input.remove(input.size()-1);
        input.add(10l);
        return input;
    }

    private void printImage(List<Long> image) {
        for (long value : image) {
            if (value == 10)
                System.out.println();
            else
                System.out.print((char)value);
        }
    }

    List<String> getPath(Map<Position, Character> map) {
        var robot = new Robot();
        robot.position = map.entrySet().stream().filter(e -> e.getValue() == '^').findAny().get().getKey();
        robot.direction = U;
        Set<Position> scaffolds = map.entrySet().stream().filter(e -> e.getValue() == '#').map(Map.Entry::getKey).collect(Collectors.toSet());
        List<String> path = new ArrayList<>();

        int step = 0;
        while (true) {
            if (scaffolds.contains(robot.frontPosition())) {
                Position nextPosition = robot.frontPosition();
                step++;
                robot.position = nextPosition;
            } else {
                path.add(String.valueOf(step));
                step=0;
                if (scaffolds.contains(robot.leftPosition())) {
                    robot.turn(true);
                    path.add("L");
                } else if (scaffolds.contains(robot.rightPosition())) {
                    robot.turn(false);
                    path.add("R");
                } else {
                    path.remove(0);
                    return path;
                }
            }
        }
    }

    int alignmentParameters(Position position) { return position.x * position.y; }

    Map<Position, Character> getMap(List<Long> inputs) {
        var map = new HashMap<Position, Character>();
        int x = 0, y = 0;
        for (long value : inputs) {
            if (value == 10) {
                y++;
                x=0;
            } else {
                map.put(new Position(x, y), (char) value);
                x++;
            }
        }
        return map;
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

    List<List<String>> extractRoutineAndFunction(List<String> s) {
        String str = String.join("", s);
        for (int x = 1; x <= 10; x++) {
            var functionAList = s.subList(0, x*2);
            for (int y = 1; y <= 10; y++) {
                var functionBStartPos = functionAList.size();
                while (isMatch(s, functionAList, functionBStartPos))
                    functionBStartPos += functionAList.size();
                var functionBList = s.subList(functionBStartPos, functionBStartPos+y*2);

                for (int z = 1; z <= 10; z++) {
                    var functionCStartPos = functionBStartPos+y*2;
                    while (true) {
                        if (isMatch(s, functionBList, functionCStartPos))
                            functionCStartPos += functionBList.size();
                        else if (isMatch(s, functionAList, functionCStartPos))
                            functionCStartPos += functionAList.size();
                        else
                            break;
                    }

                    var functionCList = s.subList(functionCStartPos, functionCStartPos+z*2);

                    String functionA = String.join("", functionAList);
                    String functionB = String.join("", functionBList);
                    String functionC = String.join("", functionCList);
                    List<String> mainRoutine = extractMainRoutine(str, Map.of("A", functionA, "B", functionB, "C", functionC));
                    if (mainRoutine != null && mainRoutine.size() < 20)
                        return List.of(mainRoutine, functionAList, functionBList, functionCList);
                }
            }
        }
        throw new RuntimeException();
    }

    boolean isMatch(List<String> s, List<String> functionAList, int functionBStartPos) {
        for (int i=0; i<functionAList.size(); i++) {
            if (!s.get(functionBStartPos+i).equals(functionAList.get(i)))
                return false;
        }
        return true;
    }

    List<String> extractMainRoutine(String s, Map<String, String> functions) {
        int pos = 0;
        var mainRoutine = new ArrayList<String>();
        while (pos < s.length()) {
            int finalPos = pos;
            var function = functions.entrySet().stream().filter(e -> s.startsWith(e.getValue(), finalPos)).findFirst();
            if (!function.isPresent())
                return null;
            mainRoutine.add(function.get().getKey());
            pos += function.get().getValue().length();
        }
        return mainRoutine;
    }

    @Test
    public void _testcases() {
        var result = extractRoutineAndFunction(List.of("R","8","R","8","R","4","R","4","R","8","L","6","L","2","R","4","R","4","R","8","R","8","R","8","L","6","L","2"));

        assertEquals(6, result.get(0).size());
        assertEquals(4, result.get(1).size());
        assertEquals(6, result.get(2).size());
        assertEquals(4, result.get(3).size());
    }

    @Test
    public void extractMainRoutine_testcases() {
        var result = extractMainRoutine("R8R8R4R4R8L6L2R4R4R8R8R8L6L2",
                Map.of(
                        "A","R8R8",
                        "B","R4R4R8",
                        "C","L6L2")
        );
        assertEquals("ABCBAC", result);

        var result2 = extractMainRoutine("R8R8R4R4R8L6L2R4R4R8R8R8L6L2",
                Map.of(
                        "A","R8",
                        "B","R4",
                        "C","L6")
        );
        assertNull(result2);
    }

    enum Direction { U, D, L, R }

    class Robot {
        Position position = new Position(0, 0);
        Direction direction = Direction.U;

        void turn(boolean isLeft) {
            if (isLeft) {
                switch (direction) {
                    case U: direction = L;
                        break;
                    case D: direction = R;
                        break;
                    case L: direction = D;
                        break;
                    case R: direction = U;
                        break;
                }
            } else {
                switch (direction) {
                    case U: direction = R;
                        break;
                    case D: direction = L;
                        break;
                    case L: direction = U;
                        break;
                    case R: direction = D;
                        break;
                }
            }
        }

        Position frontPosition() {
            var x = position.x;
            var y = position.y;

            if (direction == U) y--;
            else if (direction == D) y++;
            else if (direction == R) x++;
            else if (direction == L) x--;

            return new Position(x, y);
        }

        Position leftPosition() {
            var x = position.x;
            var y = position.y;

            if (direction == U) x--;
            else if (direction == D) x++;
            else if (direction == R) y--;
            else if (direction == L) y++;

            return new Position(x, y);
        }

        Position rightPosition() {
            var x = position.x;
            var y = position.y;

            if (direction == U) x++;
            else if (direction == D) x--;
            else if (direction == R) y++;
            else if (direction == L) y--;

            return new Position(x, y);
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

}
