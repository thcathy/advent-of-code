package com.adventofcode.year2016;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day25 {
    Logger log = LoggerFactory.getLogger(Day25.class);
    Pattern numericPattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    final static String inputFile = "2016/day25_1.txt";
    final int maxSignalCountRequire = 10000;

    int clockSignal;
    int signalCount;

    static Map<String, Function<InstructionInput, Integer>> instructionFunctions = new HashMap<>();
    {
        instructionFunctions.put("cpy", this::copy);
        instructionFunctions.put("inc", this::increase);
        instructionFunctions.put("dec", this::decrease);
        instructionFunctions.put("jnz", this::jump);
        instructionFunctions.put("tgl", this::toggle);
        instructionFunctions.put("out", this::transmits);
    }

    public static void main(String... args) throws IOException {
        Day25 solution = new Day25();
        solution.firstStar();
    }

    void firstStar() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);

        for (int i=0; i < Integer.MAX_VALUE; i++) {
            clockSignal = -1;   signalCount = 0;
            var code = startingCode();
            code.put('a', i);
            try {
                runInstructions(code, lines.stream().map(s -> s.split(" ")).collect(Collectors.toList()));
            } catch (SignalMatchedException e) {
                log.warn("First star - What is the lowest positive integer that can be used to initialize register a? {}", i);
                break;
            } catch (RuntimeException e) {} // continue to run
        }
    }

    class InstructionInput {
        Map<Character, Integer> code;
        List<String[]> instructions;
        String[] params;
        int instructionPosition;

        InstructionInput(Map<Character, Integer> code, List<String[]> instructions, String[] params,
                         int instructionPosition) {
            this.code = code;
            this.instructions = instructions;
            this.params = params;
            this.instructionPosition = instructionPosition;
        }
    }

    void runInstructions(Map<Character, Integer> code, List<String[]> instructions) {
        int i = 0;
        while (i < instructions.size()) {
            String[] inputs = instructions.get(i);
            i += instructionFunctions.get(inputs[0]).apply(new InstructionInput(code, instructions, inputs, i));
        }
    }

    Map<Character, Integer> startingCode() {
        var map = new HashMap<Character, Integer>();
        map.put('a', 0); map.put('b', 0); map.put('c', 0); map.put('d', 0);
        return map;
    }

    int copy(InstructionInput input) {
        if (isNumeric(input.params[2])) return 1;

        try {
            var value = getValue(input.params[1], input.code);
            input.code.put(input.params[2].charAt(0), value);
        } catch (Exception e) { // skip
        }
        return 1;
    }

    int increase(InstructionInput input) {
        var register = input.params[1].charAt(0);
        var value = input.code.get(register) + 1;
        input.code.put(register, value);
        return 1;
    }

    int decrease(InstructionInput input) {
        var register = input.params[1].charAt(0);
        var value = input.code.get(register) - 1;
        input.code.put(register, value);
        return 1;
    }

    int jump(InstructionInput input) {
        try {
            int valueX = getValue(input.params[1], input.code);
            int valueY = getValue(input.params[2], input.code);
            if (valueX != 0)
                return valueY;
            else
                return 1;
        } catch (Exception e) {
            return 1;
        }
    }

    int getValue(String input, Map<Character, Integer> code) {
        if (isNumeric(input)) {
            return Integer.parseInt(input);
        } else {
            return code.get(input.charAt(0));
        }
    }

    boolean isNumeric(String input) {
        return numericPattern.matcher(input).matches();
    }

    int toggle(InstructionInput input) {
        int togglePosition =  input.code.get(input.params[1].charAt(0)) + input.instructionPosition;
        if (togglePosition < 0 || togglePosition >= input.instructions.size()) return 1;

        String[] instruction = input.instructions.get(togglePosition);

        if (instruction.length == 2) {
            if (instruction[0].equals("inc")) {
                instruction[0] = "dec";
            } else {
                instruction[0] = "inc";
            }
        } else if (instruction.length == 3) {
            if (instruction[0].equals("jnz")) {
                instruction[0] = "cpy";
            } else {
                instruction[0] = "jnz";
            }
        }

        input.instructions.set(togglePosition, instruction);
        return 1;
    }

    int transmits(InstructionInput input) {
        int value = getValue(input.params[1], input.code);
        if (clockSignal == value) {
            throw new RuntimeException("incorrect clock signal pattern");
        }
        clockSignal = value;
        signalCount++;
        if (signalCount >= maxSignalCountRequire) {
            throw new SignalMatchedException();
        }
        return 1;
    }

    class SignalMatchedException extends RuntimeException {}
}
