package com.adventofcode.year2017;


import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class Day15Part2 {
    final static long GENERATOR_A_ORIGIN_VALUE = 703;
    final static long GENERATOR_B_ORIGIN_VALUE = 516;
    final static long GENERATOR_A_FACTOR = 16807;
    final static long GENERATOR_B_FACTOR = 48271;
    final static long GENERATOR_A_MULTIPLE = 4;
    final static long GENERATOR_B_MULTIPLE = 8;
    final static long TEST_CYCLE = 5_000_000;
    
    public static void main(String... args) throws IOException {
        Day15Part2 solution = new Day15Part2();
        solution.run();
    }

    void run() throws IOException {
        var result = totalMatchingPairs(GENERATOR_A_ORIGIN_VALUE, GENERATOR_B_ORIGIN_VALUE);
        System.out.printf("what is the judge's final count? %s %n", result);
    }

    int totalMatchingPairs(long a, long b) {
        int matched = 0;
        Generator generatorA = new Generator(GENERATOR_A_FACTOR, a, GENERATOR_A_MULTIPLE);
        Generator generatorB = new Generator(GENERATOR_B_FACTOR, b, GENERATOR_B_MULTIPLE);
        for (int i = 0; i < TEST_CYCLE; i++) {
            generatorA.nextValue();
            generatorB.nextValue();
            if (judge(generatorA.value, generatorB.value)) {
                matched++;
            }
        }
        return matched;
     }

    boolean judge(long a, long b) {        
        return (a % 65536) == (b % 65536);  // 2 ^ 16
    }

    class Generator {
        final long factor;
        long value;
        long multiple;

        Generator(long factor, long originValue, long multiple) {
            this.factor = factor;
            value = originValue;
            this.multiple = multiple;
        }

        long nextValue() {
            do {
                value = (value * factor) % 2147483647;            
            } while ((value % multiple) != 0);            
            return value;
        }
    }

    @Test
    public void unitTest() {
        assertEquals(309, totalMatchingPairs(65, 8921));
    }
}
