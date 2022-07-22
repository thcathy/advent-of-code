package com.adventofcode.year2017;


import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day18Part1 {
    final static String inputFile = "2017/day18_1.txt";
    
    public static void main(String... args) throws IOException {
        Day18Part1 solution = new Day18Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = recoveredFrequency(lines);
        System.out.println("What is the value of the recovered frequency? " + result);
    }

    long recoveredFrequency(List<String> inputs) {
        List<Instruction> instructions = parseInstruction(inputs);
        Program program = new Program(instructions);
        while (program.recoverValue < 0) {
            program.runNextInstruction();
        }
        return program.recoverValue;
    }

    List<Instruction> parseInstruction(List<String> inputs) {
        return inputs.stream().map(i -> {
            String[] input = i.split(" ");
            return new Instruction(stringToFunction(input[0]), Arrays.copyOfRange(input, 1, input.length));
        }).collect(Collectors.toList());
    }
    
    BiConsumer<Program, String[]> stringToFunction(String str) {
        return switch (str) {
            case "snd" -> this::playASound;
            case "set" -> this::set;
            case "add" -> this::increase;
            case "mul" -> this::multiply;
            case "mod" -> this::remainder;
            case "rcv" -> this::recover;
            case "jgz" -> this::jump;
            default -> throw new IllegalStateException("Unexpected value: " + str);
        };
    }

    class Program {
        final List<Instruction> instructions;
        Map<String, Long> registers = new HashMap<>();
        long playValue = -1;
        long recoverValue = -1;        
        int currentInstruction = 0;

        public Program(List<Instruction> instructions) {
            this.instructions = instructions;
        }

        public void runNextInstruction() {
            Instruction instruction = instructions.get(currentInstruction);
            instruction.function.accept(this, instruction.params);
        }

        long getValue(String r) {
            if (NumberUtils.isCreatable(r)) {
                return Integer.valueOf(r);
            }
            return registers.getOrDefault(r, 0L);
        }
    }

    record Instruction(BiConsumer<Program, String[]> function, String[] params) {
    }

    void playASound(Program program, String[] params) {
        program.playValue = program.getValue(params[0]);
        program.currentInstruction++;
    }
    
    void set(Program program, String[] params) {
        program.registers.put(params[0], program.getValue(params[1]));
        program.currentInstruction++;
    }

    void increase(Program program, String[] params) {
        program.registers.put(params[0], program.getValue(params[0]) + program.getValue(params[1]));
        program.currentInstruction++;
    }

    void multiply(Program program, String[] params) {
        program.registers.put(params[0], program.getValue(params[0]) * program.getValue(params[1]));
        program.currentInstruction++;
    }

    void remainder(Program program, String[] params) {
        program.registers.put(params[0], program.getValue(params[0]) % program.getValue(params[1]));
        program.currentInstruction++;
    }

    void recover(Program program, String[] params) {
        if (program.getValue(params[0]) != 0) {
            program.recoverValue = program.playValue;
        }
        program.currentInstruction++;
    }

    void jump(Program program, String[] params) {
        if (program.getValue(params[0]) > 0) {
            program.currentInstruction += program.getValue(params[1]);
        } else {
            program.currentInstruction++;
        }
    }

    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2017/day18_test.txt"), Charsets.UTF_8);
        assertEquals(4, recoveredFrequency(lines));
    }
}
