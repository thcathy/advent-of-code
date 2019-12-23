package com.adventofcode.year2016;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Resources;

import static org.junit.Assert.assertEquals;

public class Day21Part1 {
    Logger log = LoggerFactory.getLogger(Day21Part1.class);
    String inputFile = "2016/day21_1.txt";

    public static void main(String... args) throws Exception {
        Day21Part1 solution = new Day21Part1();
        solution.run();
    }

    void run() throws Exception {
        firstStar();
    }

    void firstStar() throws IOException {
        var inputs = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        log.warn("First star - what is the result of scrambling? {}", runOperations("abcdefgh", inputs));
    }

    String runOperations(String input, List<String> operations) {
        var characters = input.chars().mapToObj(c -> (char)c).collect(Collectors.toList());
        for (String operation: operations) {
            characters = runOperation(characters, operation);
            log.info("after {}, {}", operation, characters);
        }
        return Joiner.on("").join(characters);
    }

    List<Character> runOperation(List<Character> characters, String operation) {
        var inputs = operation.split(" ");
        if (operation.startsWith("swap position"))
            return swapPosition(characters, Integer.valueOf(inputs[2]), Integer.valueOf(inputs[5]));
        else if (operation.startsWith("swap letter"))
            return swapLetter(characters, inputs[2].charAt(0), inputs[5].charAt(0));
        else if (operation.startsWith("rotate left"))
            return rotateLeft(characters, Integer.valueOf(inputs[2]));
        else if (operation.startsWith("rotate right"))
            return rotateRight(characters, Integer.valueOf(inputs[2]));
        else if (operation.startsWith("rotate based on position of letter"))
            return rotateAt(characters, inputs[6].charAt(0));
        else if (operation.startsWith("reverse positions"))
            return reverse(characters, Integer.valueOf(inputs[2]), Integer.valueOf(inputs[4]));
        else if (operation.startsWith("move position"))
            return movePosition(characters, Integer.valueOf(inputs[2]), Integer.valueOf(inputs[5]));

        return characters;
    }

    List<Character> swapPosition(List<Character> string, int x, int y) {
        var temp = string.get(x);
        string.set(x, string.get(y));
        string.set(y, temp);
        return string;
    }

    List<Character> swapLetter(List<Character> string, char x, char y) {
        return swapPosition(string, string.indexOf(x), string.indexOf(y));
    }

    List<Character> rotateLeft(List<Character> string, int step) {
        var sublist = string.subList(step, string.size());
        sublist.addAll(string.subList(0, step));
        return sublist;
    }

    List<Character> rotateRight(List<Character> string, int step) {
        var sublist = string.subList(string.size() - step, string.size());
        sublist.addAll(string.subList(0, string.size() - step));
        return sublist;
    }

    List<Character> rotateAt(List<Character> string, char x) {
        int position = string.indexOf(x);
        string = rotateRight(string, 1);
        string = rotateRight(string, position);
        if (position >= 4) string = rotateRight(string, 1);
        return string;
    }

    List<Character> reverse(List<Character> string, int start, int end) {
        var newString = new ArrayList(string.subList(0, start));
        var middle = string.subList(start, end + 1);
        Collections.reverse(middle);
        newString.addAll(middle);
        newString.addAll(string.subList(end + 1, string.size()));
        return newString;
    }

    List<Character> movePosition(List<Character> string, int from, int to) {
        string.add(to, string.remove(from));
        return string;
    }

    @Test
    public void test_runOperations() {
        var result = runOperations("abcde", List.of(
                "swap position 4 with position 0",
                "swap letter d with letter b",
                "reverse positions 0 through 4",
                "rotate left 1 step",
                "move position 1 to position 4",
                "move position 3 to position 0",
                "rotate based on position of letter b",
                "rotate based on position of letter d"));
        assertEquals("decab", result);
    }

    @Test
    public void test_rotateAt() {
        var characters = "a-------".chars().mapToObj(c -> (char)c).collect(Collectors.toList());
        log.debug("{}", rotateAt(characters, 'a'));
        characters = "-a------".chars().mapToObj(c -> (char)c).collect(Collectors.toList());
        log.debug("{}", rotateAt(characters, 'a'));
        characters = "--a-----".chars().mapToObj(c -> (char)c).collect(Collectors.toList());
        log.debug("{}", rotateAt(characters, 'a'));
        characters = "---a----".chars().mapToObj(c -> (char)c).collect(Collectors.toList());
        log.debug("{}", rotateAt(characters, 'a'));
        characters = "----a---".chars().mapToObj(c -> (char)c).collect(Collectors.toList());
        log.debug("{}", rotateAt(characters, 'a'));
        characters = "-----a--".chars().mapToObj(c -> (char)c).collect(Collectors.toList());
        log.debug("{}", rotateAt(characters, 'a'));
        characters = "------a-".chars().mapToObj(c -> (char)c).collect(Collectors.toList());
        log.debug("{}", rotateAt(characters, 'a'));
        characters = "-------a".chars().mapToObj(c -> (char)c).collect(Collectors.toList());
        log.debug("{}", rotateAt(characters, 'a'));
    }
}