package com.adventofcode.year2017;


import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Resources;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

public class Day16Part1 {
    final static String inputFile = "2017/day16_1.txt";
    final static int PROGRAM_SIZE = 16;

    public static void main(String... args) throws IOException {
        Day16Part1 solution = new Day16Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = programStanding(PROGRAM_SIZE, lines.get(0));
        System.out.printf("In what order are the programs standing after their dance? %s %n", result);
    }

    String programStanding(int programSize, String inputs) {
        List<Character> programs = createPrograms(programSize);
        for (String input : inputs.split(",")) {
            if (input.startsWith("s")) {
                programs = spin(programs, Integer.valueOf(input.substring(1)));
            } else if (input.startsWith("x")) {
                String[] params = input.substring(1).split("/");
                programs = exchange(programs, Integer.valueOf(params[0]), Integer.valueOf(params[1]));
            } else if (input.startsWith("p")) {
                programs = partner(programs, input.charAt(1), input.charAt(3));
            }
        }
        return Joiner.on("").join(programs);
    }

    List<Character> createPrograms(int size) {
        return IntStream.range(0, size)
            .mapToObj(i -> {
                i+=97;
                return (char) i;
                }).collect(Collectors.toList());
    }

    List<Character> spin(List<Character> list, int programs) {
        var newList = new ArrayList<>(list.subList(list.size() - programs, list.size()));
        newList.addAll(list.subList(0, list.size() - programs));
        return newList;
    }

    List<Character> exchange(List<Character> list, int pos1, int pos2) {
        Collections.swap(list, pos1, pos2);
        return list;
    }

    List<Character> partner(List<Character> list, char a, char b) {
        return exchange(list, list.indexOf(a), list.indexOf(b));
    }

    @Test
    public void unitTest() {
        List<Character> characters = createPrograms(5);
        characters = spin(characters, 1);
        assertEquals(Arrays.asList('e', 'a', 'b', 'c', 'd'), characters);
        assertEquals(Arrays.asList('e', 'a', 'b', 'd', 'c'), exchange(characters, 3, 4));
        assertEquals(Arrays.asList('b', 'a', 'e', 'd', 'c'), partner(characters, 'e', 'b'));

        assertEquals("baedc", programStanding(5, "s1,x3/4,pe/b"));
    }
}
