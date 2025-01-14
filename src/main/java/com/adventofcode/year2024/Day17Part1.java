package com.adventofcode.year2024;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Day17Part1 {
    private static final Logger log = LoggerFactory.getLogger(Day17Part1.class);
    private static final String INPUT_FILE = "2024/day17.txt";

    public static void main(String... args) throws IOException {
        new Day17Part1().run();
    }

    private void run() throws IOException {
        var input = parseInput();
        String output = executeProgram(input.registerA, input.registerB, input.registerC, input.program);
        log.info("Program output: {}", output);
    }

    private Input parseInput() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(INPUT_FILE), Charsets.UTF_8);
        int registerA = Integer.parseInt(lines.get(0).split(": ")[1]);
        int registerB = Integer.parseInt(lines.get(1).split(": ")[1]);
        int registerC = Integer.parseInt(lines.get(2).split(": ")[1]);
        String[] programStr = lines.get(4).replaceFirst("Program: ", "").split(","); // Assuming program starts at line 4
        int[] program = new int[programStr.length];
        for (int i = 0; i < programStr.length; i++) {
            program[i] = Integer.parseInt(programStr[i]);
        }
        return new Input(registerA, registerB, registerC, program);
    }

    private String executeProgram(int registerA, int registerB, int registerC, int[] program) {
        StringBuilder outputBuilder = new StringBuilder();
        int instructionPointer = 0;

        while (instructionPointer < program.length) {
            int opcode = program[instructionPointer];
            int operand = program[instructionPointer + 1];

            switch (opcode) {
                case 0: // adv
                    int denominator = 1 << operandValue(operand, registerA, registerB, registerC);
                    registerA /= denominator;
                    break;
                case 1: // bxl
                    registerB ^= operand;
                    break;
                case 2: // bst
                    registerB = operandValue(operand, registerA, registerB, registerC) % 8;
                    break;
                case 3: // jnz
                    if (registerA != 0) {
                        instructionPointer = operand;
                        continue;
                    }
                    break;
                case 4: // bxc
                    registerB ^= registerC;
                    break;
                case 5: // out
                    outputBuilder.append(operandValue(operand, registerA, registerB, registerC) % 8).append(',');
                    break;
                case 6: // bdv
                    registerB = registerA / (1 << operandValue(operand, registerA, registerB, registerC));
                    break;
                case 7: // cdv
                    registerC = registerA / (1 << operandValue(operand, registerA, registerB, registerC));
                    break;
            }

            instructionPointer += 2;
            if (instructionPointer >= program.length) {
                break;
            }
        }

        if (!outputBuilder.isEmpty()) {
            outputBuilder.setLength(outputBuilder.length() - 1);
        }
        return outputBuilder.toString();
    }

    private int operandValue(int operand, int registerA, int registerB, int registerC) {
        return switch (operand) {
            case 0, 1, 2, 3 -> operand;
            case 4 -> registerA;
            case 5 -> registerB;
            case 6 -> registerC;
            default -> throw new IllegalArgumentException("Invalid operand: " + operand);
        };
    }

    private record Input(int registerA, int registerB, int registerC, int[] program) {}
}
