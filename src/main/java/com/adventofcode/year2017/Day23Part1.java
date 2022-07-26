package com.adventofcode.year2017;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day23Part1 {
    final static String inputFile = "2017/day23_1.txt";
    
    public static void main(String... args) throws Exception {
        Day23Part1 solution = new Day23Part1();
        solution.run();
    }

    void run() throws Exception {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = totalMultiplyingInvoked(lines);
        System.out.println("how many times is the mul instruction invoked? " + result);
    }

    int totalMultiplyingInvoked(List<String> inputs) throws InterruptedException {
        List<Instruction> instructions = parseInstruction(inputs);    
        Program program = new Program(instructions);
        program.run();        
        return program.multiplyingInvoked;
    }

    List<Instruction> parseInstruction(List<String> inputs) {
        return inputs.stream().map(i -> {
            String[] input = i.split(" ");
            return new Instruction(stringToFunction(input[0]), Arrays.copyOfRange(input, 1, input.length));
        }).collect(Collectors.toList());
    }
    
    BiConsumer<Program, String[]> stringToFunction(String str) {
        return switch (str) {            
            case "set" -> this::set;
            case "sub" -> this::decrease;       
            case "mul" -> this::multiply;            
            case "jnz" -> this::jump;
            default -> throw new IllegalStateException("Unexpected value: " + str);
        };
    }

    class Program {
        final List<Instruction> instructions;
        Map<String, Long> registers = new HashMap<>();
        int currentInstruction = 0;
        int multiplyingInvoked = 0;
        
        public Program(List<Instruction> instructions) {
            this.instructions = instructions;                        
        }

        public void run() {
            try {
                while (currentInstruction < instructions.size()) {
                    runNextInstruction();
                }
            } catch (Exception e) {
                System.out.println("Program ended due to exception: " + e.getMessage());
                e.printStackTrace();
            }            
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
    
    void set(Program program, String[] params) {
        program.registers.put(params[0], program.getValue(params[1]));
        program.currentInstruction++;
    }

    void decrease(Program program, String[] params) {
        program.registers.put(params[0], program.getValue(params[0]) - program.getValue(params[1]));
        program.currentInstruction++;
    }

    void multiply(Program program, String[] params) {
        program.registers.put(params[0], program.getValue(params[0]) * program.getValue(params[1]));
        program.currentInstruction++;
        program.multiplyingInvoked++;
    }

    void jump(Program program, String[] params) {
        if (program.getValue(params[0]) != 0) {
            program.currentInstruction += program.getValue(params[1]);
        } else {
            program.currentInstruction++;
        }
    }

}
