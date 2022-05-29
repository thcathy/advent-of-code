package com.adventofcode.year2017;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Day8Part2 {
    Logger log = LoggerFactory.getLogger(Day8Part2.class);
    final static String inputFile = "2017/day8_1.txt";

    public static void main(String... args) throws IOException {
        Day8Part2 solution = new Day8Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = runInstruction(lines);
        log.warn("the highest value held in any register during this process = {}", result);
    }

    long runInstruction(List<String> inputs) {
        HashMap<String, Long> registers = new HashMap<>();
        Long maxValue = Long.MIN_VALUE;

        for (String input : inputs) {
            var instruction = parseInstruction(input);
            if (matchCondition(instruction, registers)) {
                var newValue = newRegisterValue(instruction, registers);
                if (newValue > maxValue) maxValue = newValue;
                registers.put(instruction.register, newRegisterValue(instruction, registers));
            }
        }

        return maxValue;
    }

    private Long newRegisterValue(Instruction instruction, HashMap<String, Long> registers) {
        var value = registers.getOrDefault(instruction.register, 0L);
        return switch (instruction.action) {
            case "inc" -> value + instruction.actionValue;
            case "dec" -> value - instruction.actionValue;
            default -> 0L;
        };
    }

    private boolean matchCondition(Instruction instruction, HashMap<String, Long> registers) {
        var refValue = registers.getOrDefault(instruction.refRegister, 0L);
        return switch (instruction.condition) {
            case ">" -> refValue > instruction.conditionValue;
            case "<" -> refValue < instruction.conditionValue;
            case ">=" -> refValue >= instruction.conditionValue;
            case "<=" -> refValue <= instruction.conditionValue;
            case "==" -> refValue == instruction.conditionValue;
            case "!=" -> refValue != instruction.conditionValue;
            default -> false;
        };
    }

    private Instruction parseInstruction(String input) {
        var array = input.split(" ");
        return new Instruction(array[0], array[1], Long.parseLong(array[2]), array[4], array[5], Long.parseLong(array[6]));
    }

    record Instruction(String register, String action, long actionValue, String refRegister, String condition, long conditionValue) {}
}
