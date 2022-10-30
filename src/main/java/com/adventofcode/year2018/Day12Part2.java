package com.adventofcode.year2018;


import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day12Part2 {
    final static String inputFile = "2018/day12.txt";
    final static long maxGenerations = 50000000000L;
    
    public static void main(String... args) throws IOException {
        Day12Part2 solution = new Day12Part2();        
        solution.run();        
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = run(parseInitialState(lines.get(0)), parseSpreadNotes(lines.subList(2, lines.size())));
        System.out.println("After " + maxGenerations + " generations, what is the sum of the numbers of all pots which contain a plant? " + result);
    }

    long run(String initialPattern, Map<char[], Character> spreadNotes) {
        var state = new State(0, initialPattern);
        var states = new HashMap<Long, State>();
        states.put(0L, state);
        long generation = 0L;        
        Entry<Long, State> matchEntry = null;

        while (matchEntry == null) {
            state = nextState(state, spreadNotes);
            generation++;
            matchEntry = matchingState(state, states);
            if (matchEntry == null) {
                states.put(generation, state);
            }
        }
        
        return sum(new State(state.startPosition + (maxGenerations - generation), state.pattern));
    }

    Entry<Long, State> matchingState(State state, Map<Long, State> states) {
        for (Entry<Long, State> entry : states.entrySet()) {
            if (entry.getValue().pattern.equals(state.pattern)) {
                return entry;
            }
        }
        return null;
    }

    long sum(State state) {
        long sum = 0;
        for (int i = 0; i < state.pattern.length(); i++) {
            if (state.pattern.charAt(i) == '#') {
                sum += state.startPosition + i;
            }
        }
        return sum;
    }


    State runGenerations(State state, Map<char[], Character> spreadNotes) {
        for (int i=0; i<maxGenerations; i++) {
            state = nextState(state, spreadNotes);
        }        
        return state;
    }

    State nextState(State state, Map<char[], Character> spreadNotes) {
        StringBuilder nextPattern = new StringBuilder();
        int offset = 0;
        for (int i = -4; i < state.pattern.length() + 4; i++) {
            var nextChar = nextState(state.pattern, i, spreadNotes);
            if (nextChar == '.' && nextPattern.length() <= 0) {
                offset = i + 1;
            } else {
                nextPattern.append(nextChar);
            }
        }
        while (nextPattern.charAt(nextPattern.length() - 1) == '.') {
            nextPattern.deleteCharAt(nextPattern.length() - 1);
        }
        return new State(state.startPosition + offset, nextPattern.toString());
    }

    record State(long startPosition, String pattern) {}

    char nextState(String state, int position, Map<char[], Character> spreadNotes) {
        for (Entry<char[], Character> note : spreadNotes.entrySet()) {
            if (isMatch(state, position, note.getKey())) {
                return note.getValue();
            }
        }        
        return '.';
    }

    boolean isMatch(String state, int start, char[] note) {
        for (int i = 0; i < 5; i++) {
            var position = start + i - 2;
            var value = position < 0 || position >= state.length() ? '.' : state.charAt(position);
            if (value != note[i]) {
                return false;
            }
        }
        return true;
    }

    String parseInitialState(String input) {
        return input.replace("initial state: ", "");
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
        assertEquals(true, isMatch("#..#.#..##......###...###...........", 3, "..#.#".toCharArray()));
        assertEquals(false, isMatch("#..#.#..", 7, "#...#".toCharArray()));
        assertEquals(true, isMatch("##.#.#....#...#..##..##..##", -1, "...##".toCharArray()));
        assertEquals(true, isMatch("##.#.####..#####", -2, "....#".toCharArray()));

        assertEquals(new State(0, "#..###.#...##..#...#...#...#"), nextState(new State(1, "##.#.#....#...#..##..##..##"), spreadNotes));
        assertEquals(new State(0, "#...##...#.#...#.#...#...#...#"), nextState(new State(-1, "#.#..#...#.##....##..##..##..##"), spreadNotes));
        assertEquals(new State(-2, "#....##....#####...#######....#.#..##"), nextState(new State(-2, "#..###.#..#.#.#######.#.#.#..#.#...#"), spreadNotes));
        assertEquals(325, sum(new State(-2, "#....##....#####...#######....#.#..##")));
    }
}
