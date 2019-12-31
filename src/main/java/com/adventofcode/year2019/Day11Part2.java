package com.adventofcode.year2019;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Consumer;

import static com.adventofcode.year2019.Day11Part2.Direction.*;
import static org.junit.Assert.assertEquals;

public class Day11Part2 {
    Logger log = LoggerFactory.getLogger(Day11Part2.class);
    final static String inputFile = "2019/day11_1.txt";
    final static int INVALID_ADDRESS = -1;
    final static long STOP_PROGRAM_SINGAL = 99;

    public static void main(String... args) throws IOException {
        Day11Part2 solution = new Day11Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var panels = paintPanels(convertToLongArray(lines.get(0)));
        log.warn("hat registration identifier does it paint on your hull?");
        drawPanels(panels.panel);
    }

    long[] convertToLongArray(String input) {
        return Arrays.stream(input.split(","))
                .mapToLong(Long::valueOf)
                .toArray();
    }

    void drawPanels(Map<Position, Long> panels) {
        int minX = panels.keySet().stream().mapToInt(p -> p.x).min().getAsInt();
        int maxX = panels.keySet().stream().mapToInt(p -> p.x).max().getAsInt();
        int minY = panels.keySet().stream().mapToInt(p -> p.y).min().getAsInt();
        int maxY = panels.keySet().stream().mapToInt(p -> p.y).max().getAsInt();

        for (int y = maxY; y >= minY; y--) {
            for (int x = minX; x <= maxX; x++) {
                boolean isBlack = panels.getOrDefault(new Position(x, y), 1l) == 0;
                System.out.print(isBlack ? ' ' : '|');
            }
            System.out.println();
        }
    }

    Panels paintPanels(long[] program) {
        var software = new Software(program, List.of(1l));
        var robot = new Robot();
        var panels = new Panels();

        while (!software.isFinished()) {
            getOperationType(String.valueOf(software.getOpCode())).apply.accept(software);
            if (software.output.size() == 2) {
                panels.paint(robot.position, software.output.remove(0));
                robot.turnAndMove(software.output.remove(0));
                software.input.add(panels.colorAt(robot.position));
            }
        }

        return panels;
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

    enum Direction { U, D, L, R }

    class Robot {
        Position position = new Position(0, 0);
        Direction direction = Direction.U;

        void turnAndMove(long isLeft) { turn(isLeft); move(); }

        void turn(long isLeft) {
            if (isLeft == 0) {
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

        void move() {
            var x = position.x;
            var y = position.y;

            if (direction == U) y++;
            else if (direction == D) y--;
            else if (direction == R) x++;
            else if (direction == L) x--;

            position = new Position(x, y);
        }
    }

    class Panels {
        Map<Position, Long> panel = new HashMap<>();

        long colorAt(Position position) { return panel.getOrDefault(position, 0l); }

        void paint(Position position, long color) { panel.put(position, color); }
    }

    class Position {
        int x = 0, y = 0;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public String angleTo(Position target) {
            float angle = (float) Math.toDegrees(Math.atan2(target.y - y, target.x - x));
            if(angle < 0) { angle += 360; }
            return String.valueOf(angle);
        }

        @Override
        public String toString() {
            return MessageFormat.format("({0},{1})", x, y);
        }

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
    public void robot_panels_testcaes() {
        var robot = new Robot();
        var panels = new Panels();

        assertEquals(0, panels.colorAt(new Position(0, 0)));
        panels.paint(robot.position, 1);
        robot.turnAndMove(0);

        assertEquals(1, panels.colorAt(new Position(0, 0)));
        assertEquals(new Position(-1, 0), robot.position);
        assertEquals(L, robot.direction);

        panels.paint(robot.position, 0);
        robot.turnAndMove(0);
        assertEquals(D, robot.direction);

        panels.paint(robot.position, 1);
        robot.turnAndMove(0);
        assertEquals(1, panels.colorAt(new Position(-1, -1)));
        panels.paint(robot.position, 1);
        robot.turnAndMove(0);
        assertEquals(1, panels.colorAt(new Position(0, -1)));

        panels.paint(robot.position, 0);
        robot.turnAndMove(1);
        panels.paint(robot.position, 1);
        robot.turnAndMove(0);
        panels.paint(robot.position, 1);
        robot.turnAndMove(0);
        assertEquals(L, robot.direction);
        assertEquals(1, panels.colorAt(new Position(1, 1)));

        assertEquals(6, panels.panel.keySet().stream().distinct().count());
    }
}
