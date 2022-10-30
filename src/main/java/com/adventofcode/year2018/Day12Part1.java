package com.adventofcode.year2018;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day12Part1 {
    final static String inputFile = "2018/day12.txt";
    final static int maxGenerations = 20;
    final static int prefixLength = 100;

    public static void main(String... args) throws IOException {
        Day12Part1 solution = new Day12Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = run(parseInitialState(lines.get(0)), parseSpreadNotes(lines.subList(2, lines.size())));
        System.out.println("After 20 generations, what is the sum of the numbers of all pots which contain a plant? " + result);

    }

    int run(char[] initialState, Map<char[], Character> spreadNotes) {
        var state = ArrayUtils.addAll(defaultPots(prefixLength), initialState);
        state = ArrayUtils.addAll(state, defaultPots(prefixLength));
        state = runGenerations(state, spreadNotes);
        return sumPots(state);
    }

    int sumPots(char[] state) {
        int sum = 0;
        for (int i=0; i < state.length; i++) {
            if (state[i] == '#') sum += i-(prefixLength);
        }
        return sum;
    }

    char[] defaultPots(int length) {
        char[] pots = new char[length];
        for (int i=0; i < pots.length; i++) pots[i] = '.';
        return pots;
    }

    char[] runGenerations(char[] state, Map<char[], Character> spreadNotes) {
        for (int i=0; i<maxGenerations; i++) {
            state = nextState(state, spreadNotes);
        }        
        return state;
    }

    char[] nextState(char[] state, Map<char[], Character> spreadNotes) {
        char[] nextState = new char[state.length];
        for (int i=0; i<state.length; i++) {
            nextState[i] = nextState(state, i, spreadNotes);
        }        
        return nextState;
    }

    char nextState(char[] state, int i, Map<char[], Character> spreadNotes) {
        if (i < 2 || i > state.length-3) return '.';

        for (Entry<char[], Character> note : spreadNotes.entrySet()) {
            if (isMatch(state, i, note.getKey())) {
                return note.getValue();
            }
        }
        return '.';
    }

    boolean isMatch(char[] state, int i, char[] note) {
        for (int x=0; x<5; x++) {
            if (state[i-2+x] != note[x]) {
                return false;
            }
        }
        return true;
    }

    char[] parseInitialState(String input) {
        return input.replace("initial state: ", "").toCharArray();
    }

    Map<char[], Character> parseSpreadNotes(List<String> inputs) {
        var spreadNotes = new HashMap<char[], Character>();
        for (String input : inputs) {
            var arr = input.split(" => ");
            spreadNotes.put(arr[0].toCharArray(), arr[1].charAt(0));
        }
        return spreadNotes;
    }

    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2018/day12_test.txt"), Charsets.UTF_8);
        var spreadNotes = parseSpreadNotes(lines.subList(2, lines.size()));

        assertEquals(14, spreadNotes.size());
        assertEquals(true, isMatch("#..#.#..##......###...###...........".toCharArray(), 3, "..#.#".toCharArray()));
        assertEquals(false, isMatch("#..#.#..##......###...###...........".toCharArray(), 4, "#..#.".toCharArray()));
        assertArrayEquals("...#...#....#.....#..#..#..#...........".toCharArray(), nextState("...#..#.#..##......###...###...........".toCharArray(), spreadNotes));
        assertEquals(325, run(parseInitialState(lines.get(0)), spreadNotes));
    }
}
