package com.adventofcode.year2019;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day23Part1 {
    Logger log = LoggerFactory.getLogger(Day23Part1.class);
    final static String inputFile = "2019/day23_1.txt";
    final static int INVALID_ADDRESS = -1;
    final static long STOP_PROGRAM_SINGAL = 99;
    final static long EMPTY_PACKAGE = -1;
    final static int ENDING_PACKET_ADDRESS = 255;

    public static void main(String... args) throws IOException {
        Day23Part1 solution = new Day23Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var softwares = IntStream.range(0, 50)
                .mapToObj(i -> new Software(convertToLongArray(lines.get(0)), List.of(Long.valueOf(i))))
                .collect(Collectors.toList());
        var network = new Network(softwares);
        var executors = Executors.newFixedThreadPool(51);
        softwares.forEach(s -> executors.submit(() -> executeProgram(s)));
        executors.submit(() -> network.run());
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
            if (software.inputPackages.size() > 0) {
                var packet = software.inputPackages.remove();
                software.input.add(packet.X);
                software.input.add(packet.Y);
            }
            var input = software.input.size() > 0 ? software.input.remove(0) : EMPTY_PACKAGE;
            software.program.put(outputPosition, input);
            software.increasePointer(2);
        }),
        Output("4", (software) -> {
            var value = getParamValue(software, 1);
            var output = software.output;
            output.add(value);
            software.increasePointer(2);

            if (output.size() == 3) {
                software.outputPackages.add(new Packet(output.get(0).intValue(), output.get(1), output.get(2)));
                output.clear();
            }
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

    class Network {
        List<Software> softwares;

        public Network(List<Software> softwares) {
            this.softwares = softwares;
        }

        void run() {
            while (true) {
                softwares.forEach(s -> {
                    Packet packet = s.outputPackages.poll();
                    if (packet != null) {
                        log.debug("send packet to {}: {},{}", packet.address, packet.X, packet.Y);
                        if (packet.address == ENDING_PACKET_ADDRESS) {
                            log.warn("What is the Y actionValue of the first packet sent to address 255? {}", packet.Y);
                            System.exit(0);
                        }

                        softwares.get(packet.address).inputPackages.add(packet);
                    }
                });
            }
        }
    }

    static class Packet {
        final int address;
        final long X;
        final long Y;

        public Packet(int address, long x, long y) {
            this.address = address;
            X = x;
            Y = y;
        }
    }

    class Software {
        Map<Long, Long> program = new HashMap<>();
        List<Long> input;
        List<Long> output;
        long pointer;
        long relativeBase = 0;
        BlockingQueue<Packet> inputPackages = new LinkedBlockingQueue<>();
        BlockingQueue<Packet> outputPackages = new LinkedBlockingQueue<>();


        Software(long[] program, Collection<Long> inputs) {
            setProgram(program);
            this.input = new CopyOnWriteArrayList<>(inputs);
            this.output = new CopyOnWriteArrayList<>();
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
