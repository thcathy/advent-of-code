package com.adventofcode.year2017;


import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class Day17Part1 {
    final static int ENDING_SIZE = 2018;
    final static int STEPPING = 354;

    public static void main(String... args) throws IOException {
        Day17Part1 solution = new Day17Part1();
        solution.run();
    }

    void run() throws IOException {
        var result = findShortCircuitNumber(STEPPING);
        System.out.println("What is the value after 2017 in your completed circular buffer? " + result);
    }

    int findShortCircuitNumber(int stepping) {
        List<Integer> list = new LinkedList<>();
        list.add(0);
        int pos = 0;
        for (int i = 1; i < ENDING_SIZE; i++) {
            pos = nextState(list, pos, i, stepping);
        }
        return list.get(pos + 1);
    }

    int nextState(List<Integer> buffer, int position, int nextValue, int stepping) {
        position = (position + stepping) % buffer.size();
        buffer.add(position + 1, nextValue);
        return position + 1;
    }
    
    @Test
    public void unitTest() {
        assertEquals(638, findShortCircuitNumber(3));
    }
}
