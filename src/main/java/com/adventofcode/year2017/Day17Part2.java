package com.adventofcode.year2017;


import java.io.IOException;

public class Day17Part2 {
    final static int ENDING_SIZE = 50_000_000;
    final static int STEPPING = 354;

    public static void main(String... args) throws IOException {
        Day17Part2 solution = new Day17Part2();
        solution.run();
    }

    void run() throws IOException {
        var result = findShortCircuitNumber(STEPPING, ENDING_SIZE);
        System.out.println("What is the value after 0 the moment 50000000 is inserted? " + result);
    }

    int findShortCircuitNumber(int stepping, int finalSize) {
        int valueAtPosition1 = -1;
        int currentPosition = 0;
        for (int i = 1; i <= finalSize; i++) {
            currentPosition = ((currentPosition + stepping) % i) + 1;
            if (currentPosition == 1) {
                valueAtPosition1 = i;
            }                
        }
        return valueAtPosition1;
    }
   
}
