package com.adventofcode.year2017;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day16Part2 {
    final static String inputFile = "2017/day16_1.txt";
    final static int PROGRAM_SIZE = 16;
    final static int DANCE_TIMES = 1_000_000_000;

    public static void main(String... args) throws IOException {
        Day16Part2 solution = new Day16Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = programStanding(PROGRAM_SIZE, lines.get(0), DANCE_TIMES);
        System.out.printf("In what order are the programs standing after their billion dances? %s %n", result);
    }

    String programStanding(int programSize, String inputs, int times) {
        char[] programs = createPrograms(programSize);        
        List<Move> moves = parseInputs(inputs);
        String firstPattern = new String(programs);
        int repeatTimes = 1;
        for (int i = 0; i < times; i++) {
            programs = dance(programs, moves);
            if (firstPattern.equals(new String(programs))) {
                repeatTimes = i+1;
                break;
            }
        }
        times = times % repeatTimes;
        for (int i = 0; i < times; i++) {
            programs = dance(programs, moves);
        }
        return new String(programs);
    }

    char[] dance(char[] programs, List<Move> moves) {
        for (Move move : moves) {
            programs = move.function.apply(programs, move.params);
        }
        return programs;
    }

    List<Move> parseInputs(String inputs) {
        List<Move> moves = new ArrayList<>();
        for (String input : inputs.split(",")) {
            if (input.startsWith("s")) {
                moves.add(new Move(this::spin, new int[] { Integer.valueOf(input.substring(1)).intValue() }));
            } else if (input.startsWith("x")) {                
                String[] params = input.substring(1).split("/");
                moves.add(new Move(this::exchange, 
                    new int[] { Integer.valueOf(params[0]), Integer.valueOf(params[1]) })
                );                
            } else if (input.startsWith("p")) {
                moves.add(new Move(this::partner, 
                    new int[] { input.charAt(1), input.charAt(3) })
                );
            }
        }
        return moves;
    }

    record Move(BiFunction<char[], int[], char[]> function, int[] params) {}

    char[] createPrograms(int size) {
        char[] array = new char[size];
        for (int i = 0; i < size; i++) {
            array[i] = (char)(i + 97);
        }
        return array;
    }

    char[] spin(char[] array, int[] params) {
        char[] newArray = new char[array.length];
        for (int i = 0; i < array.length; i++) {
            newArray[(i+params[0]) % array.length] = array[i];
        }
        return newArray;
    }

    char[] exchange(char[] array, int[] params) {
        char temp = array[params[0]];
        array[params[0]] = array[params[1]];
        array[params[1]] = temp;
        return array;
    }

    char[] partner(char[] array, int[] params) {        
        return exchange(array,
                new int[] {
                        ArrayUtils.indexOf(array, (char) params[0]),
                        ArrayUtils.indexOf(array, (char) params[1])
            });
    }

    @Test
    public void unitTest() {       
        char[] characters = createPrograms(5);
        characters = spin(characters, new int[] {1});
        assertArrayEquals(new char[]{'e', 'a', 'b', 'c', 'd'}, characters);
        assertArrayEquals(new char[]{'e', 'a', 'b', 'd', 'c'}, exchange(characters, new int[] {3, 4}));
        assertArrayEquals(new char[]{ 'b', 'a', 'e', 'd', 'c'}, partner(characters, new int[] { 'e', 'b' }));

        assertEquals("ceadb", programStanding(5, "s1,x3/4,pe/b", 2));
    }
}
