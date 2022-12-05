package com.adventofcode.year2022;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day5Part2 {
    Logger log = LoggerFactory.getLogger(Day5Part2.class);
    final static String inputFile = "2022/day5.txt";

    public static void main(String... args) throws IOException {
        Day5Part2 solution = new Day5Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = executeCrane(parseInput(lines));
        System.out.println("After the rearrangement procedure completes, what crate ends up on top of each stack? " + result);
    }

    String executeCrane(Input input) {
        for (Instruction instruction : input.instructions) {
            runInstruction(input.stacks, instruction);
        }
        return cratesOnTop(input.stacks);
    }

    String cratesOnTop(List<LinkedList<Character>> stacks) {
        var sb = new StringBuilder();
        for (var stack : stacks) sb.append(stack.getLast());
        return sb.toString();
    }

    void runInstruction(List<LinkedList<Character>> stacks, Instruction Instruction) {
        var crates = new LinkedList<Character>();
        for (int i = 0; i < Instruction.numOfCrates; i++) {
            crates.addFirst(stacks.get(Instruction.from - 1).removeLast());
        }
        stacks.get(Instruction.to - 1).addAll(crates);
    }

    record Input(List<LinkedList<Character>> stacks, List<Instruction> instructions) {}

    record Instruction(int from, int to, int numOfCrates) {}

    Input parseInput(List<String> strings) {
        int i = 0;
        List<String> stacksInput = new ArrayList<>();
        while (!strings.get(i).startsWith(" 1")) {
            stacksInput.add(strings.get(i));
            i++;
        }
        int numOfStacks = numOfStacks(strings.get(i).split(" "));
        List<LinkedList<Character>> crane = IntStream.rangeClosed(1, numOfStacks).mapToObj(c -> new LinkedList<Character>()).toList();
        for (String crates : stacksInput) {
            for (int j = 0; j < crates.length(); j += 4) {
                char crate = crates.charAt(j+1);
                if (crate != ' ') {
                    crane.get(j / 4).addFirst(crate);
                }
            }
        }

        var instructions = strings.subList(i+2, strings.size()).stream().map(this::parseInstruction).toList();

        return new Input(crane, instructions);
    }

    int numOfStacks(String[] stacks) { return Integer.parseInt(stacks[stacks.length - 1]); }

    Instruction parseInstruction(String input) {
        var inputs = input.split(" ");
        return new Instruction(Integer.parseInt(inputs[3]), Integer.parseInt(inputs[5]), Integer.parseInt(inputs[1]));
    }

    @Test
    public void unitTest() throws Exception {
        var lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day5_test.txt").toURI()), Charset.defaultCharset());
        var input = parseInput(lines);
        assertEquals(4, input.instructions.size());
        assertEquals(new Instruction(2, 1, 1), input.instructions.get(0));
        assertEquals(3, input.stacks.size());
        assertEquals(2, input.stacks.get(0).size());
        assertEquals('Z', input.stacks.get(0).getFirst().charValue());

        assertEquals("MCD", executeCrane(input));
    }
}
