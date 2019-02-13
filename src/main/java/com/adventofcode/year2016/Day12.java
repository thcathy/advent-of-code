package com.adventofcode.year2016;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.assertEquals;

public class Day12 {
    Logger log = LoggerFactory.getLogger(Day12.class);
    final static String inputFile = "2016/day12_1.txt";

    static Map<String, BiFunction<Map<Character, Integer>, String[], Integer>> instructionFunctions = new HashMap<>();
    {
        instructionFunctions.put("cpy", this::copy);
        instructionFunctions.put("inc", this::increase);
        instructionFunctions.put("dec", this::decrease);
        instructionFunctions.put("jnz", this::jump);
    }

    public static void main(String... args) throws IOException {
        Day12 solution = new Day12();
        solution.firstStar();
        solution.secondStar();
    }

    void firstStar() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var code = startingCode();
        runInstructions(code, lines);

        log.warn("First star - what value is left in register a? {}", code.get('a'));
    }

    void secondStar() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var code = startingCode();
        code.put('c', 1);
        runInstructions(code, lines);

        log.warn("Second star - what value is left in register a? {}", code.get('a'));
    }

    void runInstructions(Map<Character, Integer> code, List<String> instructions) {
        int i = 0;
        while (i < instructions.size()) {
            String[] inputs = instructions.get(i).split(" ");
            i += instructionFunctions.get(inputs[0]).apply(code, inputs);
        }
    }

    Map<Character, Integer> startingCode() {
        var map = new HashMap<Character, Integer>();
        map.put('a', 0); map.put('b', 0); map.put('c', 0); map.put('d', 0);
        return map;
    }

    int copy(Map<Character, Integer> code, String[] params) {
        var value = NumberUtils.isDigits(params[1]) ? Integer.valueOf(params[1]) : code.get(params[1].charAt(0));
        code.put(params[2].charAt(0), value);
        return 1;
    }

    int increase(Map<Character, Integer> code, String[] params) {
        var value = code.get(params[1].charAt(0)) + 1;
        code.put(params[1].charAt(0), value);
        return 1;
    }

    int decrease(Map<Character, Integer> code, String[] params) {
        var value = code.get(params[1].charAt(0)) - 1;
        code.put(params[1].charAt(0), value);
        return 1;
    }

    int jump(Map<Character, Integer> code, String[] params) {
        if (NumberUtils.isDigits(params[1]) && Integer.valueOf(params[1]) != 0 )
            return Integer.valueOf(params[2]);
        else if (code.get(params[1].charAt(0)) != 0)
            return Integer.valueOf(params[2]);
        else
            return 1;
    }

    @Test
    public void test_runInstructions() {
        var instructions = List.of(
                "cpy 41 a",
                "inc a",
                "inc a",
                "dec a",
                "jnz a 2",
                "dec a");
        var code = startingCode();
        runInstructions(code, instructions);
        assertEquals(42, code.get('a').intValue());
    }
}